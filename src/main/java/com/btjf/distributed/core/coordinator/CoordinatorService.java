package com.btjf.distributed.core.coordinator;
import com.btjf.distributed.common.bean.context.DistributedTransactionContext;
import com.btjf.distributed.common.bean.entity.DistributedInvocation;
import com.btjf.distributed.common.bean.entity.DistributedTransactionInfo;
import com.btjf.distributed.common.config.Constants;
import com.btjf.distributed.common.constants.CommonConstant;
import com.btjf.distributed.common.enums.CoordinatorActionEnum;
import com.btjf.distributed.common.enums.DistributedRoleEnum;
import com.btjf.distributed.common.enums.DistributedStatusEnum;
import com.btjf.distributed.common.enums.RepositorySupportEnum;
import com.btjf.distributed.common.utils.SpringBeanUtils;
import com.btjf.distributed.core.coordinator.CoordinatorService;
import com.btjf.distributed.core.coordinator.bean.CoordinatorAction;
import com.btjf.distributed.core.spi.CoordinatorRepository;
import com.btjf.distributed.core.spi.repository.JdbcCoordinatorRepository;
import com.btjf.distributed.core.threadlocal.TransactionContextLocal;
import com.btjf.distributed.mq.bean.MessageContent;
import com.btjf.distributed.mq.producer.Producer;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.StreamSupport;

/**
 * @author luol
 * @date 2018/1/29
 * @time 16:25
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Component
public class CoordinatorService{

    private final Logger logger = LoggerFactory.getLogger(CoordinatorService.class);

    private static CoordinatorRepository coordinatorRepository = new JdbcCoordinatorRepository();

    @Resource
    private Constants constants;
    @Resource
    private Producer producer;

    private static final BlockingQueue<CoordinatorAction> QUEUE = new LinkedBlockingQueue();
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(CommonConstant.coordinatorThreadMax);

    /*@PostConstruct
    public void init() {
        //初始化spi
        initSpi();
        //初始化 协调资源线程池
        initCoordinatorPool();
    }*/

    private void initSpi() {
        //初始化spi，根据配置文件选择可用的存储方式
        ServiceLoader<CoordinatorRepository> recoverRepositories = ServiceLoader.load(CoordinatorRepository.class);

        final RepositorySupportEnum repositorySupportEnum = RepositorySupportEnum.acquire(constants.getRepositorySupportType());
        final Optional<CoordinatorRepository> repositoryOptional = StreamSupport.stream(recoverRepositories.spliterator(), false)
                .filter(recoverRepository -> Objects.equals(recoverRepository.getScheme(), repositorySupportEnum)).findFirst();
        //将CoordinatorRepository实现注入到spring容器
        repositoryOptional.ifPresent(repository -> {
            SpringBeanUtils.getInstance().registerBean(CoordinatorRepository.class.getName(), repository);
            //注入后赋值，当前类用@Resource无效
            this.coordinatorRepository = repository;
        });
    }

    private void initCoordinatorPool() {
        EXECUTOR_SERVICE.execute(() -> execute());
    }

    /**
     * 从队列中取出任务
     * */
    private void execute() {
        while (true) {
            try {
                final CoordinatorAction coordinatorAction = QUEUE.take();
                if (coordinatorAction != null) {
                    CoordinatorActionEnum coordinatorActionEnum = coordinatorAction.getCoordinatorActionEnum();
                    if (Objects.equals(CoordinatorActionEnum.SAVE, coordinatorActionEnum)) {
                        save(coordinatorAction.getDistributedTransactionInfo(), constants.getApplicationName());
                    }
                }
            } catch (Exception e) {
                logger.error("exec record log happen error,error message: " + e.getMessage(), e);
            }
        }
    }

    /**
     * 保存本地事务信息
     *
     * @param distributedTransactionInfo 实体对象
     * @return 主键 transID
     */
    public String save(DistributedTransactionInfo distributedTransactionInfo, String applicationName) {
        final int rows = coordinatorRepository.create(distributedTransactionInfo, applicationName);
        if (rows > 0) {
            return distributedTransactionInfo.getTransID();
        }
        return null;
    }

    /**
     * 根据事务id获取DistributedTransactionInfo
     *
     * @param transID 事务ID
     * @param applicationName 项目名称
     * @return DistributedTransactionInfo
     */
    public List<DistributedTransactionInfo> findByTransID(String transID, String applicationName) {
        return coordinatorRepository.findByTransID(transID, applicationName);
    }

    /**
     * 删除补偿事务信息
     *
     * @param transID 事务iD
     * @return true成功 false 失败
     */
    public boolean remove(String transID) {
        return coordinatorRepository.remove(transID) > 0;
    }

    /**
     * 更新
     *
     * @param distributedTransactionInfo 实体对象
     * @return rows 1 成功
     */
    public int update(DistributedTransactionInfo distributedTransactionInfo) {
        return coordinatorRepository.update(distributedTransactionInfo);
    }

    /**
     * 更新事务失败日志
     *
     * @param distributedTransactionInfo 实体对象
     * @return rows 1 成功
     */
    public int updateFailTransaction(DistributedTransactionInfo distributedTransactionInfo) {
        return coordinatorRepository.updateFailTransaction(distributedTransactionInfo);
    }

    /**
     * 更新 List<MythParticipant>  只更新这一个字段数据
     *
     * @param distributedTransactionInfo 实体对象
     * @return rows 1 rows 1 成功
     */
    public int updateParticipant(DistributedTransactionInfo distributedTransactionInfo) {
        return coordinatorRepository.updateParticipant(distributedTransactionInfo);
    }

    /**
     * 更新本地日志状态
     *
     * @param transID 事务ID
     * @param distributedStatusEnum  状态
     * @return rows 1 rows 1 成功
     */
    public int updateStatus(String transID, DistributedStatusEnum distributedStatusEnum) {
        return coordinatorRepository.updateStatus(transID, distributedStatusEnum.getValue());
    }

    /**
     * 提交补偿操作
     *
     * @param coordinatorAction 执行动作
     * @return true 成功
     */
    public Boolean submit(CoordinatorAction coordinatorAction) {
        try {
            QUEUE.put(coordinatorAction);
        } catch (InterruptedException e) {
            logger.error("record log to blockingQueue happen error,error message: " + e.getMessage(), e);
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * 接收到mq消息处理
     *
     * @param message 消息体
     * @return true 处理成功  false 处理失败
     */
    public Boolean processMessage(MessageContent message) {
        try {
            if (message.getRetriedCount() > constants.getRetriedCount() || !Objects.equals(message.getDistributedInvocation().getDistributedStatusEnum(), DistributedStatusEnum.PRE_FAILURE)) {
                //执行失败 保存失败的日志
                final DistributedTransactionInfo log = buildTransactionLog(message.getTransID(), "failue",
                        DistributedStatusEnum.FAILURE,
                        message.getDistributedInvocation().getTargetClass().getName(),
                        message.getDistributedInvocation().getMethodName());
                submit(new CoordinatorAction(CoordinatorActionEnum.SAVE, log));
                return Boolean.FALSE;
            }
            handlerMessage(message);
            //执行成功 保存成功的日志
            final DistributedTransactionInfo log = buildTransactionLog(message.getTransID(), "success",
                    DistributedStatusEnum.SUCCESS,
                    message.getDistributedInvocation().getTargetClass().getName(),
                    message.getDistributedInvocation().getMethodName());
            message.getDistributedInvocation().setDistributedStatusEnum(DistributedStatusEnum.SUCCESS);
            submit(new CoordinatorAction(CoordinatorActionEnum.SAVE, log));
        } catch (Throwable throwable) {
            //执行失败，设置失败原因和重试次数
            message.setRetriedCount(message.getRetriedCount() + 1);
            processMessage(message);
        } finally {
            if (TransactionContextLocal.getInstance().get() != null) {
                TransactionContextLocal.getInstance().remove();
            }
        }
        return Boolean.TRUE;
    }

    private void handlerMessage(MessageContent messageContent) throws Exception {
        //设置事务上下文，这个类会传递给远端
        DistributedTransactionContext context = new DistributedTransactionContext();
        //设置事务id
        context.setTransID(messageContent.getTransID());
        //设置为发起者角色
        context.setRole(DistributedRoleEnum.LOCAL);
        TransactionContextLocal.getInstance().set(context);
        executeLocalTransaction(messageContent.getDistributedInvocation());
    }

    private void executeLocalTransaction(DistributedInvocation distributedInvocation) throws Exception {
        if (Objects.nonNull(distributedInvocation)) {
            final Class clazz = distributedInvocation.getTargetClass();
            final String method = distributedInvocation.getMethodName();
            final Object[] args = distributedInvocation.getArgs();
            final Class[] parameterTypes = distributedInvocation.getParameterTypes();
            //如果是枚举的话，转换成枚举进行传参
            for (int i = 0; i< parameterTypes.length; i ++) {
                if (parameterTypes[i].isEnum()) {
                    args[i] = Enum.valueOf(parameterTypes[i], args[i].toString());
                }
            }
            final Object bean = SpringBeanUtils.getInstance().getBean(clazz);
            MethodUtils.invokeMethod(bean, method, args, parameterTypes);
            logger.info("distributed transaction exec success!" + distributedInvocation.getTargetClass()
                    + ":" + distributedInvocation.getMethodName());
        }
    }

    private DistributedTransactionInfo buildTransactionLog(String transID, String errorMsg, DistributedStatusEnum status, String targetClass, String targetMethod) {
        DistributedTransactionInfo logTransaction = new DistributedTransactionInfo(transID);
        logTransaction.setRetriedCount(1);
        logTransaction.setStatus(status);
        logTransaction.setErrorMsg(errorMsg);
        logTransaction.setRole(DistributedRoleEnum.PROVIDER);
        logTransaction.setTargetClass(targetClass);
        logTransaction.setTargetMethod(targetMethod);
        return logTransaction;
    }

    /**
     * 发送消息
     *
     * @param distributedTransactionContext 消息体
     * @return true 处理成功  false 处理失败
     */
    public Boolean sendMessage(DistributedTransactionContext distributedTransactionContext) {
        Map<String, List<DistributedInvocation>> keyMap = distributedTransactionContext.getMap();
        List<DistributedInvocation> keyList = distributedTransactionContext.getDistributedInvocationList();
        keyList.forEach(kk -> {
            List<DistributedInvocation> keys = keyMap.get(kk.toString());
            if (keys == null || Objects.equals(kk.getDistributedStatusEnum(), DistributedStatusEnum.PRE_FAILURE)) {
                MessageContent messageContent = new MessageContent();
                messageContent.setDistributedInvocation(kk);
                messageContent.setTransID(distributedTransactionContext.getTransID());
                producer.send(constants.getApplicationName(), constants.getApplicationName() + CommonConstant.SUBCRIBE_SUFFIX, messageContent);
            }
        });
        return Boolean.TRUE;
    }

}



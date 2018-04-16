package com.luol.transaction.common.coordinator.impl;

import com.alibaba.fastjson.JSON;
import com.lmax.disruptor.EventSink;
import com.luol.transaction.common.bean.context.NtcTransactionContext;
import com.luol.transaction.common.bean.entity.NtcInvocation;
import com.luol.transaction.common.bean.message.MessageEntity;
import com.luol.transaction.common.bean.model.NtcTransaction;
import com.luol.transaction.common.concurrent.threadlocal.TransactionContextLocal;
import com.luol.transaction.common.config.NtcConfig;
import com.luol.transaction.common.coordinator.CoordinatorService;
import com.luol.transaction.common.coordinator.NtcMqSendService;
import com.luol.transaction.common.enums.AsynchronousTypeEnums;
import com.luol.transaction.common.enums.EventTypeEnum;
import com.luol.transaction.common.enums.NtcRoleEnum;
import com.luol.transaction.common.enums.NtcStatusEnum;
import com.luol.transaction.common.enums.PatternEnum;
import com.luol.transaction.common.serializer.ObjectSerializer;
import com.luol.transaction.common.spi.CoordinatorRepository;
import com.luol.transaction.common.utils.InvokeUtils;
import com.luol.transaction.common.utils.SpringBeanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author luol
 * @date 2018/4/4
 * @time 9:12
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Service("coordinatorService")
public class CoordinatorServiceImpl implements CoordinatorService {

    private CoordinatorRepository coordinatorRepository;

    private ObjectSerializer serializer;

    @Resource
    private NtcConfig ntcConfig;

    @Resource
    private NtcMqSendService ntcMqSendService;

    private static final Logger LOGGER = LoggerFactory.getLogger(CoordinatorServiceImpl.class);

    /**
     * 启动本地补偿事务，根据配置是否进行补偿
     *
     * @throws Exception 异常
     */
    @Override
    public void start() throws Exception {
        coordinatorRepository = SpringBeanUtils.getInstance()
                .getBean(CoordinatorRepository.class);
        coordinatorRepository.init(ntcConfig.getModelName());
    }

    /**
     * 保存补偿事务信息
     *
     * @param ntcTransaction 实体对象
     * @return 主键id
     */
    @Override
    public String save(NtcTransaction ntcTransaction) {
        final int rows = coordinatorRepository.create(ntcTransaction);
        if (rows > 0) {
            return ntcTransaction.getTransID();
        }
        return null;
    }

    /**
     * 根据事务id获取NtcTransaction
     *
     * @param transID 事务id
     * @return NtcTransaction
     */
    @Override
    public NtcTransaction findByTransID(String transID) {
        return coordinatorRepository.findByTransID(transID);
    }

    /**
     * 根据id,类名，方法名获取对象
     *
     * @param transID      transID
     * @param targetClass  类名
     * @param targetMethod 方法名
     * @return NtcTransaction
     */
    @Override
    public NtcTransaction getByTransIDAndName(String transID, String targetClass, String targetMethod) {
        return coordinatorRepository.getByTransIDAndName(transID, targetClass, targetMethod);
    }

    /**
     * 删除补偿事务信息
     *
     * @param transID 事务id
     * @return true成功 false 失败
     */
    @Override
    public boolean remove(String transID) {
        return coordinatorRepository.remove(transID) > 0;
    }

    /**
     * 更新
     *
     * @param ntcTransaction 实体对象
     */
    @Override
    public void update(NtcTransaction ntcTransaction) {
        coordinatorRepository.update(ntcTransaction);
    }

    /**
     * 发送消息
     *
     * @param message
     */
    @Override
    public Boolean sendMessage(MessageEntity message) {
        try {
            ntcMqSendService.sendMessage(ntcConfig.getModelName(), serializer.serialize(message));
        } catch (Exception e) {
            LOGGER.warn("MQ发送消息异常：", e);
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
    @Override
    public Boolean processMessage(byte[] message) {
        MessageEntity messageEntity = serializer.deSerialize(message, MessageEntity.class);
        try {
            if (Objects.equals(messageEntity.getAsynchronousTypeEnums(), AsynchronousTypeEnums.INVOCATION)) {
                Boolean isFail = handlerInvocation(messageEntity.getNtcTransaction());
                return !isFail;
            } else if (Objects.equals(messageEntity.getAsynchronousTypeEnums(), AsynchronousTypeEnums.LOGS)) {
                handlerLogs(messageEntity.getNtcTransaction(), messageEntity.getEventTypeEnum());
            }
        } catch (Exception e) {
            LOGGER.warn("处理MQ消息发生异常：", e);
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * 设置序列化方式
     * @param serializer 序列化方式
     */
    @Override
    public void setSerializer(ObjectSerializer serializer) {
        this.serializer = serializer;
    }

    /**
     * 日志处理调用，逻辑不想写两遍
     *
     * @param ntcTransaction
     * @param eventTypeEnum
     */
    @Override
    public void handlerLogs(NtcTransaction ntcTransaction, EventTypeEnum eventTypeEnum) {
        if (Objects.isNull(eventTypeEnum)) {
            LOGGER.warn("======handlerLogs传入处理类型为空======" + JSON.toJSONString(ntcTransaction));
            return;
        }
        switch (eventTypeEnum) {
            case SAVE:
                LOGGER.warn("======SAVE======" + JSON.toJSONString(ntcTransaction));
                save(ntcTransaction);
                break;
            case UPDATE:
                LOGGER.warn("======UPDATE======" + JSON.toJSONString(ntcTransaction));
                update(ntcTransaction);
                break;
            case DELETE:
                LOGGER.warn("======DELETE======" + JSON.toJSONString(ntcTransaction));
                remove(ntcTransaction.getTransID());
                break;
            default:
                LOGGER.warn("======DEFAULT======" + JSON.toJSONString(ntcTransaction));
                break;
        }
    }

    /**
     * 反射处理调用
     *
     * @param transaction
     * @return 返回是否失败
     */
    @Override
    public Boolean handlerInvocation(NtcTransaction transaction) {
        if (Objects.isNull(transaction) || CollectionUtils.isEmpty(transaction.getRpcNtcInvocations())) {
            return Boolean.TRUE;
        }
        //校验事物状态，排除已成功  +++  todo 延迟队列  ---  定时查库，定时发送消息，防止遗漏
        NtcTransaction ntcTransaction = findByTransID(transaction.getTransID());
        if (Objects.nonNull(ntcTransaction) && Objects.equals(ntcTransaction.getNtcStatusEnum(), NtcStatusEnum.SUCCESS)) {
            return Boolean.FALSE;
        }
        try {
            NtcTransactionContext context = new NtcTransactionContext();
            context.setTransID(transaction.getTransID());
            Boolean bool = Objects.equals(transaction.getPatternEnum(), PatternEnum.ONLY_ROLLBACK);
            if (bool) {
                context.setNtcStatusEnum(NtcStatusEnum.CANCEL);
                context.setPatternEnum(PatternEnum.ONLY_ROLLBACK);
                TransactionContextLocal.getInstance().set(context);
            } else {
                context.setNtcStatusEnum(NtcStatusEnum.NOTIFY);
                context.setPatternEnum(PatternEnum.NOTICE_ROLLBACK);
                context.setNtcRoleEnum(NtcRoleEnum.LOCAL);
                TransactionContextLocal.getInstance().set(context);
            }
            if (!bool && transaction.getCurrentRetryCounts() > transaction.getMaxRetryCounts()) {
                transaction.setPatternEnum(PatternEnum.ONLY_ROLLBACK);
            }
            transaction.setCurrentRetryCounts(transaction.getCurrentRetryCounts() + NumberUtils.INTEGER_ONE);
            //todo 设置最大最大调用次数，不能一直调用吧
            Boolean flag = Boolean.FALSE;
            for (NtcInvocation ntcInvocation : transaction.getRpcNtcInvocations()) {
                if (Objects.equals(ntcInvocation.getIsSuccess(), bool)) {
                    try {
                        InvokeUtils.executeParticipantMethod(ntcInvocation);
                        ntcInvocation.setIsSuccess(!bool);
                    } catch (Exception e) {
                        LOGGER.warn("{} 方法执行失败！", ntcInvocation.getMethodName());
                        LOGGER.warn("失败信息：", e);
                        flag = Boolean.TRUE;
                    }
                }
            }
            if (flag) {
                //重新走MQ
                MessageEntity messageEntity = new MessageEntity();
                messageEntity.setAsynchronousTypeEnums(AsynchronousTypeEnums.INVOCATION);
                messageEntity.setNtcTransaction(transaction);
                final Boolean isSuccess = sendMessage(messageEntity);
                return !isSuccess;
            }
            return flag;
        } finally {
            //删除上下文
            TransactionContextLocal.getInstance().remove();
        }
    }

}

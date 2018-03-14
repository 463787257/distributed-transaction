package com.luol.transaction.core.service.handler;

import com.luol.transaction.annotation.Ntc;
import com.luol.transaction.common.bean.context.NtcTransactionContext;
import com.luol.transaction.common.bean.entity.NtcInvocation;
import com.luol.transaction.common.bean.model.NtcTransaction;
import com.luol.transaction.common.enums.EventTypeEnum;
import com.luol.transaction.common.enums.NtcRoleEnum;
import com.luol.transaction.common.enums.NtcStatusEnum;
import com.luol.transaction.common.enums.PatternEnum;
import com.luol.transaction.common.utils.SpringBeanUtils;
import com.luol.transaction.core.concurrent.threadlocal.TransactionContextLocal;
import com.luol.transaction.core.disruptor.publisher.NtcTransactionLogsPublisher;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author luol
 * @date 2018/3/8
 * @time 9:39
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Component
public class NtcTransactionManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(NtcTransactionManager.class);

    /**
     * 将事务信息存放在threadLocal里面
     */
    private static final ThreadLocal<NtcTransaction> CURRENT = new ThreadLocal<>();

    @Resource
    private NtcTransactionLogsPublisher ntcTransactionLogsPublisher;

    /**
     * 事物是否开启---根据当前线程中是否存在context来判断
     * */
    public Boolean isBegin() {
        return Objects.nonNull(CURRENT.get());
    }

    /**
     * 发起者开启事物
     * */
    public NtcTransaction begin(ProceedingJoinPoint point) {
        LOGGER.warn("开始执行ntc事务！start");

        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Ntc ntc = method.getAnnotation(Ntc.class);
        PatternEnum pattern = ntc.pattern();

        //构建事物对象
        final NtcTransaction ntcTransaction = buildNtcTransaction(NtcRoleEnum.START, null, pattern);

        //将事务对象保存在threadLocal中
        CURRENT.set(ntcTransaction);

        //设置ntc事务上下文，这个类会传递给远端
        NtcTransactionContext context = new NtcTransactionContext();
        context.setTransID(ntcTransaction.getTransID());
        context.setNtcStatusEnum(NtcStatusEnum.TRY_BEGIN);
        context.setPatternEnum(pattern);
        TransactionContextLocal.getInstance().set(context);

        return ntcTransaction;
    }

    private NtcTransaction buildNtcTransaction(NtcRoleEnum ntcRoleEnum, String transID, PatternEnum pattern) {
        NtcTransaction ntcTransaction;
        if (StringUtils.isNotBlank(transID)) {
            ntcTransaction = new NtcTransaction(transID);
        } else {
            ntcTransaction = new NtcTransaction();
        }
        ntcTransaction.setNtcStatusEnum(NtcStatusEnum.TRY_BEGIN);
        ntcTransaction.setNtcRoleEnum(ntcRoleEnum);
        ntcTransaction.setPatternEnum(pattern);
        return ntcTransaction;
    }

    public void cleanThreadLocal() {
        CURRENT.remove();
    }

    public NtcTransaction getCurrentTransaction() {
        return CURRENT.get();
    }

    /**
     * 走自己的cancel
     * */
    public void cancel(ProceedingJoinPoint point) {
        LOGGER.warn("开始执行cancel方法！");
        //todo 记录日志----cancel方法无论成功还是失败都记录日志
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Class<?> clazz = point.getTarget().getClass();
        //访问目标方法的参数
        Object[] args = point.getArgs();
        Class<?>[] parameterTypes = method.getParameterTypes();
        Ntc ntc = method.getAnnotation(Ntc.class);
        String cancelMethod = ntc.cancelMethod();
        if (StringUtils.isEmpty(cancelMethod)) {
            //给默认的cancel方法名称
            cancelMethod = method.getName() + "Cancel";
        }
        try {
            executeParticipantMethod(new NtcInvocation(clazz, cancelMethod, parameterTypes, args));
            LOGGER.warn("执行cancel方法成功！");
        } catch (Throwable throwable) {
            //todo cancel方法放入队列中进行重试---并记录数据库，成功后修改状态
            LOGGER.warn("{} 方法的cancel方法执行失败！", method.getName());
            LOGGER.warn("失败信息：", throwable);
        }
    }

    /**
     * 反射调用下级的cancel
     * */
    public void cancel() {
        NtcTransaction currentTransaction = getCurrentTransaction();

        //更新失败的日志信息 todo
        ntcTransactionLogsPublisher.publishEvent(currentTransaction, EventTypeEnum.UPDATE);

        //调用下级cancel todo
        if (!CollectionUtils.isEmpty(currentTransaction.getRpcNtcInvocations())) {

        }
        //更新失败的日志信息为成功 todo
        currentTransaction.setNtcStatusEnum(NtcStatusEnum.SUCCESS);
        ntcTransactionLogsPublisher.publishEvent(currentTransaction, EventTypeEnum.UPDATE);
    }

    /**
     * 发送消息
     * */
    public void sendMessage() {
        NtcTransaction currentTransaction = getCurrentTransaction();
        //当前事物不为空，且模式为优先补偿才发送消息
        if (Objects.nonNull(currentTransaction) && Objects.equals(currentTransaction.getPatternEnum(), PatternEnum.NOTICE_ROLLBACK)) {
            //todo spi加载，优先级：rocketMQ--->redis--->jdk;异步通知反射调用
        }
    }

    /*public void addAllNtcInvocations(ProceedingJoinPoint point) {
        NtcTransaction currentTransaction = getCurrentTransaction();
        if (Objects.nonNull(currentTransaction)) {
            MethodSignature signature = (MethodSignature) point.getSignature();
            Method method = signature.getMethod();
            Class<?> clazz = point.getTarget().getClass();
            //访问目标方法的参数
            Object[] args = point.getArgs();
            Class<?>[] parameterTypes = method.getParameterTypes();
            currentTransaction.addAllNtcInvocations(new NtcInvocation(clazz, method.getName(), parameterTypes, args));
        }
    }*/

    /**
     * 传入转化好的需要反射调用的model类
     * */
    private void executeParticipantMethod(NtcInvocation ntcInvocation) throws Exception {
        if (Objects.nonNull(ntcInvocation)) {
            final Class clazz = ntcInvocation.getTargetClass();
            final String method = ntcInvocation.getMethodName();
            final Object[] args = ntcInvocation.getArgs();
            final Class[] parameterTypes = ntcInvocation.getParameterTypes();
            //todo 目测枚举不用区分开
            /*if (args != null && args.length > 0) {
                for (int i = 0; i < parameterTypes.length ; i++) {
                    if (parameterTypes[i].isEnum()) {
                        args[i] = Enum.valueOf(parameterTypes[i], args[i].toString());
                    }
                }
            }*/
            final Object bean = SpringBeanUtils.getInstance().getBean(clazz);
            MethodUtils.invokeMethod(bean, method, args, parameterTypes);
        }
    }

}

package com.luol.transaction.core.service.handler;

import com.luol.transaction.annotation.Ntc;
import com.luol.transaction.common.bean.context.NtcTransactionContext;
import com.luol.transaction.common.bean.entity.NtcInvocation;
import com.luol.transaction.common.bean.model.NtcTransaction;
import com.luol.transaction.common.concurrent.threadlocal.TransactionContextLocal;
import com.luol.transaction.common.coordinator.CoordinatorService;
import com.luol.transaction.common.enums.EventTypeEnum;
import com.luol.transaction.common.enums.NtcRoleEnum;
import com.luol.transaction.common.enums.NtcStatusEnum;
import com.luol.transaction.common.enums.PatternEnum;
import com.luol.transaction.common.utils.InvokeUtils;
import com.luol.transaction.asynchronous.disruptor.invocation.publisher.NtcTransactionInvocationPublisher;
import com.luol.transaction.asynchronous.disruptor.logs.publisher.NtcTransactionLogsPublisher;
import org.apache.commons.lang3.StringUtils;
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

    @Resource
    private NtcTransactionInvocationPublisher ntcTransactionInvocationPublisher;

    @Resource
    private CoordinatorService coordinatorService;

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
        Class<?> clazz = point.getTarget().getClass();

        //构建事物对象
        final NtcTransaction ntcTransaction = buildNtcTransaction(NtcRoleEnum.START, null, pattern);

        ntcTransaction.setMaxRetryCounts(ntc.maxRetryCounts());
        ntcTransaction.setRollbackFor(ntc.rollbackFor());
        ntcTransaction.setTargetClass(clazz.getName());
        ntcTransaction.setTargetMethod(method.getName());

        //将事务对象保存在threadLocal中
        CURRENT.set(ntcTransaction);

        //设置ntc事务上下文，这个类会传递给远端
        NtcTransactionContext context = new NtcTransactionContext();
        context.setTransID(ntcTransaction.getTransID());
        context.setNtcStatusEnum(NtcStatusEnum.TRY);
        context.setPatternEnum(pattern);
        TransactionContextLocal.getInstance().set(context);

        return ntcTransaction;
    }

    public NtcTransaction buildNtcTransaction(NtcRoleEnum ntcRoleEnum, String transID, PatternEnum pattern) {
        NtcTransaction ntcTransaction;
        if (StringUtils.isNotBlank(transID)) {
            ntcTransaction = new NtcTransaction(transID);
        } else {
            ntcTransaction = new NtcTransaction();
        }
        ntcTransaction.setNtcStatusEnum(NtcStatusEnum.TRY);
        ntcTransaction.setNtcRoleEnum(ntcRoleEnum);
        ntcTransaction.setPatternEnum(pattern);
        return ntcTransaction;
    }

    public void addLogs(NtcRoleEnum ntcRoleEnum, NtcTransactionContext ntcTransactionContext, NtcStatusEnum ntcStatusEnum, EventTypeEnum eventTypeEnum) {
        NtcTransaction ntcTransaction = buildNtcTransaction(ntcRoleEnum, ntcTransactionContext.getTransID(), ntcTransactionContext.getPatternEnum());
        ntcTransaction.setNtcStatusEnum(ntcStatusEnum);
        ntcTransaction.setTargetClass(ntcTransactionContext.getTargetClass());
        ntcTransaction.setTargetMethod(ntcTransactionContext.getTargetMethod());
        ntcTransactionLogsPublisher.publishEvent(ntcTransaction, eventTypeEnum);
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
    public void cancel(ProceedingJoinPoint point) throws Exception {
        LOGGER.warn("开始执行cancel方法！");
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Class<?> clazz = point.getTarget().getClass();
        //访问目标方法的参数
        Object[] args = point.getArgs();
        Class<?>[] parameterTypes = method.getParameterTypes();

        Ntc ntc = method.getAnnotation(Ntc.class);
        String cancelMethod = ntc.cancelMethod();
        Boolean isExit = Boolean.TRUE;
        if (StringUtils.isEmpty(cancelMethod)) {
            //给默认的cancel方法名称
            cancelMethod = method.getName() + "Cancel";
            isExit = Boolean.FALSE;
        }
        try {
            InvokeUtils.executeParticipantMethod(new NtcInvocation(clazz, cancelMethod, parameterTypes, args));
            LOGGER.warn("执行cancel方法成功！");
        } catch (NoSuchMethodException e) {
            if (isExit) {
                LOGGER.warn("{} 方法的cancel方法执行失败！", method.getName());
                LOGGER.warn("失败信息：", e);
                throw e;
            }
        } catch (Throwable throwable) {
            LOGGER.warn("{} 方法的cancel方法执行失败！", method.getName());
            LOGGER.warn("失败信息：", throwable);
            throw throwable;
        }
    }

    /**
     * 反射调用下级的cancel todo 重写
     * */
    public void cancel() {

        NtcTransaction currentTransaction = getCurrentTransaction();
        if (Objects.isNull(currentTransaction)) {
            return;
        }

        //更新失败的日志信息 todo
        ntcTransactionLogsPublisher.publishEvent(currentTransaction, EventTypeEnum.UPDATE);

        //调用下级cancel todo
        if (!CollectionUtils.isEmpty(currentTransaction.getRpcNtcInvocations())) {
            currentTransaction.getRpcNtcInvocations().forEach(ntcInvocation -> {
                try {
                    if (!ntcInvocation.getIsSuccess()) {
                        InvokeUtils.executeParticipantMethod(ntcInvocation);
                        LOGGER.warn("执行cancel方法成功！");
                    }
                } catch (Exception e) {
                    LOGGER.warn("{} 方法的cancel方法执行失败！", ntcInvocation.getMethodName());
                    LOGGER.warn("失败信息：", e);
                }
            });
        }
        //更新失败的日志信息为成功 todo
        currentTransaction.setNtcStatusEnum(NtcStatusEnum.SUCCESS);
        ntcTransactionLogsPublisher.publishEvent(currentTransaction, EventTypeEnum.UPDATE);
    }

    /**
     * 发送消息
     * */
    public void asynchronous() {
        NtcTransaction currentTransaction = getCurrentTransaction();
        //当前事物不为空,并且不成功
        if (Objects.nonNull(currentTransaction)) {
            //jdk异步通知反射调用
            ntcTransactionInvocationPublisher.publishEvent(currentTransaction);
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

    public void enlistParticipant(Method method, Class clazz, Object[] arguments, Class[] args, Boolean aTrue) {
        NtcTransaction currentTransaction = getCurrentTransaction();
        if (Objects.nonNull(currentTransaction)) {
            currentTransaction.addRpcNtcInvocations(new NtcInvocation(clazz, method.getName(), args, arguments, aTrue));
        }
    }
}

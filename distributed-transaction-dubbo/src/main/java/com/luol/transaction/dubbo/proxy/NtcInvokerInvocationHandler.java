package com.luol.transaction.dubbo.proxy;

import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.proxy.InvokerInvocationHandler;
import com.luol.transaction.annotation.Ntc;
import com.luol.transaction.common.bean.context.NtcTransactionContext;
import com.luol.transaction.common.bean.model.NtcTransaction;
import com.luol.transaction.common.concurrent.threadlocal.TransactionContextLocal;
import com.luol.transaction.common.enums.EventTypeEnum;
import com.luol.transaction.common.enums.NtcRoleEnum;
import com.luol.transaction.common.enums.NtcStatusEnum;
import com.luol.transaction.common.enums.PatternEnum;
import com.luol.transaction.common.utils.DefaultValueUtils;
import com.luol.transaction.common.utils.SpringBeanUtils;
import com.luol.transaction.core.service.handler.NtcTransactionManager;
import com.luol.transaction.notify.disruptor.logs.publisher.NtcTransactionLogsPublisher;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author luol
 * @date 2018/3/28
 * @time 10:45
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public class NtcInvokerInvocationHandler extends InvokerInvocationHandler {

    private Object target;

    public NtcInvokerInvocationHandler(Invoker<?> handler) {
        super(handler);
    }

    public <T> NtcInvokerInvocationHandler(T target, Invoker<T> invoker) {
        super(invoker);
        this.target = target;

    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final Ntc ntc = method.getAnnotation(Ntc.class);
        final Class<?>[] arguments = method.getParameterTypes();
        final Class clazz = method.getDeclaringClass();
        NtcTransactionContext ntcTransactionContext = null;
        if (Objects.nonNull(ntc)) {
            ntcTransactionContext = TransactionContextLocal.getInstance().get();
            boolean flag = Objects.nonNull(ntcTransactionContext) && Objects.equals(ntcTransactionContext.getNtcRoleEnum(), NtcRoleEnum.LOCAL);
            if (flag) {
                return super.invoke(proxy, method, args);
            }
            NtcTransactionManager ntcTransactionManager = SpringBeanUtils.getInstance().getBean(NtcTransactionManager.class);
            try {
                Object invoke = super.invoke(proxy, method, args);
                ntcTransactionManager.enlistParticipant(method, clazz, args, arguments, Boolean.TRUE);
                return invoke;
            } catch (Throwable throwable) {
                ntcTransactionManager.enlistParticipant(method, clazz, args, arguments, Boolean.FALSE);
                if (Objects.isNull(ntcTransactionContext) || Objects.equals(ntcTransactionContext.getPatternEnum(), PatternEnum.ONLY_ROLLBACK)) {
                    throw throwable;
                }
                //todo 异常修改
                NtcTransaction ntcTransaction = ntcTransactionManager.getCurrentTransaction();
                if (Objects.nonNull(ntcTransaction.getRollbackFor()) && ntcTransaction.getRollbackFor().length > 0) {
                    for (Class<? extends Throwable> claz : ntcTransaction.getRollbackFor()) {
                        if (Objects.equals(claz, throwable.getClass())) {
                            ntcTransaction.setPatternEnum(PatternEnum.ONLY_ROLLBACK);
                            if (Objects.nonNull(ntcTransactionContext)) {
                                ntcTransactionContext.setPatternEnum(PatternEnum.ONLY_ROLLBACK);
                            }
                            throw throwable;
                        }
                    }
                }
                NtcTransactionLogsPublisher ntcTransactionLogsPublisher = SpringBeanUtils.getInstance().getBean(NtcTransactionLogsPublisher.class);
                ntcTransaction.setNtcStatusEnum(NtcStatusEnum.NOTIFY);
                ntcTransactionLogsPublisher.publishEvent(ntcTransaction, EventTypeEnum.UPDATE);
                return DefaultValueUtils.getDefaultValue(method.getReturnType());
            }
        } else {
            return super.invoke(proxy, method, args);
        }
    }
}

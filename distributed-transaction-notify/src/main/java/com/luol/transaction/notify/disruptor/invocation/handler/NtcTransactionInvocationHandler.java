package com.luol.transaction.notify.disruptor.invocation.handler;

import com.lmax.disruptor.EventHandler;
import com.luol.transaction.common.bean.context.NtcTransactionContext;
import com.luol.transaction.common.bean.entity.NtcInvocation;
import com.luol.transaction.common.bean.model.NtcTransaction;
import com.luol.transaction.common.concurrent.threadlocal.TransactionContextLocal;
import com.luol.transaction.common.enums.NtcRoleEnum;
import com.luol.transaction.common.enums.NtcStatusEnum;
import com.luol.transaction.common.enums.PatternEnum;
import com.luol.transaction.common.utils.InvokeUtils;
import com.luol.transaction.common.utils.SpringBeanUtils;
import com.luol.transaction.notify.disruptor.invocation.event.NtcTransactionInvocation;
import com.luol.transaction.notify.disruptor.invocation.publisher.NtcTransactionInvocationPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Objects;

/**
 * @author luol
 * @date 2018/3/27
 * @time 9:57
 * @function 功能：todo 对象 NtcTransactionInvocationPublisher --- NtcTransactionInvocationHandler 间相互引用了
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Component
public class NtcTransactionInvocationHandler implements EventHandler<NtcTransactionInvocation> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NtcTransactionInvocationHandler.class);

    private NtcTransactionInvocationPublisher ntcTransactionInvocationPublisher;

    @Override
    public void onEvent(NtcTransactionInvocation ntcTransactionInvocation, long l, boolean b) throws Exception {
        try {
            if (!CollectionUtils.isEmpty(ntcTransactionInvocation.getRpcNtcInvocations())) {
                NtcTransactionContext context = new NtcTransactionContext();
                context.setTransID(ntcTransactionInvocation.getTransID());
                Boolean bool = Objects.equals(ntcTransactionInvocation.getPatternEnum(), PatternEnum.ONLY_ROLLBACK);
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
                LOGGER.warn("{} 模式开始执行!", ntcTransactionInvocation.getPatternEnum().getContent());
                Boolean flag = Boolean.FALSE;
                for (NtcInvocation ntcInvocation : ntcTransactionInvocation.getRpcNtcInvocations()) {
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
                    //重新排队，重复调用，并记录当前调用几次 todo
                    if (Objects.isNull(ntcTransactionInvocationPublisher)) {
                        final NtcTransactionInvocationPublisher ntcTransactionInvocationPublisher = SpringBeanUtils.getInstance().getBean(NtcTransactionInvocationPublisher.class);
                        this.ntcTransactionInvocationPublisher = ntcTransactionInvocationPublisher;
                    }
                    NtcTransaction ntcTransaction = new NtcTransaction();
                    if (!bool && ntcTransactionInvocation.getCurrentRetryCounts() > ntcTransactionInvocation.getMaxRetryCounts()) {
                        ntcTransaction.setPatternEnum(PatternEnum.ONLY_ROLLBACK);
                    } else {
                        ntcTransaction.setPatternEnum(ntcTransactionInvocation.getPatternEnum());
                    }
                    ntcTransaction.setRpcNtcInvocations(ntcTransactionInvocation.getRpcNtcInvocations());
                    ntcTransaction.setCurrentRetryCounts(ntcTransactionInvocation.getCurrentRetryCounts());
                    ntcTransactionInvocationPublisher.publishEvent(ntcTransaction);
                }
                LOGGER.warn("{} 模式执行结束!", ntcTransactionInvocation.getPatternEnum().getContent());
            }
        } finally {
            //删除上下文
            TransactionContextLocal.getInstance().remove();
        }
    }

}

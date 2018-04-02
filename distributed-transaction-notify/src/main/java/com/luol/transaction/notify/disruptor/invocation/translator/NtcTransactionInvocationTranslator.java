package com.luol.transaction.notify.disruptor.invocation.translator;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.luol.transaction.common.bean.model.NtcTransaction;
import com.luol.transaction.notify.disruptor.invocation.event.NtcTransactionInvocation;


/**
 * @author luol
 * @date 2018/3/27
 * @time 9:57
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public class NtcTransactionInvocationTranslator implements EventTranslatorOneArg<NtcTransactionInvocation, NtcTransaction> {
    @Override
    public void translateTo(NtcTransactionInvocation ntcTransactionInvocation, long l, NtcTransaction ntcTransaction) {
        ntcTransactionInvocation.setTransID(ntcTransaction.getTransID());
        ntcTransactionInvocation.setPatternEnum(ntcTransaction.getPatternEnum());
        ntcTransactionInvocation.setRpcNtcInvocations(ntcTransaction.getRpcNtcInvocations());
        ntcTransactionInvocation.setMaxRetryCounts(ntcTransaction.getMaxRetryCounts());
        ntcTransactionInvocation.setCurrentRetryCounts(ntcTransaction.getCurrentRetryCounts());
    }
}

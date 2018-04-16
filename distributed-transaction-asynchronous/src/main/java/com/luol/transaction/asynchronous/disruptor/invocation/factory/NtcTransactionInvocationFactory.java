package com.luol.transaction.asynchronous.disruptor.invocation.factory;

import com.lmax.disruptor.EventFactory;
import com.luol.transaction.asynchronous.disruptor.invocation.event.NtcTransactionInvocation;

/**
 * @author luol
 * @date 2018/3/27
 * @time 9:57
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public class NtcTransactionInvocationFactory implements EventFactory<NtcTransactionInvocation> {
    @Override
    public NtcTransactionInvocation newInstance() {
        return new NtcTransactionInvocation();
    }
}

package com.luol.transaction.core.disruptor.factory;

import com.lmax.disruptor.EventFactory;
import com.luol.transaction.core.disruptor.event.NtcTransactionLogs;

/**
 * @author luol
 * @date 2018/3/12
 * @time 10:07
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public class NtcTransactionLogsFactory implements EventFactory<NtcTransactionLogs> {

    @Override
    public NtcTransactionLogs newInstance() {
        return new NtcTransactionLogs();
    }
}

package com.luol.transaction.core.disruptor.handler;

import com.lmax.disruptor.EventHandler;
import com.luol.transaction.core.disruptor.event.NtcTransactionLogs;
import org.springframework.stereotype.Component;

/**
 * @author luol
 * @date 2018/3/12
 * @time 10:07
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Component
public class NtcTransactionLogsHandler implements EventHandler<NtcTransactionLogs> {


    @Override
    public void onEvent(NtcTransactionLogs ntcTransactionLogs,
                        long sequence, boolean endOfBatch)
            throws Exception {
        switch (ntcTransactionLogs.getEventTypeEnum()) {
            case SAVE:
                break;
            case DELETE:
                break;
        }
        ntcTransactionLogs.clear();
    }
}

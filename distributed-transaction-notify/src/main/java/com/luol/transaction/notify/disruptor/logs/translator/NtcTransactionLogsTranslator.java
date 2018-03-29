package com.luol.transaction.notify.disruptor.logs.translator;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.luol.transaction.common.bean.model.NtcTransaction;
import com.luol.transaction.common.enums.EventTypeEnum;
import com.luol.transaction.notify.disruptor.logs.event.NtcTransactionLogs;

/**
 * @author luol
 * @date 2018/3/12
 * @time 10:07
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public class NtcTransactionLogsTranslator implements EventTranslatorOneArg<NtcTransactionLogs, NtcTransaction> {

    private EventTypeEnum eventTypeEnum;

    public NtcTransactionLogsTranslator(EventTypeEnum eventTypeEnum) {
        this.eventTypeEnum = eventTypeEnum;
    }

    @Override
    public void translateTo(NtcTransactionLogs ntcTransactionLogs, long l,
                            NtcTransaction NtcTransaction) {
        ntcTransactionLogs.setNtcTransaction(NtcTransaction);
        ntcTransactionLogs.setEventTypeEnum(eventTypeEnum);
    }
}

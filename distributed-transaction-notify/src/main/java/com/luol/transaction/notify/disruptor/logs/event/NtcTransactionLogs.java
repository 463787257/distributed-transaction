package com.luol.transaction.notify.disruptor.logs.event;

import com.luol.transaction.common.bean.model.NtcTransaction;
import com.luol.transaction.common.enums.EventTypeEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * @author luol
 * @date 2018/3/12
 * @time 10:07
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Data
public class NtcTransactionLogs implements Serializable {
    private static final long serialVersionUID = 2214661747971384962L;

    private NtcTransaction ntcTransaction;

    private EventTypeEnum eventTypeEnum;

    public void clear() {
        ntcTransaction = null;
    }
}

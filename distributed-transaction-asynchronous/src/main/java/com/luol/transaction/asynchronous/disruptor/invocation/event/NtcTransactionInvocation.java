package com.luol.transaction.asynchronous.disruptor.invocation.event;

import com.luol.transaction.common.bean.model.NtcTransaction;
import lombok.Data;

import java.io.Serializable;

/**
 * @author luol
 * @date 2018/3/27
 * @time 9:56
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Data
public class NtcTransactionInvocation implements Serializable {

    private static final long serialVersionUID = -4625787946912880404L;

    private NtcTransaction ntcTransaction;

    public void clear() {
        ntcTransaction = null;
    }
}

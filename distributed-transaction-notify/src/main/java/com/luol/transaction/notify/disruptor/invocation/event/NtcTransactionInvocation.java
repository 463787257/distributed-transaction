package com.luol.transaction.notify.disruptor.invocation.event;

import com.luol.transaction.common.bean.entity.NtcInvocation;
import com.luol.transaction.common.enums.PatternEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

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

    /**
     * 事物ID
     * */
    private String transID;

    /**
     * 事物类型
     * */
    private PatternEnum patternEnum;

    /**
     * 通知+回滚模式---最大重试次数
     * */
    private Integer maxRetryCounts;

    /**
     * 通知+回滚模式---当前重试次数
     * */
    private Integer currentRetryCounts;

    /**
     * RPC调用ntc方法集合
     * */
    private List<NtcInvocation> rpcNtcInvocations;
}

package com.luol.transaction.common.bean.model;

import com.google.common.collect.Lists;
import com.luol.transaction.common.bean.entity.NtcInvocation;
import com.luol.transaction.common.enums.NtcRoleEnum;
import com.luol.transaction.common.enums.NtcStatusEnum;
import com.luol.transaction.common.enums.PatternEnum;
import com.luol.transaction.common.utils.IDWorkerUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author luol
 * @date 2018/3/7
 * @time 16:50
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Data
@AllArgsConstructor
public class NtcTransaction implements Serializable {

    private static final long serialVersionUID = -4566175711273417877L;

    /**
     * 事务id
     */
    private String transID;

    /**
     * 事物进行阶段
     * */
    private NtcStatusEnum ntcStatusEnum;

    /**
     * 执行角色
     * */
    private NtcRoleEnum ntcRoleEnum;

    /**
     * 事物类型
     * */
    private PatternEnum patternEnum;

    /**
     * 通知+回滚模式---最大重试次数
     * */
    private Integer maxRetryCounts;

    /**
     * 通知+回滚模式---异常直接回滚集合
     * */
    private Class<? extends Throwable>[] rollbackFor;

    /**
     * RPC调用ntc方法集合
     * */
    private List<NtcInvocation> rpcNtcInvocations;

    public NtcTransaction() {
        this.transID = IDWorkerUtils.getInstance().createUUID();
        rpcNtcInvocations = Lists.newCopyOnWriteArrayList();
    }

    public NtcTransaction(String transID) {
        this.transID = transID;
        rpcNtcInvocations = Lists.newCopyOnWriteArrayList();
    }

    /**
     * 代理中调用
     * */
    public void addRpcNtcInvocations(NtcInvocation ntcInvocation) {
        rpcNtcInvocations.add(ntcInvocation);
    }

}

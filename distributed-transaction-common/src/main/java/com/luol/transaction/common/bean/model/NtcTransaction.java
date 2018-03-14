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
     * RPC调用ntc方法集合
     * */
    private List<NtcInvocation> rpcNtcInvocations;

    /**
     * 所有调用ntc方法集合 todo 好像没啥用处
     * */
    //private List<NtcInvocation> allNtcInvocations;

    public NtcTransaction() {
        this.transID = IDWorkerUtils.getInstance().createUUID();
        rpcNtcInvocations = Lists.newCopyOnWriteArrayList();
        //allNtcInvocations = Lists.newCopyOnWriteArrayList();
    }

    public NtcTransaction(String transID) {
        this.transID = transID;
        rpcNtcInvocations = Lists.newCopyOnWriteArrayList();
        //allNtcInvocations = Lists.newCopyOnWriteArrayList();
    }

    /**
     * 代理中调用
     * */
    public void addRpcNtcInvocations(NtcInvocation ntcInvocation) {
        rpcNtcInvocations.add(ntcInvocation);
    }

    /**
     * aop中调用
     * */
    /*public void addAllNtcInvocations(NtcInvocation ntcInvocation) {
        allNtcInvocations.add(ntcInvocation);
    }*/

}

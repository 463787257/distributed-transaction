package com.luol.transaction.common.bean.model;

import com.google.common.collect.Lists;
import com.luol.transaction.common.bean.entity.NtcInvocation;
import com.luol.transaction.common.enums.NtcRoleEnum;
import com.luol.transaction.common.enums.NtcStatusEnum;
import com.luol.transaction.common.enums.PatternEnum;
import com.luol.transaction.common.exception.NtcException;
import com.luol.transaction.common.utils.IDGenUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
     * 通知+回滚模式---当前重试次数
     * */
    private Integer currentRetryCounts;

    /**
     * 通知+回滚模式---异常直接回滚集合
     * */
    private Class<? extends Throwable>[] rollbackFor;

    /**
     * RPC调用ntc方法集合
     * */
    private List<NtcInvocation> rpcNtcInvocations;

    /**
     * 调用接口名称
     */
    private String targetClass;

    /**
     * 调用方法名称
     */
    private String targetMethod;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 版本号
     */
    private Integer version = 1;

    /**
     * 更新时间
     */
    private Date lastTime;

    public NtcTransaction() {
        this.transID = IDGenUtils.get().createID();
        rpcNtcInvocations = Lists.newCopyOnWriteArrayList();
        currentRetryCounts = NumberUtils.INTEGER_ZERO;
        this.createTime = new Date();
        this.lastTime = new Date();
    }

    public NtcTransaction(String transID) {
        this.transID = transID;
        rpcNtcInvocations = Lists.newCopyOnWriteArrayList();
        currentRetryCounts = NumberUtils.INTEGER_ZERO;
        this.createTime = new Date();
        this.lastTime = new Date();
    }

    /**
     * 代理中调用
     * */
    public void addRpcNtcInvocations(NtcInvocation ntcInvocation) {
        rpcNtcInvocations.add(ntcInvocation);
    }

}

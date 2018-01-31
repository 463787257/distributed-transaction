package com.btjf.distributed.common.bean.entity;

import com.btjf.distributed.common.enums.DistributedRoleEnum;
import com.btjf.distributed.common.enums.DistributedStatusEnum;
import com.btjf.distributed.common.utils.IdWorkerUtils;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author luol
 * @date 2018/1/29
 * @time 15:35
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Data
public class DistributedTransactionInfo implements Serializable{
    private static final long serialVersionUID = 2897425002350834918L;

    /**
     * 事务id
     */
    private String transID;

    /**
     * 事务状态
     */
    private DistributedStatusEnum status;

    /**
     * 事务类型
     */
    private DistributedRoleEnum role;

    /**
     * 重试次数
     */
    private volatile int retriedCount = 0;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date lastTime;

    /**
     * 版本号 乐观锁控制
     */
    private Integer version = 1;


    /**
     * 调用接口名称
     */
    private String targetClass;


    /**
     * 调用方法名称
     */
    private String targetMethod;

    /**
     * 调用错误信息
     */
    private String errorMsg;

    /**
     * 参与协调的方法集合
     */
    private Set<DistributedParticipant> distributedParticipants;


    public DistributedTransactionInfo() {
        this.transID = IdWorkerUtils.getInstance().createUUID();
        this.createTime = new Date();
        this.lastTime = new Date();
        distributedParticipants = new ConcurrentSkipListSet();
    }

    public DistributedTransactionInfo(String transID) {
        this.transID = transID;
        this.createTime = new Date();
        this.lastTime = new Date();
        distributedParticipants = new ConcurrentSkipListSet();
    }

    public void registerParticipant(DistributedParticipant distributedParticipant) {
        distributedParticipants.add(distributedParticipant);
    }

    public String getTransID() {
        return transID;
    }

    public void setTransID(String transID) {
        this.transID = transID;
    }

    public DistributedStatusEnum getStatus() {
        return status;
    }

    public void setStatus(Object status) {
        if (status instanceof Integer) {
            this.status = DistributedStatusEnum.acquireByValue((Integer)status);
        } else if (status instanceof DistributedStatusEnum) {
            this.status = (DistributedStatusEnum)status;
        }
    }

    public DistributedRoleEnum getRole() {
        return role;
    }

    public void setRole(Object role) {
        if (role instanceof Integer) {
            this.role = DistributedRoleEnum.acquireByValue((Integer)role);
        } else if (role instanceof DistributedRoleEnum) {
            this.role = (DistributedRoleEnum)role;
        }
    }

    public int getRetriedCount() {
        return retriedCount;
    }

    public void setRetriedCount(int retriedCount) {
        this.retriedCount = retriedCount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(String targetClass) {
        this.targetClass = targetClass;
    }

    public String getTargetMethod() {
        return targetMethod;
    }

    public void setTargetMethod(String targetMethod) {
        this.targetMethod = targetMethod;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Set<DistributedParticipant> getDistributedParticipants() {
        return distributedParticipants;
    }

    public void setDistributedParticipants(Set<DistributedParticipant> distributedParticipants) {
        this.distributedParticipants = distributedParticipants;
    }
}

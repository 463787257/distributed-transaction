package com.luol.transaction.common.bean.context;

import com.luol.transaction.common.enums.NtcRoleEnum;
import com.luol.transaction.common.enums.NtcStatusEnum;
import com.luol.transaction.common.enums.PatternEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author luol
 * @date 2018/3/7
 * @time 16:33
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@NoArgsConstructor
public class NtcTransactionContext implements Serializable {
    private static final long serialVersionUID = -797132718768582477L;

    /**
     * 事物ID
     * */
    private String transID;

    /**
     * 执行状态
     * */
    private NtcStatusEnum ntcStatusEnum;

    /**
     * 模式
     * */
    private PatternEnum patternEnum;

    /**
     * 执行角色
     * */
    private NtcRoleEnum ntcRoleEnum;

    public String getTransID() {
        return transID;
    }

    public void setTransID(String transID) {
        this.transID = transID;
    }

    public NtcStatusEnum getNtcStatusEnum() {
        return ntcStatusEnum;
    }

    public void setNtcStatusEnum(Object ntcStatusEnum) {
        if (Objects.nonNull(ntcStatusEnum)) {
            if (ntcStatusEnum instanceof NtcStatusEnum) {
                this.ntcStatusEnum = (NtcStatusEnum)ntcStatusEnum;
            } else {
                this.ntcStatusEnum = NtcStatusEnum.objectOf(ntcStatusEnum);
            }
        }
    }

    public PatternEnum getPatternEnum() {
        return patternEnum;
    }

    public void setPatternEnum(Object patternEnum) {
        if (Objects.nonNull(patternEnum)) {
            if (patternEnum instanceof PatternEnum) {
                this.patternEnum = (PatternEnum)patternEnum;
            } else {
                this.patternEnum = PatternEnum.objectOf(patternEnum);
            }
        }
    }

    public NtcRoleEnum getNtcRoleEnum() {
        return ntcRoleEnum;
    }

    public void setNtcRoleEnum(Object ntcRoleEnum) {
        if (Objects.nonNull(ntcRoleEnum)) {
            if (ntcRoleEnum instanceof NtcRoleEnum) {
                this.ntcRoleEnum = (NtcRoleEnum)ntcRoleEnum;
            } else if (ntcRoleEnum instanceof String) {
                this.ntcRoleEnum = NtcRoleEnum.objectOf(ntcRoleEnum);
            }
        }
    }
}

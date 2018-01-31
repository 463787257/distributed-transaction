package com.btjf.distributed.common.bean.entity;

import com.btjf.distributed.common.enums.DistributedStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author luol
 * @date 2018/1/29
 * @time 15:54
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Data
@AllArgsConstructor
public class DistributedInvocation implements Serializable {

    private static final long serialVersionUID = -310875026192214992L;

    /**
     * 类名
     * */
    private Class targetClass;

    /**
     * 方法名
     * */
    private String methodName;

    /**
     * 对应的事物状态
     * */
    private DistributedStatusEnum distributedStatusEnum;

    /**
     * 参数
     * */
    private Class[] parameterTypes;

    /**
     * 参数
     * */
    private Object[] args;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DistributedInvocation that = (DistributedInvocation) o;

        if (targetClass != null ? !targetClass.equals(that.targetClass) : that.targetClass != null) return false;
        if (methodName != null ? !methodName.equals(that.methodName) : that.methodName != null) return false;
        if (!Arrays.equals(parameterTypes, that.parameterTypes)) return false;
        return Arrays.equals(args, that.args);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (targetClass != null ? targetClass.hashCode() : 0);
        result = 31 * result + (methodName != null ? methodName.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(parameterTypes);
        result = 31 * result + Arrays.hashCode(args);
        return result;
    }

    @Override
    public String toString() {
        return "DistributedInvocation{" +
                "targetClass=" + targetClass +
                ", methodName='" + methodName + '\'' +
                ", parameterTypes=" + Arrays.toString(parameterTypes) +
                ", args=" + Arrays.toString(args) +
                '}';
    }
}

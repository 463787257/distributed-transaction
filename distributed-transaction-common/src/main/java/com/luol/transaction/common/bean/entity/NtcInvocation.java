package com.luol.transaction.common.bean.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

/**
 * @author luol
 * @date 2018/3/7
 * @time 16:46
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NtcInvocation implements Serializable {

    private static final long serialVersionUID = 5512498387154936754L;

    /**
     * 类名
     * */
    private Class targetClass;

    /**
     * 方法名
     * */
    private String methodName;

    /**
     * 参数类型
     * */
    private Class[] parameterTypes;

    /**
     * 参数
     * */
    private Object[] args;

    /**
     * 是否成功
     * */
    private Boolean isSuccess;

    public NtcInvocation(Class<?> clazz, String cancelMethod, Class<?>[] parameterTypes, Object[] args) {
        this.targetClass = clazz;
        this.methodName = cancelMethod;
        this.parameterTypes = parameterTypes;
        this.args = args;
    }
}

package com.luol.transaction.common.utils;

import com.luol.transaction.common.bean.entity.NtcInvocation;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.util.Objects;

/**
 * @author luol
 * @date 2018/3/27
 * @time 13:44
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public class InvokeUtils {

    /**
     * 传入转化好的需要反射调用的model类
     * */
    public static void executeParticipantMethod(NtcInvocation ntcInvocation) throws Exception {
        if (Objects.nonNull(ntcInvocation)) {
            final Class clazz = ntcInvocation.getTargetClass();
            final String method = ntcInvocation.getMethodName();
            final Object[] args = ntcInvocation.getArgs();
            final Class[] parameterTypes = ntcInvocation.getParameterTypes();
            final Object bean = SpringBeanUtils.getInstance().getBean(clazz);
            MethodUtils.invokeMethod(bean, method, args, parameterTypes);
        }
    }

}

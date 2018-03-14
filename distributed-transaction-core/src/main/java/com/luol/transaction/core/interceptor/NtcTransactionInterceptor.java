package com.luol.transaction.core.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author luol
 * @date 2018/3/7
 * @time 17:06
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@FunctionalInterface
public interface NtcTransactionInterceptor {

    /**
     * 分布式事务拦截方法
     *
     * @param pjp 切入点
     * @return Object
     * @throws Throwable 异常
     */
    Object interceptor(ProceedingJoinPoint pjp) throws Throwable;

}

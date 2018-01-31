package com.btjf.distributed.core.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author luol
 * @date 2018/1/29
 * @time 14:05
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@FunctionalInterface
public interface DistributedTransactionInterceptor {

    /**
     * 分布式事务拦截方法
     *
     * @param pjp 切入点
     * @return Object
     * @throws Throwable 异常
     */
    Object interceptor(ProceedingJoinPoint pjp) throws Throwable;

}

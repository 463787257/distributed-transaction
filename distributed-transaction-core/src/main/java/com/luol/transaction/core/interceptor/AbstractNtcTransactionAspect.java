package com.luol.transaction.core.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @author luol
 * @date 2018/3/7
 * @time 17:06
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Aspect
public class AbstractNtcTransactionAspect {

    private NtcTransactionInterceptor ntcTransactionInterceptor;

    public AbstractNtcTransactionAspect(NtcTransactionInterceptor ntcTransactionInterceptor) {
        this.ntcTransactionInterceptor = ntcTransactionInterceptor;
    }

    @Pointcut("@annotation(com.luol.transaction.annotation.Ntc)")
    public void ntcTransactionInterceptor() {}

    @Around("ntcTransactionInterceptor()")
    public Object interceptCompensableMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return ntcTransactionInterceptor.interceptor(proceedingJoinPoint);
    }

}

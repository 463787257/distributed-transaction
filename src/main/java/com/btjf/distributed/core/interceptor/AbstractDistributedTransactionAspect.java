package com.btjf.distributed.core.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import javax.annotation.Resource;

/**
 * @author luol
 * @date 2018/1/29
 * @time 14:04
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Aspect
public abstract class AbstractDistributedTransactionAspect {

    private DistributedTransactionInterceptor distributedTransactionInterceptor;

    public void setDistributedTransactionInterceptor(DistributedTransactionInterceptor distributedTransactionInterceptor) {
        this.distributedTransactionInterceptor = distributedTransactionInterceptor;
    }

    @Pointcut("@annotation(com.btjf.distributed.annotation.DistributedTransaction)")
    public void distributedTransactionInterceptor() {

    }

    @Around("distributedTransactionInterceptor()")
    public Object interceptCompensableMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return distributedTransactionInterceptor.interceptor(proceedingJoinPoint);
    }

    /**
     * spring Order 接口，该值的返回直接会影响springBean的加载顺序
     *
     * @return int 类型
     */
    public abstract int getOrder();
}

package com.btjf.distributed.core.service;

import com.btjf.distributed.common.bean.context.DistributedTransactionContext;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author luol
 * @date 2018/1/29
 * @time 14:20
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@FunctionalInterface
public interface DistributedTransactionAspectService {

    /**
     * distributed事务切面服务
     *
     * @param distributedTransactionContext distributed事务上下文对象
     * @param point                 切点
     * @return object
     * @throws Throwable 异常信息
     */
    Object invoke(DistributedTransactionContext distributedTransactionContext, ProceedingJoinPoint point) throws Throwable;

}

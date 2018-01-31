package com.btjf.distributed.core.service;

import com.btjf.distributed.common.bean.context.DistributedTransactionContext;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author luol
 * @date 2018/1/29
 * @time 14:33
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@FunctionalInterface
public interface DistributedTransactionHandler {

    /**
     * DistributedTransaction分布式事务处理接口
     *
     * @param point                  point 切点
     * @param distributedTransactionContext DistributedTransaction事务上下文
     * @return Object
     * @throws Throwable 异常
     */
    Object handler(ProceedingJoinPoint point, DistributedTransactionContext distributedTransactionContext) throws Throwable;

}

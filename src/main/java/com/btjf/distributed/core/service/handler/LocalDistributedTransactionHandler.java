package com.btjf.distributed.core.service.handler;

import com.btjf.distributed.common.bean.context.DistributedTransactionContext;
import com.btjf.distributed.core.service.DistributedTransactionHandler;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

/**
 * @author luol
 * @date 2018/1/29
 * @time 15:09
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Component
public class LocalDistributedTransactionHandler implements DistributedTransactionHandler {
    /**
     * DistributedTransaction分布式事务处理接口
     *
     * @param point                         point 切点
     * @param distributedTransactionContext DistributedTransaction事务上下文
     * @return Object
     * @throws Throwable 异常
     */
    @Override
    public Object handler(ProceedingJoinPoint point, DistributedTransactionContext distributedTransactionContext) throws Throwable {
        return point.proceed();
    }
}

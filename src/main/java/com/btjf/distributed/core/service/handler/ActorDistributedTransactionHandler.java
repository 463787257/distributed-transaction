package com.btjf.distributed.core.service.handler;

import com.btjf.distributed.common.bean.context.DistributedTransactionContext;
import com.btjf.distributed.core.service.DistributedTransactionHandler;
import com.btjf.distributed.core.service.impl.DistributedTransactionManager;
import com.btjf.distributed.core.threadlocal.TransactionContextLocal;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author luol
 * @date 2018/1/29
 * @time 15:09
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Component
public class ActorDistributedTransactionHandler implements DistributedTransactionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ActorDistributedTransactionHandler.class);

    @Resource
    private DistributedTransactionManager distributedTransactionManager;

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
        try {
            //先保存事务日志
            distributedTransactionManager.actorTransaction(point, distributedTransactionContext);
            //发起调用 执行try方法
            final Object proceed = point.proceed();
            //执行成功 更新状态为commit
            distributedTransactionManager.commitStatus(point);
            return proceed;
        } catch (Throwable throwable) {
            logger.error("exec DistributedTransaction fail,transID: " + distributedTransactionContext.getTransID());
            distributedTransactionManager.failTransaction(point, throwable.getMessage());
            throw throwable;
        } finally {
            TransactionContextLocal.getInstance().remove();
        }
    }
}

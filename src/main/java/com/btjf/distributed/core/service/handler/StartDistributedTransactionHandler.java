package com.btjf.distributed.core.service.handler;

import com.btjf.distributed.common.bean.context.DistributedTransactionContext;
import com.btjf.distributed.core.service.DistributedTransactionHandler;
import com.btjf.distributed.core.service.impl.DistributedTransactionManager;
import com.btjf.distributed.core.threadlocal.TransactionContextLocal;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author luol
 * @date 2018/1/29
 * @time 15:08
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Component
public class StartDistributedTransactionHandler implements DistributedTransactionHandler {

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
            distributedTransactionManager.begin(point);
            Object proceed = point.proceed();
            distributedTransactionManager.commitStatus(point);
            return proceed;
        } catch (Throwable throwable) {
            //更新失败的日志信息
            distributedTransactionManager.failTransaction(point, throwable.getMessage());
            throw throwable;
        } finally {
            //事物更新成commit状态，除非上面try里面的commit也失败了
            distributedTransactionManager.successTransaction();
            //发送消息
            distributedTransactionManager.sendMessage();
            distributedTransactionManager.cleanThreadLocal();
            TransactionContextLocal.getInstance().remove();
        }
    }
}

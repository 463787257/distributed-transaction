package com.btjf.distributed.core.service;

import com.btjf.distributed.common.bean.context.DistributedTransactionContext;

/**
 * @author luol
 * @date 2018/1/29
 * @time 14:29
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@FunctionalInterface
public interface DistributedTransactionFactoryService<T> {

    /**
     * 返回 实现TxTransactionHandler类的名称
     *
     * @param context 事务上下文
     * @return Class<T>
     * @throws Throwable 抛出异常
     */
    Class<T> factoryOf(DistributedTransactionContext context) throws Throwable;

}

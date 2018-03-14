package com.luol.transaction.core.service;

import com.luol.transaction.common.bean.context.NtcTransactionContext;

/**
 * @author luol
 * @date 2018/3/7
 * @time 17:37
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@FunctionalInterface
public interface NtcTransactionFactoryService<T> {

    /**
     * 返回 实现TxTransactionHandler类的名称
     *
     * @param context
     * @return Class<T>
     * @throws Throwable 抛出异常
     */
    Class<T> factoryOf(NtcTransactionContext context) throws Throwable;

}

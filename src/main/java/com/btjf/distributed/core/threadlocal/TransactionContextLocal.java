package com.btjf.distributed.core.threadlocal;

import com.btjf.distributed.common.bean.context.DistributedTransactionContext;

/**
 * @author luol
 * @date 2018/1/29
 * @time 13:48
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public class TransactionContextLocal {

    private static final ThreadLocal<DistributedTransactionContext> CURRENT_LOCAL = new ThreadLocal<>();

    private static final TransactionContextLocal TRANSACTION_CONTEXT_LOCAL = new TransactionContextLocal();

    private TransactionContextLocal() {

    }

    public static TransactionContextLocal getInstance() {
        return TRANSACTION_CONTEXT_LOCAL;
    }


    public void set(DistributedTransactionContext context) {
        CURRENT_LOCAL.set(context);
    }

    public DistributedTransactionContext get() {
        return CURRENT_LOCAL.get();
    }

    public void remove() {
        CURRENT_LOCAL.remove();
    }

}

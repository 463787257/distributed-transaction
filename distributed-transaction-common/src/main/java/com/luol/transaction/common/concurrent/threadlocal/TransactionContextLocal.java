package com.luol.transaction.common.concurrent.threadlocal;

import com.luol.transaction.common.bean.context.NtcTransactionContext;

/**
 * @author luol
 * @date 2018/3/27
 * @time 14:15
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public class TransactionContextLocal {
    private static final ThreadLocal<NtcTransactionContext> CURRENT_LOCAL = new ThreadLocal<>();

    private static final TransactionContextLocal TRANSACTION_CONTEXT_LOCAL = new TransactionContextLocal();

    private TransactionContextLocal() {

    }

    public static TransactionContextLocal getInstance() {
        return TRANSACTION_CONTEXT_LOCAL;
    }


    public void set(NtcTransactionContext context) {
        CURRENT_LOCAL.set(context);
    }

    public NtcTransactionContext get() {
        return CURRENT_LOCAL.get();
    }

    public void remove() {
        CURRENT_LOCAL.remove();
    }
}

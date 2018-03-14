package com.luol.transaction.core.service;

import com.luol.transaction.common.bean.context.NtcTransactionContext;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author luol
 * @date 2018/3/7
 * @time 17:23
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@FunctionalInterface
public interface NtcTransactionAspectService {
    /**
     * ntc 事务切面服务
     *
     * @param ntcTransactionContext ntc事务上下文对象
     * @param point                 切点
     * @return object
     * @throws Throwable 异常信息
     */
    Object invoke(NtcTransactionContext ntcTransactionContext, ProceedingJoinPoint point) throws Throwable;
}

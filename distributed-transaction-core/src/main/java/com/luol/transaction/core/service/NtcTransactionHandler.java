package com.luol.transaction.core.service;

import com.luol.transaction.common.bean.context.NtcTransactionContext;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author luol
 * @date 2018/3/7
 * @time 17:44
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@FunctionalInterface
public interface NtcTransactionHandler {
    /**
     * 分布式事务处理接口
     *
     * @param point                 point 切点
     * @param ntcTransactionContext ntc事务上下文
     * @return Object
     * @throws Throwable 异常
     */
    Object handler(ProceedingJoinPoint point, NtcTransactionContext ntcTransactionContext) throws Throwable;
}

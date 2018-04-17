package com.luol.transaction.core.service.handler;

import com.luol.transaction.common.bean.context.NtcTransactionContext;
import com.luol.transaction.core.service.NtcTransactionHandler;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

/**
 * @author luol
 * @date 2018/3/8
 * @time 10:05
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Component
public class LocalNtcTransactionHandler implements NtcTransactionHandler {

//    @Resource
//    private NtcTransactionManager ntcTransactionManager;

    /**
     * 分布式事务处理接口
     *
     * @param point                 point 切点
     * @param ntcTransactionContext ntc事务上下文
     * @return Object
     * @throws Throwable 异常
     */
    @Override
    public Object handler(ProceedingJoinPoint point, NtcTransactionContext ntcTransactionContext) throws Throwable {
        /*Object proceed = point.proceed();
        if (Objects.nonNull(ntcTransactionContext)) {
            ntcTransactionManager.addLogs(NtcRoleEnum.LOCAL, ntcTransactionContext, NtcStatusEnum.SUCCESS, EventTypeEnum.UPDATE);
        }
        return proceed;*/
        return point.proceed();
    }
}

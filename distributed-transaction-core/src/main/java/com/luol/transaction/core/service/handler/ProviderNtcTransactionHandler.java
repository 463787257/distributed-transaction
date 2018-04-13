package com.luol.transaction.core.service.handler;

import com.luol.transaction.common.bean.context.NtcTransactionContext;
import com.luol.transaction.common.enums.EventTypeEnum;
import com.luol.transaction.common.enums.NtcRoleEnum;
import com.luol.transaction.common.enums.NtcStatusEnum;
import com.luol.transaction.common.enums.PatternEnum;
import com.luol.transaction.common.utils.DefaultValueUtils;
import com.luol.transaction.core.service.NtcTransactionHandler;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author luol
 * @date 2018/3/8
 * @time 10:05
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Component
public class ProviderNtcTransactionHandler implements NtcTransactionHandler {

    @Resource
    private NtcTransactionManager ntcTransactionManager;

    /**
     * 分布式事务处理接口 todo 如果异常是注解上的异常，则对异常进行封装   --- 日志记录，执行状态用上下文传过来的对象
     *
     * @param point                 point 切点
     * @param ntcTransactionContext ntc事务上下文
     * @return Object
     * @throws Throwable 异常
     */
    @Override
    public Object handler(ProceedingJoinPoint point, NtcTransactionContext ntcTransactionContext) throws Throwable {
        if (Objects.equals(ntcTransactionContext.getPatternEnum(), PatternEnum.NOTICE_ROLLBACK)) {
            //更新日志 -- 通知
            ntcTransactionManager.addLogs(NtcRoleEnum.PROVIDER, ntcTransactionContext, NtcStatusEnum.TRY, EventTypeEnum.SAVE);
            Object proceed = point.proceed();
            ntcTransactionManager.addLogs(NtcRoleEnum.PROVIDER, ntcTransactionContext, NtcStatusEnum.SUCCESS, EventTypeEnum.UPDATE);
            return proceed;
        } else {
            if (Objects.equals(ntcTransactionContext.getNtcStatusEnum(), NtcStatusEnum.CANCEL)) {
                //更新日志 -- cancel
                ntcTransactionManager.addLogs(NtcRoleEnum.PROVIDER, ntcTransactionContext, NtcStatusEnum.CANCEL, EventTypeEnum.UPDATE);
                ntcTransactionManager.cancel(point);
                ntcTransactionManager.addLogs(NtcRoleEnum.PROVIDER, ntcTransactionContext, NtcStatusEnum.SUCCESS, EventTypeEnum.UPDATE);
            } else {
                ntcTransactionManager.addLogs(NtcRoleEnum.PROVIDER, ntcTransactionContext, NtcStatusEnum.TRY, EventTypeEnum.SAVE);
                Object proceed = point.proceed();
                ntcTransactionManager.addLogs(NtcRoleEnum.PROVIDER, ntcTransactionContext, NtcStatusEnum.SUCCESS, EventTypeEnum.UPDATE);
                return proceed;
            }
        }
        Method method = ((MethodSignature) (point.getSignature())).getMethod();
        return DefaultValueUtils.getDefaultValue(method.getReturnType());
    }

}

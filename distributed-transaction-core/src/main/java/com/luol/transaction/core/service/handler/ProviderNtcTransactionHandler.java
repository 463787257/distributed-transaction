package com.luol.transaction.core.service.handler;

import com.luol.transaction.common.bean.context.NtcTransactionContext;
import com.luol.transaction.common.bean.model.NtcTransaction;
import com.luol.transaction.common.enums.EventTypeEnum;
import com.luol.transaction.common.enums.NtcRoleEnum;
import com.luol.transaction.common.enums.NtcStatusEnum;
import com.luol.transaction.common.enums.PatternEnum;
import com.luol.transaction.common.utils.DefaultValueUtils;
import com.luol.transaction.core.service.NtcTransactionHandler;
import com.luol.transaction.notify.disruptor.logs.publisher.NtcTransactionLogsPublisher;
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

    @Resource
    private NtcTransactionLogsPublisher ntcTransactionLogsPublisher;

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
            //更新日志 -- 通知 todo
            return point.proceed();
        } else {
            if (Objects.equals(ntcTransactionContext.getNtcStatusEnum(), NtcStatusEnum.CANCEL)) {
                ntcTransactionManager.cancel(point);
            } else {
                //更新日志 -- cancel todo
                addLogs(ntcTransactionContext, NtcStatusEnum.CANCEL);
                return point.proceed();
            }
        }
        Method method = ((MethodSignature) (point.getSignature())).getMethod();
        return DefaultValueUtils.getDefaultValue(method.getReturnType());
    }

    private void addLogs(NtcTransactionContext ntcTransactionContext, NtcStatusEnum ntcStatusEnum) {
        NtcTransaction ntcTransaction = ntcTransactionManager.buildNtcTransaction(NtcRoleEnum.PROVIDER, ntcTransactionContext.getTransID(), ntcTransactionContext.getPatternEnum());
        ntcTransaction.setNtcStatusEnum(ntcStatusEnum);
        ntcTransactionLogsPublisher.publishEvent(ntcTransaction, EventTypeEnum.SAVE);
    }

}

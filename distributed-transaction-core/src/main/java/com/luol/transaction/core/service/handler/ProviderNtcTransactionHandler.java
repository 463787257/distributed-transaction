package com.luol.transaction.core.service.handler;

import com.luol.transaction.common.bean.context.NtcTransactionContext;
import com.luol.transaction.common.enums.NtcStatusEnum;
import com.luol.transaction.common.enums.PatternEnum;
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
     * 分布式事务处理接口
     *
     * @param point                 point 切点
     * @param ntcTransactionContext ntc事务上下文
     * @return Object
     * @throws Throwable 异常
     */
    @Override
    public Object handler(ProceedingJoinPoint point, NtcTransactionContext ntcTransactionContext) throws Throwable {
        if (Objects.equals(ntcTransactionContext.getPatternEnum(), PatternEnum.NOTICE_ROLLBACK)) {
            try {
                return point.proceed();
            } catch (Throwable throwable) {
                //todo 更新失败日志

                throw throwable;
            }
        } else {
            if (Objects.equals(ntcTransactionContext.getNtcStatusEnum(), NtcStatusEnum.CANCEL)) {
                ntcTransactionManager.cancel(point);
            } else {
                //todo 添加日志

                return point.proceed();
            }
        }
        Method method = ((MethodSignature) (point.getSignature())).getMethod();
        return getDefaultValue(method.getReturnType());
    }

    private Object getDefaultValue(Class type) {
        if (boolean.class.equals(type)) {
            return false;
        } else if (byte.class.equals(type)) {
            return 0;
        } else if (short.class.equals(type)) {
            return 0;
        } else if (int.class.equals(type)) {
            return 0;
        } else if (long.class.equals(type)) {
            return 0;
        } else if (float.class.equals(type)) {
            return 0;
        } else if (double.class.equals(type)) {
            return 0;
        }
        return null;
    }
}

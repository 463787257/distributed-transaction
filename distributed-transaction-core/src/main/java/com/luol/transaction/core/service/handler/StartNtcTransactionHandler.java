package com.luol.transaction.core.service.handler;

import com.luol.transaction.common.bean.context.NtcTransactionContext;
import com.luol.transaction.common.bean.model.NtcTransaction;
import com.luol.transaction.common.concurrent.threadlocal.TransactionContextLocal;
import com.luol.transaction.common.enums.EventTypeEnum;
import com.luol.transaction.common.enums.NtcStatusEnum;
import com.luol.transaction.common.enums.PatternEnum;
import com.luol.transaction.core.service.NtcTransactionHandler;
import com.luol.transaction.asynchronous.disruptor.logs.publisher.NtcTransactionLogsPublisher;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author luol
 * @date 2018/3/8
 * @time 9:52
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Component
public class StartNtcTransactionHandler implements NtcTransactionHandler {

    @Resource
    private NtcTransactionManager ntcTransactionManager;

    @Resource
    private NtcTransactionLogsPublisher ntcTransactionLogsPublisher;

    private static final Logger LOGGER = LoggerFactory.getLogger(StartNtcTransactionHandler.class);

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
        NtcTransaction ntcTransaction = null;
        try {
            ntcTransaction = ntcTransactionManager.begin(point);

            //发布事务保存事件，异步保存---发起者，try开始
            ntcTransactionLogsPublisher.publishEvent(ntcTransaction, EventTypeEnum.SAVE);

            //调用当前调用方法
            final Object proceed = point.proceed();

            if (Objects.equals(ntcTransaction.getPatternEnum(), PatternEnum.ONLY_ROLLBACK)) {
                ntcTransaction.setNtcStatusEnum(NtcStatusEnum.CANCEL);
            } else if (Objects.equals(ntcTransaction.getPatternEnum(), PatternEnum.NOTIFY_ROLLBACK)) {
                ntcTransaction.setNtcStatusEnum(NtcStatusEnum.NOTIFY);
            }

            return proceed;
        } catch (Throwable throwable) {
            ntcTransaction.setNtcStatusEnum(NtcStatusEnum.CANCEL);
            ntcTransaction.setPatternEnum(PatternEnum.ONLY_ROLLBACK);
            throw throwable;
        } finally {
            //更新日志状态---发起者，try完成
            ntcTransactionLogsPublisher.publishEvent(ntcTransaction, EventTypeEnum.UPDATE);
            //发送消息
            ntcTransactionManager.asynchronous();
            ntcTransactionManager.cleanThreadLocal();
            TransactionContextLocal.getInstance().remove();
            LOGGER.warn("执行ntc事务结束！end");
        }
    }
}

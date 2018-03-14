package com.luol.transaction.core.service.handler;

import com.luol.transaction.common.bean.context.NtcTransactionContext;
import com.luol.transaction.common.bean.model.NtcTransaction;
import com.luol.transaction.common.enums.EventTypeEnum;
import com.luol.transaction.common.enums.NtcStatusEnum;
import com.luol.transaction.common.enums.PatternEnum;
import com.luol.transaction.core.concurrent.threadlocal.TransactionContextLocal;
import com.luol.transaction.core.disruptor.publisher.NtcTransactionLogsPublisher;
import com.luol.transaction.core.service.NtcTransactionHandler;
import org.aspectj.lang.ProceedingJoinPoint;
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
        Object returnValue = null;
        NtcTransaction ntcTransaction = null;
        boolean isSucess = Boolean.TRUE;
        try {
            ntcTransaction = ntcTransactionManager.begin(point);

            //发布事务保存事件，异步保存---发起者，try开始
            ntcTransactionLogsPublisher.publishEvent(ntcTransaction, EventTypeEnum.SAVE);

            //调用当前调用方法
            returnValue = point.proceed();

            ntcTransaction.setNtcStatusEnum(NtcStatusEnum.TRY_END);
            //更新日志状态---发起者，try完成
            ntcTransactionLogsPublisher.publishEvent(ntcTransaction, EventTypeEnum.UPDATE);
        } catch (Throwable throwable) {
            isSucess = Boolean.FALSE;
            NtcTransactionContext context = TransactionContextLocal.getInstance().get();
            if (Objects.nonNull(context)) {
                if (Objects.equals(context.getPatternEnum(), PatternEnum.ONLY_ROLLBACK)){
                    ntcTransaction.setNtcStatusEnum(NtcStatusEnum.CANCEL);
                    context.setNtcStatusEnum(NtcStatusEnum.CANCEL);
                    //异常执行cancel---发起者只需要调用下级cancel就好了 todo 想走异步
                    ntcTransactionManager.cancel();
                } else{
                    context.setNtcStatusEnum(NtcStatusEnum.NOTIFY);
                    ntcTransaction.setNtcStatusEnum(NtcStatusEnum.NOTIFY);
                }
            }
            throw throwable;
        } finally {
            //发送消息
            if (!isSucess) {
                ntcTransactionManager.sendMessage();
            }
            ntcTransactionManager.cleanThreadLocal();
            TransactionContextLocal.getInstance().remove();
        }
        return returnValue;
    }
}

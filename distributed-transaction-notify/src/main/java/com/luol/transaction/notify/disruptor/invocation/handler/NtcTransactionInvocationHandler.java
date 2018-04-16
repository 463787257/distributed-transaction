package com.luol.transaction.notify.disruptor.invocation.handler;

import com.lmax.disruptor.EventHandler;
import com.luol.transaction.common.bean.context.NtcTransactionContext;
import com.luol.transaction.common.bean.entity.NtcInvocation;
import com.luol.transaction.common.bean.model.NtcTransaction;
import com.luol.transaction.common.concurrent.threadlocal.TransactionContextLocal;
import com.luol.transaction.common.enums.EventTypeEnum;
import com.luol.transaction.common.enums.NtcRoleEnum;
import com.luol.transaction.common.enums.NtcStatusEnum;
import com.luol.transaction.common.enums.PatternEnum;
import com.luol.transaction.common.utils.InvokeUtils;
import com.luol.transaction.common.utils.SpringBeanUtils;
import com.luol.transaction.notify.disruptor.invocation.event.NtcTransactionInvocation;
import com.luol.transaction.notify.disruptor.invocation.publisher.NtcTransactionInvocationPublisher;
import com.luol.transaction.notify.disruptor.logs.publisher.NtcTransactionLogsPublisher;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author luol
 * @date 2018/3/27
 * @time 9:57
 * @function 功能: todo 要使用不查库方式，查库存在异步先后问题 +++ 另外 新开一个延迟队列
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Component
public class NtcTransactionInvocationHandler implements EventHandler<NtcTransactionInvocation> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NtcTransactionInvocationHandler.class);

    private NtcTransactionInvocationPublisher ntcTransactionInvocationPublisher;

    @Resource
    private NtcTransactionLogsPublisher ntcTransactionLogsPublisher;

//    @Resource
//    private CoordinatorService coordinatorService;

    @Override
    public void onEvent(NtcTransactionInvocation ntcTransactionInvocation, long l, boolean b) throws Exception {
        notByPersistentStorage(ntcTransactionInvocation);
//        byPersistentStorage(ntcTransactionInvocation);
    }

    /**查库方式*/
    /*private void byPersistentStorage(NtcTransactionInvocation ntcTransactionInvocation) {
        NtcTransaction ntcTransaction = coordinatorService.getByTransIDAndName(ntcTransactionInvocation.getTransID(),
                ntcTransactionInvocation.getTargetClass(), ntcTransactionInvocation.getTargetMethod());
        if (Objects.isNull(ntcTransaction) || Objects.equals(ntcTransaction.getNtcStatusEnum(), NtcStatusEnum.SUCCESS)) {
            return;
        }
        if (CollectionUtils.isEmpty(ntcTransaction.getRpcNtcInvocations())) {
            ntcTransaction.setRpcNtcInvocations(ntcTransactionInvocation.getRpcNtcInvocations());
        }
        try {
            NtcTransactionContext context = new NtcTransactionContext();
            context.setTransID(ntcTransactionInvocation.getTransID());
            Boolean bool = Objects.equals(ntcTransactionInvocation.getPatternEnum(), PatternEnum.ONLY_ROLLBACK);
            if (bool) {
                context.setNtcStatusEnum(NtcStatusEnum.CANCEL);
                context.setPatternEnum(PatternEnum.ONLY_ROLLBACK);
                TransactionContextLocal.getInstance().set(context);
            } else {
                context.setNtcStatusEnum(NtcStatusEnum.NOTIFY);
                context.setPatternEnum(PatternEnum.NOTICE_ROLLBACK);
                context.setNtcRoleEnum(NtcRoleEnum.LOCAL);
                TransactionContextLocal.getInstance().set(context);
            }
            LOGGER.warn("NtcTransactionInvocationHandler --- {} 模式开始执行!", ntcTransactionInvocation.getPatternEnum().getContent());
            Boolean flag = Boolean.FALSE;
            for (NtcInvocation ntcInvocation : ntcTransaction.getRpcNtcInvocations()) {
                NtcTransaction transIDAndName = coordinatorService.getByTransIDAndName(ntcTransactionInvocation.getTransID(),
                        ntcInvocation.getTargetClass().getName(), ntcInvocation.getMethodName());
                if (Objects.equals(Objects.nonNull(transIDAndName) &&
                        Objects.equals(transIDAndName.getNtcStatusEnum(), NtcStatusEnum.SUCCESS), bool)) {
                    try {
                        InvokeUtils.executeParticipantMethod(ntcInvocation);
                        ntcInvocation.setIsSuccess(!bool);
                    } catch (Exception e) {
                        LOGGER.warn("NtcTransactionInvocationHandler --- {} 方法执行失败！", ntcInvocation.getMethodName());
                        LOGGER.warn("NtcTransactionInvocationHandler --- 失败信息：", e);
                        flag = Boolean.TRUE;
                    }
                }
            }
            if (!bool && ntcTransaction.getCurrentRetryCounts() >= ntcTransaction.getMaxRetryCounts()) {
                ntcTransaction.setPatternEnum(PatternEnum.ONLY_ROLLBACK);
            }
            ntcTransaction.setCurrentRetryCounts(ntcTransaction.getCurrentRetryCounts() + NumberUtils.INTEGER_ONE);
            if (flag) {
                //重新排队，重复调用，并记录当前调用几次
                if (Objects.isNull(ntcTransactionInvocationPublisher)) {
                    final NtcTransactionInvocationPublisher ntcTransactionInvocationPublisher = SpringBeanUtils.getInstance().getBean(NtcTransactionInvocationPublisher.class);
                    this.ntcTransactionInvocationPublisher = ntcTransactionInvocationPublisher;
                }
                //ntcTransactionLogsPublisher.publishEvent(ntcTransaction, EventTypeEnum.UPDATE); todo 异步导致重试次数不准确
                coordinatorService.update(ntcTransaction);
                ntcTransactionInvocationPublisher.publishEvent(ntcTransaction);//todo 应该延时完成任务
            } else {
                ntcTransaction.setNtcStatusEnum(NtcStatusEnum.SUCCESS);
                ntcTransaction.setNtcRoleEnum(context.getNtcRoleEnum());
                //ntcTransactionLogsPublisher.publishEvent(ntcTransaction, EventTypeEnum.UPDATE);
                coordinatorService.update(ntcTransaction);
            }
            LOGGER.warn("NtcTransactionInvocationHandler --- {} 模式执行结束!", ntcTransaction.getPatternEnum().getContent());
        } finally {
            //删除上下文
            TransactionContextLocal.getInstance().remove();
            ntcTransactionInvocation.clear();
        }
    }*/

    /**不查库方式*/
    private void notByPersistentStorage(NtcTransactionInvocation ntcTransactionInvocation) {
        if (CollectionUtils.isEmpty(ntcTransactionInvocation.getRpcNtcInvocations())) {
            return;
        }
        try {
            NtcTransactionContext context = new NtcTransactionContext();
            context.setTransID(ntcTransactionInvocation.getTransID());
            Boolean bool = Objects.equals(ntcTransactionInvocation.getPatternEnum(), PatternEnum.ONLY_ROLLBACK);
            if (bool) {
                context.setNtcStatusEnum(NtcStatusEnum.CANCEL);
                context.setPatternEnum(PatternEnum.ONLY_ROLLBACK);
                TransactionContextLocal.getInstance().set(context);
            } else {
                context.setNtcStatusEnum(NtcStatusEnum.NOTIFY);
                context.setPatternEnum(PatternEnum.NOTICE_ROLLBACK);
                context.setNtcRoleEnum(NtcRoleEnum.LOCAL);
                TransactionContextLocal.getInstance().set(context);
            }
            LOGGER.warn("NtcTransactionInvocationHandler --- {} 模式开始执行!", ntcTransactionInvocation.getPatternEnum().getContent());
            Boolean flag = Boolean.FALSE;
            for (NtcInvocation ntcInvocation : ntcTransactionInvocation.getRpcNtcInvocations()) {
                if (Objects.equals(ntcInvocation.getIsSuccess(), bool)) {
                    try {
                        InvokeUtils.executeParticipantMethod(ntcInvocation);
                        ntcInvocation.setIsSuccess(!bool);
                    } catch (Exception e) {
                        LOGGER.warn("NtcTransactionInvocationHandler --- {} 方法执行失败！", ntcInvocation.getMethodName());
                        LOGGER.warn("NtcTransactionInvocationHandler --- 失败信息：", e);
                        flag = Boolean.TRUE;
                    }
                }
            }
            NtcTransaction ntcTransaction = new NtcTransaction();
            if (!bool && ntcTransactionInvocation.getCurrentRetryCounts() > ntcTransactionInvocation.getMaxRetryCounts()) {
                ntcTransaction.setPatternEnum(PatternEnum.ONLY_ROLLBACK);
            } else {
                ntcTransaction.setPatternEnum(ntcTransactionInvocation.getPatternEnum());
            }
            ntcTransaction.setRpcNtcInvocations(ntcTransactionInvocation.getRpcNtcInvocations());
            ntcTransaction.setCurrentRetryCounts(ntcTransactionInvocation.getCurrentRetryCounts() + NumberUtils.INTEGER_ONE);
            if (flag) {
                //重新排队，重复调用，并记录当前调用几次
                if (Objects.isNull(ntcTransactionInvocationPublisher)) {
                    final NtcTransactionInvocationPublisher ntcTransactionInvocationPublisher = SpringBeanUtils.getInstance().getBean(NtcTransactionInvocationPublisher.class);
                    this.ntcTransactionInvocationPublisher = ntcTransactionInvocationPublisher;
                }
                //todo 异常优先MQ，MQ失败再走JDK
                ntcTransactionInvocationPublisher.publishEvent(ntcTransaction);
                ntcTransactionLogsPublisher.publishEvent(ntcTransaction, EventTypeEnum.UPDATE);
            } else {
                ntcTransaction.setNtcStatusEnum(NtcStatusEnum.SUCCESS);
                ntcTransaction.setNtcRoleEnum(context.getNtcRoleEnum());
                ntcTransactionLogsPublisher.publishEvent(ntcTransaction, EventTypeEnum.UPDATE);
            }
            LOGGER.warn("NtcTransactionInvocationHandler --- {} 模式执行结束!", ntcTransactionInvocation.getPatternEnum().getContent());
        } finally {
            //删除上下文
            TransactionContextLocal.getInstance().remove();
            ntcTransactionInvocation.clear();
        }
    }

}

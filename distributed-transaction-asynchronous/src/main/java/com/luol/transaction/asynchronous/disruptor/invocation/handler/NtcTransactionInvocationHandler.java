package com.luol.transaction.asynchronous.disruptor.invocation.handler;

import com.lmax.disruptor.EventHandler;
import com.luol.transaction.asynchronous.disruptor.invocation.event.NtcTransactionInvocation;
import com.luol.transaction.asynchronous.disruptor.invocation.publisher.NtcTransactionInvocationPublisher;
import com.luol.transaction.asynchronous.disruptor.logs.publisher.NtcTransactionLogsPublisher;
import com.luol.transaction.common.bean.model.NtcTransaction;
import com.luol.transaction.common.coordinator.CoordinatorService;
import com.luol.transaction.common.enums.EventTypeEnum;
import com.luol.transaction.common.enums.NtcStatusEnum;
import com.luol.transaction.common.utils.SpringBeanUtils;
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
 * @function 功能:
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Component
public class NtcTransactionInvocationHandler implements EventHandler<NtcTransactionInvocation> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NtcTransactionInvocationHandler.class);

    private NtcTransactionInvocationPublisher ntcTransactionInvocationPublisher;

    @Resource
    private NtcTransactionLogsPublisher ntcTransactionLogsPublisher;

    @Resource
    private CoordinatorService coordinatorService;

    @Override
    public void onEvent(NtcTransactionInvocation ntcTransactionInvocation, long l, boolean b) throws Exception {
        if (Objects.isNull(ntcTransactionInvocation)) {
            return;
        }
        notByPersistentStorage(ntcTransactionInvocation);
    }

    /**不查库方式*/
    private void notByPersistentStorage(NtcTransactionInvocation ntcTransactionInvocation) {
        NtcTransaction transaction = ntcTransactionInvocation.getNtcTransaction();
        if (CollectionUtils.isEmpty(transaction.getRpcNtcInvocations())) {
            return;
        }
        LOGGER.warn("NtcTransactionInvocationHandler --- {} 模式执行开始!", transaction.getPatternEnum().getContent());
        try {
            Boolean isFail = coordinatorService.handlerInvocation(transaction);
            if (isFail) {
                //重新排队，重复调用，并记录当前调用几次
                if (Objects.isNull(ntcTransactionInvocationPublisher)) {
                    final NtcTransactionInvocationPublisher ntcTransactionInvocationPublisher = SpringBeanUtils.getInstance().getBean(NtcTransactionInvocationPublisher.class);
                    this.ntcTransactionInvocationPublisher = ntcTransactionInvocationPublisher;
                }
                ntcTransactionInvocationPublisher.publishEvent(transaction);
            } else {
                transaction.setNtcStatusEnum(NtcStatusEnum.SUCCESS);
                ntcTransactionLogsPublisher.publishEvent(transaction, EventTypeEnum.UPDATE);
            }
        } finally {
            LOGGER.warn("NtcTransactionInvocationHandler --- {} 模式执行结束!", transaction.getPatternEnum().getContent());
            ntcTransactionInvocation.clear();
        }
    }

}

package com.luol.transaction.asynchronous.disruptor.logs.handler;

import com.lmax.disruptor.ExceptionHandler;
import com.luol.transaction.asynchronous.disruptor.logs.event.NtcTransactionLogs;
import com.luol.transaction.asynchronous.disruptor.logs.publisher.NtcTransactionLogsPublisher;
import com.luol.transaction.common.bean.message.MessageEntity;
import com.luol.transaction.common.coordinator.CoordinatorService;
import com.luol.transaction.common.enums.AsynchronousTypeEnums;
import com.luol.transaction.common.utils.SpringBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author luol
 * @date 2018/3/26
 * @time 9:36
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Component
public class NtcTransactionLogsExceptionHandler implements ExceptionHandler<NtcTransactionLogs> {

    private final static Logger LOGER = LoggerFactory.getLogger(NtcTransactionLogsExceptionHandler.class);

    @Resource
    private CoordinatorService coordinatorService;

    private NtcTransactionLogsPublisher ntcTransactionLogsPublisher;

    @Override
    public void handleEventException(Throwable throwable, long l, NtcTransactionLogs ntcTransactionLogs) {
        LOGER.warn("异步日志操作出现异常，异常信息：", throwable);

        //日志异常走MQ，MQ失败再重新走JDK
        MessageEntity messageEntity = new MessageEntity(AsynchronousTypeEnums.LOGS, ntcTransactionLogs.getNtcTransaction(), ntcTransactionLogs.getEventTypeEnum());
        Boolean isSuccess = coordinatorService.sendMessage(messageEntity);
        if (!isSuccess) {
            if (Objects.isNull(ntcTransactionLogsPublisher)) {
                ntcTransactionLogsPublisher = SpringBeanUtils.getInstance().getBean(NtcTransactionLogsPublisher.class);
            }
            if (Objects.nonNull(ntcTransactionLogs)) {
                ntcTransactionLogsPublisher.publishEvent(ntcTransactionLogs.getNtcTransaction(), ntcTransactionLogs.getEventTypeEnum());
            }
        }
    }

    @Override
    public void handleOnStartException(Throwable throwable) {

    }

    @Override
    public void handleOnShutdownException(Throwable throwable) {

    }
}

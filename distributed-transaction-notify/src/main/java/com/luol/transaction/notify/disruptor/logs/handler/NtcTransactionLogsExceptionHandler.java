package com.luol.transaction.notify.disruptor.logs.handler;

import com.lmax.disruptor.ExceptionHandler;
import com.luol.transaction.common.utils.SpringBeanUtils;
import com.luol.transaction.notify.disruptor.logs.event.NtcTransactionLogs;
import com.luol.transaction.notify.disruptor.logs.publisher.NtcTransactionLogsPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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

    private NtcTransactionLogsPublisher ntcTransactionLogsPublisher;

    @Override
    public void handleEventException(Throwable throwable, long l, NtcTransactionLogs ntcTransactionLogs) {
        LOGER.warn("异步日志操作出现异常，异常信息：", throwable);
        if (Objects.isNull(ntcTransactionLogsPublisher)) {
            ntcTransactionLogsPublisher = SpringBeanUtils.getInstance().getBean(NtcTransactionLogsPublisher.class);
        }
        //todo 日志异常走MQ，MQ日常再重新走JDK
        if (Objects.nonNull(ntcTransactionLogs)) {
            ntcTransactionLogsPublisher.publishEvent(ntcTransactionLogs.getNtcTransaction(), ntcTransactionLogs.getEventTypeEnum());
        }
    }

    @Override
    public void handleOnStartException(Throwable throwable) {

    }

    @Override
    public void handleOnShutdownException(Throwable throwable) {

    }
}

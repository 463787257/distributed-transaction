package com.luol.transaction.asynchronous.disruptor.invocation.handler;

import com.lmax.disruptor.ExceptionHandler;
import com.luol.transaction.asynchronous.disruptor.invocation.event.NtcTransactionInvocation;
import com.luol.transaction.asynchronous.disruptor.invocation.publisher.NtcTransactionInvocationPublisher;
import com.luol.transaction.common.utils.SpringBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author luol
 * @date 2018/3/27
 * @time 9:57
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Component
public class NtcTransactionInvocationExceptionHandler implements ExceptionHandler<NtcTransactionInvocation> {

    private final static Logger LOGER = LoggerFactory.getLogger(NtcTransactionInvocationExceptionHandler.class);

    private NtcTransactionInvocationPublisher ntcTransactionInvocationPublisher;

    @Override
    public void handleEventException(Throwable throwable, long l, NtcTransactionInvocation ntcTransactionInvocation) {
        LOGER.warn("异步反射调用出现异常，异常信息：", throwable);
        //重新排队，重复调用，并记录当前调用几次
        if (Objects.isNull(ntcTransactionInvocationPublisher)) {
            final NtcTransactionInvocationPublisher ntcTransactionInvocationPublisher = SpringBeanUtils.getInstance().getBean(NtcTransactionInvocationPublisher.class);
            this.ntcTransactionInvocationPublisher = ntcTransactionInvocationPublisher;
        }
        ntcTransactionInvocationPublisher.publishEvent(ntcTransactionInvocation.getNtcTransaction());
    }

    @Override
    public void handleOnStartException(Throwable throwable) {

    }

    @Override
    public void handleOnShutdownException(Throwable throwable) {

    }
}

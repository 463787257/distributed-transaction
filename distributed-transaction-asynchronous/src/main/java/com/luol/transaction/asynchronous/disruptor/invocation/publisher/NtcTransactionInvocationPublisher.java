package com.luol.transaction.asynchronous.disruptor.invocation.publisher;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.luol.transaction.asynchronous.disruptor.invocation.event.NtcTransactionInvocation;
import com.luol.transaction.asynchronous.disruptor.invocation.handler.NtcTransactionInvocationHandler;
import com.luol.transaction.common.bean.model.NtcTransaction;
import com.luol.transaction.asynchronous.disruptor.invocation.factory.NtcTransactionInvocationFactory;
import com.luol.transaction.asynchronous.disruptor.invocation.handler.NtcTransactionInvocationExceptionHandler;
import com.luol.transaction.asynchronous.disruptor.invocation.translator.NtcTransactionInvocationTranslator;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author luol
 * @date 2018/3/27
 * @time 9:57
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Component
public class NtcTransactionInvocationPublisher {

    private Disruptor<NtcTransactionInvocation> disruptor;

    @Resource
    private NtcTransactionInvocationHandler ntcTransactionInvocationHandler;

    @Resource
    private NtcTransactionInvocationExceptionHandler ntcTransactionInvocationExceptionHandler;

    @PostConstruct
    public void init() throws Exception {
        disruptor = new Disruptor<>(new NtcTransactionInvocationFactory(),
                1024, r -> {
            AtomicInteger index = new AtomicInteger(1);
            return new Thread(null, r, "disruptor-invocation-thread-" + index.getAndIncrement());
        }, ProducerType.MULTI, new YieldingWaitStrategy());
        disruptor.handleEventsWith(ntcTransactionInvocationHandler);
        disruptor.setDefaultExceptionHandler(ntcTransactionInvocationExceptionHandler);
        disruptor.start();
    }

    public void publishEvent(NtcTransaction ntcTransaction) {
        final RingBuffer<NtcTransactionInvocation> ringBuffer = disruptor.getRingBuffer();
        ringBuffer.publishEvent(new NtcTransactionInvocationTranslator(), ntcTransaction);
    }

    @PreDestroy
    public void destroy() throws Exception {
        disruptor.shutdown();
    }
}

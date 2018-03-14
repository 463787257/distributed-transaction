package com.luol.transaction.core.disruptor.publisher;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.luol.transaction.common.bean.model.NtcTransaction;
import com.luol.transaction.common.enums.EventTypeEnum;
import com.luol.transaction.core.disruptor.event.NtcTransactionLogs;
import com.luol.transaction.core.disruptor.factory.NtcTransactionLogsFactory;
import com.luol.transaction.core.disruptor.handler.NtcTransactionLogsHandler;
import com.luol.transaction.core.disruptor.translator.NtcTransactionLogsTranslator;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author luol
 * @date 2018/3/12
 * @time 10:07
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Component
public class NtcTransactionLogsPublisher implements InitializingBean,DisposableBean {

    private Disruptor<NtcTransactionLogs> disruptor;

    @Resource
    private NtcTransactionLogsHandler ntcTransactionLogsHandler;

    @Override
    public void afterPropertiesSet() throws Exception {
        disruptor = new Disruptor<>(new NtcTransactionLogsFactory(),
                        1024, r -> {
                    AtomicInteger index = new AtomicInteger(1);
                    return new Thread(null, r, "disruptor-thread-" + index.getAndIncrement());
                }, ProducerType.MULTI, new YieldingWaitStrategy());
        disruptor.handleEventsWith(ntcTransactionLogsHandler);
        disruptor.start();
    }

    public void publishEvent(NtcTransaction ntcTransaction, EventTypeEnum eventTypeEnum) {
        final RingBuffer<NtcTransactionLogs> ringBuffer = disruptor.getRingBuffer();
        ringBuffer.publishEvent(new NtcTransactionLogsTranslator(eventTypeEnum), ntcTransaction);
    }

    @Override
    public void destroy() throws Exception {
        disruptor.shutdown();
    }

}

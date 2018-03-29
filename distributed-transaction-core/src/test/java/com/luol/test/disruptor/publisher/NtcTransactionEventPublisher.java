/*
 *
 *  * Licensed to the Apache Software Foundation (ASF) under one or more
 *  * contributor license agreements.  See the NOTICE file distributed with
 *  * this work for additional information regarding copyright ownership.
 *  * The ASF licenses this file to You under the Apache License, Version 2.0
 *  * (the "License"); you may not use this file except in compliance with
 *  * the License.  You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.luol.test.disruptor.publisher;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.ProducerType;
import com.luol.test.disruptor.event.NtcTransactionEvent;
import com.luol.test.disruptor.factory.NtcTransactionEventFactory;
import com.luol.test.disruptor.handler.NtcTransactionEventHandler;
import com.luol.test.disruptor.handler.TestHandler;
import com.luol.test.disruptor.translator.NtcTransactionEventTranslator;
import com.luol.transaction.common.bean.model.NtcTransaction;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>Description: .</p>
 *
 * @author xiaoyu(Myth)
 * @version 1.0
 * @date 2018/3/5 15:01
 * @since JDK 1.8
 */
public class NtcTransactionEventPublisher implements DisposableBean {

    private Disruptor<NtcTransactionEvent> disruptor;

    private NtcTransactionEventHandler ntcTransactionEventHandler = new NtcTransactionEventHandler();


    public NtcTransactionEventPublisher(int bufferSize) {
        disruptor =
                new Disruptor<>(new NtcTransactionEventFactory(),
                        bufferSize, r -> {
                    AtomicInteger index = new AtomicInteger(1);
                    return new Thread(null, r, "disruptor-thread-" + index.getAndIncrement());
                }, ProducerType.MULTI, new YieldingWaitStrategy());

        EventHandlerGroup<NtcTransactionEvent> ntcTransactionEventEventHandlerGroup = disruptor.handleEventsWith(ntcTransactionEventHandler);
        ntcTransactionEventEventHandlerGroup.then(new TestHandler());
        disruptor.start();
    }

    public void publishEvent(NtcTransaction ntcTransaction, int type) {
        final RingBuffer<NtcTransactionEvent> ringBuffer = disruptor.getRingBuffer();
        ringBuffer.publishEvent(new NtcTransactionEventTranslator(type), ntcTransaction);
    }


    /**
     * Invoked by a BeanFactory on destruction of a singleton.
     *
     * @throws Exception in case of shutdown errors.
     *                   Exceptions will get logged but not rethrown to allow
     *                   other beans to release their resources too.
     */
    @Override
    public void destroy() throws Exception {
        disruptor.shutdown();
    }
}

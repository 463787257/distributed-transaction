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

package com.luol.test.disruptor.handler;

import com.alibaba.fastjson.JSON;
import com.lmax.disruptor.EventHandler;
import com.luol.test.disruptor.event.NtcTransactionEvent;

/**
 * <p>Description: .</p>
 *
 * @author xiaoyu(Myth)
 * @version 1.0
 * @date 2018/3/5 11:52
 * @since JDK 1.8
 */
public class NtcTransactionEventHandler implements EventHandler<NtcTransactionEvent> {


    @Override
    public void onEvent(NtcTransactionEvent ntcTransactionEvent,
                        long sequence, boolean endOfBatch)
            throws Exception {
        System.out.println("handler : " + JSON.toJSONString(ntcTransactionEvent));
        //ntcTransactionEvent.clear();
    }
}

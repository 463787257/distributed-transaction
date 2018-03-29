package com.luol.test.disruptor.main;

import com.luol.test.disruptor.publisher.NtcTransactionEventPublisher;
import com.luol.transaction.common.bean.model.NtcTransaction;

/**
 * @author luol
 * @date 2018/3/12
 * @time 10:17
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public class Main {
    public static void main(String[] args) throws Exception {
        NtcTransactionEventPublisher ntcTransactionEventPublisher = new NtcTransactionEventPublisher(1024);
        for (int i = 0; i < 100; i++) {
            Thread.sleep(10);
            ntcTransactionEventPublisher.publishEvent(new NtcTransaction(), i);
        }
        ntcTransactionEventPublisher.destroy();
    }
}

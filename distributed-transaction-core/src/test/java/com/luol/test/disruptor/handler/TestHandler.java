package com.luol.test.disruptor.handler;

import com.alibaba.fastjson.JSON;
import com.lmax.disruptor.EventHandler;
import com.luol.test.disruptor.event.NtcTransactionEvent;

/**
 * @author luol
 * @date 2018/3/16
 * @time 11:00
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public class TestHandler  implements EventHandler<NtcTransactionEvent> {
    @Override
    public void onEvent(NtcTransactionEvent ntcTransactionEvent, long l, boolean b) throws Exception {
        System.out.println("我是测试：" + JSON.toJSONString(ntcTransactionEvent));
    }
}

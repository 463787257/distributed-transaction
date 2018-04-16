package com.luol.transaction.common.coordinator;

/**
 * @author luol
 * @date 2018/4/16
 * @time 11:50
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@FunctionalInterface
public interface NtcMqSendService {
    /**
     * 发送消息
     * @param topic 队列
     * @param message  NtcTransaction实体对象转换成byte[]后的数据
     */
    void sendMessage(String topic, byte[] message);
}

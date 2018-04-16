package com.luol.transaction.common.coordinator;

/**
 * @author luol
 * @date 2018/4/16
 * @time 11:54
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@FunctionalInterface
public interface NtcMqReceiveService {

    /**
     * 处理发出的mq消息
     * @param message 实体对象转换成byte[]后的数据
     * @return true 成功 false 失败
     */
    Boolean processMessage(byte[] message);

}

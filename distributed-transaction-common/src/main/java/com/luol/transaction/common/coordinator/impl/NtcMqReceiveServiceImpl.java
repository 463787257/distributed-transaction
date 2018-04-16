package com.luol.transaction.common.coordinator.impl;

import com.luol.transaction.common.coordinator.CoordinatorService;
import com.luol.transaction.common.coordinator.NtcMqReceiveService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author luol
 * @date 2018/4/16
 * @time 14:01
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Component("ntcMqReceiveService")
public class NtcMqReceiveServiceImpl implements NtcMqReceiveService {

    @Resource
    private CoordinatorService coordinatorService;

    /**
     * 处理发出的mq消息
     *
     * @param message 实体对象转换成byte[]后的数据
     * @return true 成功 false 失败
     */
    @Override
    public Boolean processMessage(byte[] message) {
        return coordinatorService.processMessage(message);
    }
}

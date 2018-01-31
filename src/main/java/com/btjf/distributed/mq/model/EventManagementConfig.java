package com.btjf.distributed.mq.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ll
 * @version 0.0.1
 * @date 16/7/6
 * @time 下午5:15
 * @function 功能: 事件机制配置类
 * @describe 版本描述:
 * @modifyLog 修改日志:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventManagementConfig {

    /**
     * rocketMQ配置  注入
     */
    private String ROCKETMQ_NAMESVR_ADDRESS;

    private String APPLICATION_NAME;

}

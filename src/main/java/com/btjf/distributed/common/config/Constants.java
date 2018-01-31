package com.btjf.distributed.common.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author luol
 * @date 2018/1/29
 * @time 11:55
 * @function 功能：读取共用的配置信息
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Configuration
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Constants {

    /**
     * 项目name，作为消息的topic
     * */
    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${distributed.transaction.repository.type}")
    private String repositorySupportType;

    @Value("${serializer.type}")
    private String serializerType;

    /**
     * 日志异步队列大小
     */
    @Value("${coordinator.queue.max}")
    private Integer coordinatorQueueMax;

    /**
     * 重试次数
     */
    @Value("${retried.count}")
    private Integer retriedCount;

}

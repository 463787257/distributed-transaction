package com.btjf.distributed.mq.event;

import com.alibaba.fastjson.JSON;
import com.btjf.distributed.core.coordinator.CoordinatorService;
import com.btjf.distributed.mq.annotation.RocketConsumer;
import com.btjf.distributed.mq.bean.MessageContent;
import com.btjf.distributed.mq.processor.MessageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

/**
 * @author luol
 * @date 2018/1/29
 * @time 11:34
 * @function 功能：接受事物的消息
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@RocketConsumer
public class TransactionIdenticalConsumer implements MessageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(TransactionIdenticalConsumer.class);
    @Resource
    private CoordinatorService coordinatorService;

    @Override
    public void processMessage(String message) {
        logger.info("transactionIdenticalConsumer accept message success, messageContext: {}", message);
        logger.info("transactionIdenticalConsumer consumer message begin!");
        coordinatorService.processMessage(JSON.parseObject(message, MessageContent.class));
        logger.info("transactionIdenticalConsumer consumer message end!");
    }
}

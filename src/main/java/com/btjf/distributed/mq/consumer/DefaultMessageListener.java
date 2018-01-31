package com.btjf.distributed.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.btjf.distributed.mq.processor.MessageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author luol
 * @date 2017/8/16
 * @time 18:07
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public class DefaultMessageListener implements MessageListenerConcurrently {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMessageListener.class);

    private MessageProcessor messageProcessor;

    public DefaultMessageListener(MessageProcessor messageProcessor){
        this.messageProcessor = messageProcessor;
    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        for (MessageExt messageExt : list) {
            if(messageExt == null || messageExt.getBody() == null) {
                LOGGER.warn("rocketMQ decoder or decoder.getBody() could not be null!");
                continue;
            }
            String msgkey = messageExt.getKeys();
            String message = JSON.parseObject(messageExt.getBody(), String.class);
            try {
                this.messageProcessor.processMessage(message);
            } catch (Exception e) {
                LOGGER.warn("The Message processed unsuccessfully, msgId= " + msgkey);
                LOGGER.warn("cause by: " + e.getMessage());
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}

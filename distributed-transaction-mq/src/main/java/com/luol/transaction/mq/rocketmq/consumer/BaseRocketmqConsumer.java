package com.luol.transaction.mq.rocketmq.consumer;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.luol.transaction.common.config.NtcConfig;
import com.luol.transaction.common.coordinator.NtcMqReceiveService;
import com.luol.transaction.mq.rocketmq.util.ConsumerTopicSubscribeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author luol
 * @date 2018/4/16
 * @time 10:48
 * @function 功能：一个应用创建一个Consumer，由应用来维护此对象
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Component
public class BaseRocketmqConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseRocketmqConsumer.class);

    @Resource
    private NtcConfig ntcConfig;

    @Resource
    private NtcMqReceiveService ntcMqReceiveService;

    @PostConstruct
    public void init() throws MQClientException {
        pushConsumer();
    }

    public void pushConsumer() throws MQClientException {
        String topic = ntcConfig.getModelName();
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(ConsumerTopicSubscribeUtils.buidGroup(topic));
        consumer.setNamesrvAddr(ntcConfig.getMqAddress());
        consumer.subscribe(topic, ConsumerTopicSubscribeUtils.buidSubscribe(topic));
        consumer.setConsumeThreadMin(5);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.registerMessageListener(
            (List<MessageExt> msgList, ConsumeConcurrentlyContext context) -> {
                MessageExt messageExt = msgList.get(0);
                try {
                    // 默认msgList里只有一条消息，可以通过设置consumeMessageBatchMaxSize参数来批量接收消息
                    final byte[] message = messageExt.getBody();
                    ntcMqReceiveService.processMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    //重复消费
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                //如果没有return success，consumer会重复消费此信息，直到success。
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        );
        consumer.start();
        LOGGER.warn("topic: {},启动成功", topic);
    }

}

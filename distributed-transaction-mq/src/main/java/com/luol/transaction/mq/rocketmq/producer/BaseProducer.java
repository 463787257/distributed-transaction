package com.luol.transaction.mq.rocketmq.producer;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.common.message.Message;
import com.luol.transaction.common.config.NtcConfig;
import com.luol.transaction.common.coordinator.NtcMqSendService;
import com.luol.transaction.common.utils.IDGenUtils;
import com.luol.transaction.mq.rocketmq.util.ConsumerTopicSubscribeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author luol
 * @date 2018/4/16
 * @time 10:53
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Component("ntcMqSendService")
public class BaseProducer implements NtcMqSendService {

    private DefaultMQProducer defaultMQProducer;

    @Resource
    private NtcConfig ntcConfig;

    private static final Logger logger = LoggerFactory.getLogger(BaseProducer.class);

    @PostConstruct
    public void init() {
        defaultMQProducer = new DefaultMQProducer();
        defaultMQProducer.setNamesrvAddr(ntcConfig.getMqAddress());
        defaultMQProducer.setProducerGroup("ll-group");
        defaultMQProducer.setSendMsgTimeout(10000);
        try {
            defaultMQProducer.start();
        } catch (MQClientException e) {
            logger.warn("producer初始化失败：" + e.getMessage());
        }
    }

    @PreDestroy
    public void destroy(){
        if(Objects.nonNull(defaultMQProducer)){
            defaultMQProducer.shutdown();
        }
    }

    /**
     * 发送消息到mq
     * */
    @Override
    public void sendMessage(String topic, byte[] messageByte){
        sendMessage(topic, ConsumerTopicSubscribeUtils.buidSubscribe(topic), messageByte);
    }

    /**
     * 发送消息到mq
     * */
    private void sendMessage(String topic, String subscribe, byte[] messageByte){
        Message message = new Message(topic, subscribe
                , IDGenUtils.get().createID(), messageByte);
        try {
            defaultMQProducer.send(message);
        } catch (Exception e) {
            logger.warn("消息发送失败：" + e.getMessage());
        }
    }
}

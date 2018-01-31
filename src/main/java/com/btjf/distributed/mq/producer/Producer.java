package com.btjf.distributed.mq.producer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.common.message.Message;
import com.btjf.distributed.mq.model.EventManagementConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.UUID;

/**
 * @author luol
 * @date 2017/8/17
 * @time 13:57
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Component
public class Producer {

    private DefaultMQProducer producer;

    private static final Logger logger = LoggerFactory.getLogger(Producer.class);

    @Resource
    private EventManagementConfig eventManagementConfig;

    @PostConstruct
    public void init(){
        producer = new DefaultMQProducer();
        producer.setNamesrvAddr(eventManagementConfig.getROCKETMQ_NAMESVR_ADDRESS());
        producer.setProducerGroup("ll-group");
        producer.setSendMsgTimeout(10000);
        try {
            producer.start();
        } catch (MQClientException e) {
            logger.warn("producer初始化失败：" + e.getMessage());
        }
    }

    @PreDestroy
    public void destroy(){
        if(producer != null){
            producer.shutdown();
        }
    }

    public void send(String topic, String subscribe, Object object){
        Message message = new Message(topic,subscribe
                , UUID.randomUUID().toString(), JSON.toJSONBytes(object, new SerializerFeature[0]));
        try {
            producer.send(message);
        } catch (Exception e) {
            logger.warn("消息发送失败：" + e.getMessage());
        }
    }

}

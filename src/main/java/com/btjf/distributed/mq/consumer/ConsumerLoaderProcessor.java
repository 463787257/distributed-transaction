package com.btjf.distributed.mq.consumer;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.btjf.distributed.common.config.Constants;
import com.btjf.distributed.common.constants.CommonConstant;
import com.btjf.distributed.mq.annotation.RocketConsumer;
import com.btjf.distributed.mq.model.EventManagementConfig;
import com.btjf.distributed.mq.processor.MessageProcessor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author luol
 * @date 2018/1/29
 * @time 11:10
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Component
@Configuration
public class ConsumerLoaderProcessor implements BeanPostProcessor {

    @Resource
    private EventManagementConfig eventManagementConfig;
    @Resource
    private Constants constants;

    private final Logger logger = LoggerFactory.getLogger(ConsumerLoaderProcessor.class);

    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        return o;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        RocketConsumer rocketConsumer = o.getClass().getAnnotation(RocketConsumer.class);
        if (rocketConsumer != null){
            //对应加上SimpleConsumer注解类要实现MessageProcessor
            MessageProcessor messageProcesser = (MessageProcessor)o;
            String topic = StringUtils.isEmpty(rocketConsumer.topic()) ? constants.getApplicationName() : rocketConsumer.topic();
            try {
                String groupName = StringUtils.isEmpty(rocketConsumer.groupName()) ? s + CommonConstant.GROUP_NAME_SUFFIX : rocketConsumer.groupName();
                DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(groupName);
                consumer.setNamesrvAddr(eventManagementConfig.getROCKETMQ_NAMESVR_ADDRESS());
                if (StringUtils.isEmpty(topic)) {
                    throw new RuntimeException("create RocketConsumer happen error, error message: topic is null");
                }
                String subscribe = StringUtils.isEmpty(rocketConsumer.subscribe()) ? constants.getApplicationName() + CommonConstant.SUBCRIBE_SUFFIX : rocketConsumer.subscribe();
                consumer.subscribe(topic, subscribe);
                consumer.setConsumeThreadMin(5);
                consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
                consumer.registerMessageListener(new DefaultMessageListener(messageProcesser));
                consumer.start();
                logger.warn(topic + " create RocketConsumer success ");
            } catch (MQClientException e) {
                logger.warn(topic + " create RocketConsumer fail, error message: " + e.getMessage());
            }
        }
        return o;
    }

}

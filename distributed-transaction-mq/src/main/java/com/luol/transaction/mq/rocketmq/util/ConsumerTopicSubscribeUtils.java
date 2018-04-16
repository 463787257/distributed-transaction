package com.luol.transaction.mq.rocketmq.util;

import java.util.Objects;

/**
 * @author luol
 * @date 2018/4/16
 * @time 11:11
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public class ConsumerTopicSubscribeUtils {

    private static final String SUFFIX_GROUP = "-group";

    private static final String SUFFIX_SUBSCRIBE = "-subscribe";

    public static String buidSubscribe(String topic) {
        if (Objects.isNull(topic)) {
            return null;
        }
        return topic + SUFFIX_SUBSCRIBE;
    }

    public static String buidGroup(String topic) {
        if (Objects.isNull(topic)) {
            return null;
        }
        return topic + SUFFIX_GROUP;
    }
}

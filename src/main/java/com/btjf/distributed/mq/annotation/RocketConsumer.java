package com.btjf.distributed.mq.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author luol
 * @date 2018/1/29
 * @time 11:15
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Component
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RocketConsumer {

    /**
     * 指定groupName
     * @return
     */
    String groupName() default "";

    /**
     * 指定Topic
     * @return
     */
    String topic() default "";

    /**
     * 指定Subscribe
     * @return
     */
    String subscribe() default "";
}

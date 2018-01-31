package com.btjf.distributed.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author luol
 * @date 2018/1/29
 * @time 13:37
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DistributedTransaction {
    //选用模式
    TransactionType value() default TransactionType.MQ_RETRY;
}

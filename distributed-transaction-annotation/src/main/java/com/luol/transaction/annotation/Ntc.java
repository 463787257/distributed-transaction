package com.luol.transaction.annotation;

import com.luol.transaction.common.enums.PatternEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author luol
 * @date 2018/3/7
 * @time 15:49
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Ntc {

    /**
     * 选用模式
     * */
    PatternEnum pattern() default PatternEnum.NOTICE_ROLLBACK;

    /**
     * 回滚方法名称
     * */
    String cancelMethod() default "";

    /**
     * 重试最大次数
     * */
    int maxRetryCounts() default 10;

    /**
     * 碰到异常直接回滚的异常集合
     * */
    Class<? extends Throwable>[] rollbackFor() default {};

}

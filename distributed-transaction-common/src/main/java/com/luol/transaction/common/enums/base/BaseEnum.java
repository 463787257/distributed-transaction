package com.luol.transaction.common.enums.base;

import java.util.Objects;

/**
 * @author luol
 * @date 2018/3/7
 * @time 16:08
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public interface BaseEnum {

    String getContent();

    Integer getValue();

    default boolean equalsValue(Integer value) {
        return Objects.equals(this.getValue(), value);
    }
}

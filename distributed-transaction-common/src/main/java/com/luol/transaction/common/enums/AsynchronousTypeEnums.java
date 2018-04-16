package com.luol.transaction.common.enums;

import com.luol.transaction.common.enums.base.BaseEnum;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author luol
 * @date 2018/4/16
 * @time 14:20
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public enum AsynchronousTypeEnums  implements BaseEnum {

    INVOCATION("反射调用", 0),
    LOGS("日志记录", 1);

    private String content;
    private Integer value;

    AsynchronousTypeEnums(String content, Integer value) {
        this.content = content;
        this.value = value;
    }

    @Override
    public String getContent() {
        return this.content;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    public static AsynchronousTypeEnums objectOf(Object object) {
        Optional<AsynchronousTypeEnums> enums = Arrays.stream(AsynchronousTypeEnums.values())
                .filter(v -> Objects.equals(v.getContent(), object) || Objects.equals(v.getValue(), object))
                .findFirst();
        return enums.orElse(null);
    }
}

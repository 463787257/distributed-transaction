package com.luol.transaction.common.enums;

import com.luol.transaction.common.enums.base.BaseEnum;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author luol
 * @date 2018/3/12
 * @time 10:39
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public enum  EventTypeEnum implements BaseEnum{

    SAVE("保存", 0),
    UPDATE("更新", 1),
    DELETE("删除", 2);

    private String content;
    private Integer value;

    EventTypeEnum(String content, Integer value) {
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

    public static EventTypeEnum objectOf(Object object) {
        Optional<EventTypeEnum> enums = Arrays.stream(EventTypeEnum.values())
                .filter(v -> Objects.equals(v.getContent(), object) || Objects.equals(v.getValue(), object))
                .findFirst();
        return enums.orElse(EventTypeEnum.SAVE);
    }
}

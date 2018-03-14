package com.luol.transaction.common.enums;

import com.luol.transaction.common.enums.base.BaseEnum;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author luol
 * @date 2018/3/7
 * @time 16:52
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public enum NtcRoleEnum implements BaseEnum {

    START("发起者", 1),
    CONSUMER("消费者", 2),
    PROVIDER("提供者", 3),
    LOCAL("本地调用", 4);

    private String content;
    private Integer value;

    NtcRoleEnum(String content, Integer value) {
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

    public static NtcRoleEnum objectOf(Object object) {
        Optional<NtcRoleEnum> enums = Arrays.stream(NtcRoleEnum.values())
                .filter(v -> Objects.equals(v.getContent(), object) || Objects.equals(v.getValue(), object))
                .findFirst();
        return enums.orElse(NtcRoleEnum.START);
    }

}

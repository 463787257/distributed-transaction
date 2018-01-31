package com.btjf.distributed.common.enums;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author luol
 * @date 2018/1/29
 * @time 15:02
 * @function 功能：
 * @contentribe 版本描述：
 * @modifyLog 修改日志：
 */
public enum DistributedRoleEnum {

    START("发起者", 1),
    LOCAL("本地执行", 2),
    PROVIDER("提供者", 3);

    private int value;

    private String content;

    DistributedRoleEnum(String content, int value) {
        this.value = value;
        this.content = content;
    }


    /**
     * Acquire by value tcc action enum.
     *
     * @param value the value
     * @return the tcc action enum
     */
    public static DistributedRoleEnum acquireByValue(int value) {
        Optional<DistributedRoleEnum> tccRoleEnum =
                Arrays.stream(DistributedRoleEnum.values())
                        .filter(v -> Objects.equals(v.getValue(), value))
                        .findFirst();
        return tccRoleEnum.orElse(DistributedRoleEnum.START);

    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

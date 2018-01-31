package com.btjf.distributed.common.enums;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author luol
 * @date 2018/1/30
 * @time 9:21
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public enum SerializeEnum {

    JDK("jdk"),
    KRYO("kryo"),
    HESSIAN("hessian"),
    PROTOSTUFF("protostuff");

    private String serialize;

    SerializeEnum(String serialize) {
        this.serialize = serialize;
    }

    public static SerializeEnum acquire(String serialize) {
        Optional<SerializeEnum> serializeEnum =
                Arrays.stream(SerializeEnum.values())
                        .filter(v -> Objects.equals(v.getSerialize(), serialize))
                        .findFirst();
        return serializeEnum.orElse(SerializeEnum.KRYO);

    }

    public String getSerialize() {
        return serialize;
    }

    public void setSerialize(String serialize) {
        this.serialize = serialize;
    }
}

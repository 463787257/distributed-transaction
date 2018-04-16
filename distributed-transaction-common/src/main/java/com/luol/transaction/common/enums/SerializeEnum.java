package com.luol.transaction.common.enums;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author luol
 * @date 2018/3/30
 * @time 11:45
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public enum SerializeEnum {
    /**
     * Jdk serialize protocol enum.
     */
    JDK("jdk"),

    /**
     * Kryo serialize protocol enum.
     */
    KRYO("kryo"),

    /**
     * Hessian serialize protocol enum.
     */
    HESSIAN("hessian"),

    /**
     * Protostuff serialize protocol enum.
     */
    PROTOSTUFF("protostuff");

    private String serialize;

    SerializeEnum(String serialize) {
        this.serialize = serialize;
    }

    /**
     * Acquire serialize protocol serialize protocol enum.
     *
     * @param serialize the serialize protocol
     * @return the serialize protocol enum
     */
    public static SerializeEnum acquire(String serialize) {
        Optional<SerializeEnum> serializeEnum =
                Arrays.stream(SerializeEnum.values())
                        .filter(v -> Objects.equals(v.getSerialize(), serialize))
                        .findFirst();
        return serializeEnum.orElse(SerializeEnum.KRYO);
    }

    /**
     * Gets serialize protocol.
     *
     * @return the serialize protocol
     */
    public String getSerialize() {
        return serialize;
    }

    /**
     * Sets serialize protocol.
     *
     * @param serialize the serialize protocol
     */
    public void setSerialize(String serialize) {
        this.serialize = serialize;
    }
}

package com.luol.transaction.common.enums;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author luol
 * @date 2018/3/30
 * @time 15:27
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public enum RepositorySupportEnum {
    /**
     * Db compensate cache type enum.
     */
    DB("db"),

    /**
     * File compensate cache type enum.
     */
    FILE("file"),

    /**
     * Redis compensate cache type enum.
     */
    REDIS("redis"),

    /**
     * Mongodb compensate cache type enum.
     */
    MONGODB("mongodb"),

    /**
     * Zookeeper compensate cache type enum.
     */
    ZOOKEEPER("zookeeper");

    private String support;

    RepositorySupportEnum(String support) {
        this.support = support;
    }

    /**
     * Acquire compensate cache type compensate cache type enum.
     *
     * @param support the compensate cache type
     * @return the compensate cache type enum
     */
    public static RepositorySupportEnum acquire(String support) {
        Optional<RepositorySupportEnum> repositorySupportEnum =
                Arrays.stream(RepositorySupportEnum.values())
                        .filter(v -> Objects.equals(v.getSupport(), support))
                        .findFirst();
        return repositorySupportEnum.orElse(RepositorySupportEnum.REDIS);
    }


    public String getSupport() {
        return support;
    }

    public void setSupport(String support) {
        this.support = support;
    }
}

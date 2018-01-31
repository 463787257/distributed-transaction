package com.btjf.distributed.common.enums;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author luol
 * @date 2018/1/29
 * @time 17:39
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public enum RepositorySupportEnum {

    DATABASE("dataBase"),
    REDIS("redis");

    private String support;

    RepositorySupportEnum(String support) {
        this.support = support;
    }

    public static RepositorySupportEnum acquire(String support) {
        Optional<RepositorySupportEnum> repositorySupportEnum =
                Arrays.stream(RepositorySupportEnum.values())
                        .filter(v -> Objects.equals(v.getSupport(), support))
                        .findFirst();
        return repositorySupportEnum.orElse(RepositorySupportEnum.DATABASE);
    }


    public String getSupport() {
        return support;
    }

    public void setSupport(String support) {
        this.support = support;
    }
}

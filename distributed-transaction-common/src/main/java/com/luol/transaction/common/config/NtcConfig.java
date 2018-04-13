package com.luol.transaction.common.config;

import lombok.Data;

/**
 * @author luol
 * @date 2018/4/4
 * @time 16:03
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Data
public class NtcConfig {
    /**
     * 提供不同的序列化对象 {@linkplain com.luol.transaction.common.enums.SerializeEnum}
     */
    private String serializer = "kryo";

    /**
     * 补偿存储类型 {@linkplain com.luol.transaction.common.enums.RepositorySupportEnum}
     */
    private String repositorySupport = "redis";

    /**
     * 模块名称
     * */
    private String modelName = "one";
}

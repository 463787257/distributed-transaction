package com.luol.transaction.common.config;

import com.luol.transaction.common.enums.RepositorySupportEnum;
import com.luol.transaction.common.enums.SerializeEnum;
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
    private SerializeEnum serializer = SerializeEnum.KRYO;

    /**
     * 补偿存储类型 {@linkplain com.luol.transaction.common.enums.RepositorySupportEnum}
     */
    private RepositorySupportEnum repositorySupport = RepositorySupportEnum.REDIS;

    /**
     * 模块名称
     * */
    private String modelName = "modelName";

    /**
     * mq地址
     * */
    private String mqAddress = "192.168.100.181:9876";

    /**
     * cancel方法连续调用最大次数
     * */
    private Integer maxInvocationCancelNum = 6;

    /**
     * 是否需要自动恢复
     * 1 注意 当为事务发起方的时候（调用方/消费方），这里需要填true，
     * 默认为false，为了节省资源，不开启线程池调度
     */
    private Boolean needRecover = true;

    /**
     * 调度时间周期 单位min
     */
    private int scheduledDelay = 10;

    /**
     * redis 配置
     * */
    private NtcRedisConfig ntcRedisConfig;

    /**
     * mongdb配置
     * */
    private NtcMongoConfig ntcMongoConfig;
}

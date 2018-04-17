package com.luol.transaction.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author luol
 * @date 2018/4/16
 * @time 15:09
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Configuration
public class BeanConfig {

    @Bean
    @ConditionalOnMissingBean(NtcConfig.class)
    public NtcConfig ntcConfig() {
        //给默认的NtcConfig
        NtcConfig ntcConfig = new NtcConfig();
        NtcRedisConfig ntcRedisConfig = new NtcRedisConfig();
        ntcRedisConfig.setHostName("47.94.147.107");
        ntcRedisConfig.setPort(6379);
        ntcConfig.setNtcRedisConfig(ntcRedisConfig);
        return ntcConfig;
    }
}

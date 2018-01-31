package com.btjf.distributed.base.config.mybaits;

import com.btjf.distributed.mq.model.EventManagementConfig;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by 10731 on 2017/9/20.
 */
@Configuration
@MapperScan(value = "com.btjf.distributed.**.mapper")
public class MybatisConfig {

    @Value("${spring.application.name}")
    private String applicationName;
    @Value("${rocketmq.namesrv.address}")
    private String rocketmqAddress;

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public EventManagementConfig eventManagementConfig() {
        EventManagementConfig eventManagementConfig = new EventManagementConfig();
        eventManagementConfig.setAPPLICATION_NAME(applicationName);
        eventManagementConfig.setROCKETMQ_NAMESVR_ADDRESS(rocketmqAddress);
        return eventManagementConfig;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath*:com/btjf/distributed/**/mapper/*.xml"));

        //添加插件
        sqlSessionFactoryBean.setPlugins(new org.apache.ibatis.plugin.Interceptor[]{pageHelper()});

        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBean.getObject();
        sqlSessionFactory.getConfiguration().setMapUnderscoreToCamelCase(true);
        return sqlSessionFactory;
    }

    @Bean
    public PageHelper pageHelper() {
        //分页插件
        PageHelper pageHelper = new PageHelper();
        Properties properties = new Properties();
        properties.setProperty("offsetAsPageNum", "true");
        properties.setProperty("rowBoundsWithCount", "true");
        properties.setProperty("reasonable", "true");
        properties.setProperty("supportMethodsArguments", "true");
        properties.setProperty("returnPageInfo", "check");
        properties.setProperty("params", "count=countSql");
        pageHelper.setProperties(properties);
        return pageHelper;
    }

}

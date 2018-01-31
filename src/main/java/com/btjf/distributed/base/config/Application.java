package com.btjf.distributed.base.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.transaction.annotation.ProxyTransactionManagementConfiguration;

/**
 * @author luol
 * @date 2018/1/29
 *  @function 功能：
 */
@SpringBootApplication
@ComponentScan({"com.btjf.distributed"})
@ImportResource(locations = {"applicationContext-redis.xml"})
public class Application extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Application.class);
    }

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(Application.class, args);
        ProxyTransactionManagementConfiguration proxyTransactionManagementConfiguration = applicationContext.getBean(ProxyTransactionManagementConfiguration.class);
        System.out.print(proxyTransactionManagementConfiguration.getClass());
    }
}

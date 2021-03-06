package com.luol.transaction.core.bootstrap;

import com.luol.transaction.common.utils.SpringBeanUtils;
import com.luol.transaction.core.service.NtcInitService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author luol
 * @date 2018/3/27
 * @time 17:15
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Component
public class NtcTransactionBootstrap implements ApplicationContextAware {

    @Resource
    private NtcInitService ntcInitService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringBeanUtils.getInstance().setCfgContext((ConfigurableApplicationContext) applicationContext);
        ntcInitService.initialization();
    }

}

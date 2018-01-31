package com.btjf.distributed.core.service.impl;

import com.btjf.distributed.common.bean.context.DistributedTransactionContext;
import com.btjf.distributed.common.utils.SpringBeanUtils;
import com.btjf.distributed.core.service.DistributedTransactionAspectService;
import com.btjf.distributed.core.service.DistributedTransactionFactoryService;
import com.btjf.distributed.core.service.DistributedTransactionHandler;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author luol
 * @date 2018/1/29
 * @time 14:28
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Component
public class DistributedTransactionAspectServiceImpl implements DistributedTransactionAspectService {

    @Resource
    private DistributedTransactionFactoryService distributedTransactionFactoryService;

    /**
     * myth事务切面服务
     *
     * @param distributedTransactionContext myth事务上下文对象
     * @param point                         切点
     * @return object
     * @throws Throwable 异常信息
     */
    @Override
    public Object invoke(DistributedTransactionContext distributedTransactionContext, ProceedingJoinPoint point) throws Throwable {
        final Class clazz = distributedTransactionFactoryService.factoryOf(distributedTransactionContext);
        final DistributedTransactionHandler distributedTransactionHandler =
                (DistributedTransactionHandler) SpringBeanUtils.getInstance().getBean(clazz);
        return distributedTransactionHandler.handler(point, distributedTransactionContext);
    }
}

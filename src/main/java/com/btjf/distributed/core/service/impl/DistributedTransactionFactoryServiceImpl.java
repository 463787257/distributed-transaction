package com.btjf.distributed.core.service.impl;

import com.btjf.distributed.common.bean.context.DistributedTransactionContext;
import com.btjf.distributed.common.enums.DistributedRoleEnum;
import com.btjf.distributed.core.service.DistributedTransactionFactoryService;
import com.btjf.distributed.core.service.handler.ActorDistributedTransactionHandler;
import com.btjf.distributed.core.service.handler.LocalDistributedTransactionHandler;
import com.btjf.distributed.core.service.handler.StartDistributedTransactionHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author luol
 * @date 2018/1/29
 * @time 14:30
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Component
public class DistributedTransactionFactoryServiceImpl implements DistributedTransactionFactoryService {

    @Resource
    private DistributedTransactionManager distributedTransactionManager;

    /**
     * 返回 实现TxTransactionHandler类的名称
     *
     * @param context 事务上下文
     * @return Class<T>
     * @throws Throwable 抛出异常
     */
    @Override
    public Class factoryOf(DistributedTransactionContext context) throws Throwable {
        //如果事务还没开启或者 DistributedTransaction事务上下文是空， 那么应该进入发起调用
        if (!distributedTransactionManager.isBegin() && Objects.isNull(context)) {
            return StartDistributedTransactionHandler.class;
        } else {
            if (Objects.equals(context.getRole(), DistributedRoleEnum.LOCAL)) {
                return LocalDistributedTransactionHandler.class;
            }
            return ActorDistributedTransactionHandler.class;
        }
    }
}

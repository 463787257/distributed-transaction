package com.luol.transaction.core.service.impl;

import com.luol.transaction.common.bean.context.NtcTransactionContext;
import com.luol.transaction.core.service.NtcTransactionFactoryService;
import com.luol.transaction.core.service.handler.LocalNtcTransactionHandler;
import com.luol.transaction.core.service.handler.NtcTransactionManager;
import com.luol.transaction.core.service.handler.ProviderNtcTransactionHandler;
import com.luol.transaction.core.service.handler.StartNtcTransactionHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author luol
 * @date 2018/3/7
 * @time 17:38
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Service("ntcTransactionFactoryService")
public class NtcTransactionFactoryServiceImpl implements NtcTransactionFactoryService {

    @Resource
    private NtcTransactionManager ntcTransactionManager;

    /**
     * 返回 实现TxTransactionHandler类的名称
     *
     * @param context
     * @return Class<T>
     * @throws Throwable 抛出异常
     */
    @Override
    public Class factoryOf(NtcTransactionContext context) throws Throwable {
        //如果事务还没开启或者 ntc事务上下文是空， 那么应该进入发起调用
        if (!ntcTransactionManager.isBegin() && Objects.isNull(context)) {
            return StartNtcTransactionHandler.class;
        } else if (ntcTransactionManager.isBegin() && Objects.isNull(context)) {
            return LocalNtcTransactionHandler.class;
        } else if (Objects.nonNull(context)) {
            return ProviderNtcTransactionHandler.class;
        }
        return LocalNtcTransactionHandler.class;
    }
}

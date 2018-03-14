package com.luol.transaction.core.service.impl;

import com.luol.transaction.common.bean.context.NtcTransactionContext;
import com.luol.transaction.common.utils.SpringBeanUtils;
import com.luol.transaction.core.service.NtcTransactionAspectService;
import com.luol.transaction.core.service.NtcTransactionFactoryService;
import com.luol.transaction.core.service.NtcTransactionHandler;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author luol
 * @date 2018/3/7
 * @time 17:36
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Service("ntcTransactionAspectService")
public class NtcTransactionAspectServiceImpl implements NtcTransactionAspectService {

    @Resource
    private NtcTransactionFactoryService ntcTransactionFactoryService;

    /**
     * ntc 事务切面服务
     *
     * @param ntcTransactionContext ntc事务上下文对象
     * @param point                 切点
     * @return object
     * @throws Throwable 异常信息
     */
    @Override
    public Object invoke(NtcTransactionContext ntcTransactionContext, ProceedingJoinPoint point) throws Throwable {
        final Class aClass = ntcTransactionFactoryService.factoryOf(ntcTransactionContext);
        final NtcTransactionHandler ntcTransactionHandler = (NtcTransactionHandler) SpringBeanUtils.getInstance().getBean(aClass);
        return ntcTransactionHandler.handler(point, ntcTransactionContext);
    }
}

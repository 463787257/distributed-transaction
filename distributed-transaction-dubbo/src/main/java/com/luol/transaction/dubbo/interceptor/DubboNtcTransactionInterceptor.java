package com.luol.transaction.dubbo.interceptor;

import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.fastjson.JSON;
import com.luol.transaction.common.bean.context.NtcTransactionContext;
import com.luol.transaction.common.constant.CommonConstant;
import com.luol.transaction.core.concurrent.threadlocal.TransactionContextLocal;
import com.luol.transaction.core.interceptor.NtcTransactionInterceptor;
import com.luol.transaction.core.service.NtcTransactionAspectService;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author luol
 * @date 2018/3/7
 * @time 17:18
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Component
public class DubboNtcTransactionInterceptor implements NtcTransactionInterceptor {

    @Resource
    private NtcTransactionAspectService ntcTransactionAspectService;

    /**
     * 分布式事务拦截方法
     *
     * @param pjp 切入点
     * @return Object
     * @throws Throwable 异常
     */
    @Override
    public Object interceptor(ProceedingJoinPoint pjp) throws Throwable {
        final String context = RpcContext.getContext().getAttachment(CommonConstant.NTC_TRANSACTION_CONTEXT);
        NtcTransactionContext ntcTransactionContext;
        if (StringUtils.isEmpty(context)) {
            ntcTransactionContext = TransactionContextLocal.getInstance().get();
        } else {
            ntcTransactionContext = JSON.parseObject(context, NtcTransactionContext.class);
        }
        return ntcTransactionAspectService.invoke(ntcTransactionContext, pjp);
    }
}

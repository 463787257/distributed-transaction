package com.btjf.distributed.dubbo.interceptor;

import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.fastjson.JSON;
import com.btjf.distributed.common.constants.CommonConstant;
import com.btjf.distributed.common.bean.context.DistributedTransactionContext;
import com.btjf.distributed.core.interceptor.DistributedTransactionInterceptor;
import com.btjf.distributed.core.service.DistributedTransactionAspectService;
import com.btjf.distributed.core.threadlocal.TransactionContextLocal;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author luol
 * @date 2018/1/29
 * @time 14:17
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Component
public class DubboDistributedTransactionInterceptor implements DistributedTransactionInterceptor{

    @Resource
    private DistributedTransactionAspectService distributedTransactionAspectService;

    @Override
    public Object interceptor(ProceedingJoinPoint pjp) throws Throwable {
        final String context = RpcContext.getContext().getAttachment(CommonConstant.DISTRIBUTED_TRANSACTION_CONTEXT);
        DistributedTransactionContext distributedTransactionContext;
        if (StringUtils.isNoneBlank(context)) {
            distributedTransactionContext = JSON.parseObject(context, DistributedTransactionContext.class);
        }else{
            distributedTransactionContext= TransactionContextLocal.getInstance().get();
        }
        return distributedTransactionAspectService.invoke(distributedTransactionContext, pjp);
    }
}

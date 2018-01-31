package com.btjf.distributed.dubbo.filter;


import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import com.alibaba.fastjson.JSON;
import com.btjf.distributed.annotation.DistributedTransaction;
import com.btjf.distributed.annotation.TransactionType;
import com.btjf.distributed.common.bean.context.DistributedTransactionContext;
import com.btjf.distributed.common.constants.CommonConstant;
import com.btjf.distributed.common.enums.DistributedRoleEnum;
import com.btjf.distributed.core.threadlocal.TransactionContextLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author luol
 * @date 2018/1/29
 * @time 13:40
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Activate(group = {Constants.SERVER_KEY, Constants.CONSUMER})
public class DubboDistributedTransactionFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(DubboDistributedTransactionFilter.class);

    @Override
    @SuppressWarnings("unchecked")
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        String methodName = invocation.getMethodName();
        Class clazz = invoker.getInterface();
        Class[] args = invocation.getParameterTypes();

        Method method = null;
        DistributedTransaction distributedTransaction = null;
        try {
            method = clazz.getDeclaredMethod(methodName, args);
            distributedTransaction = method.getAnnotation(DistributedTransaction.class);
        } catch (NoSuchMethodException e) {
            logger.error("get DistributedTransaction appear NoSuchMethodException !", e);
        }

        if (Objects.nonNull(distributedTransaction)) {
            if (Objects.equals(distributedTransaction.value(), TransactionType.MQ_RETRY)) {
                try {
                    final DistributedTransactionContext distributedTransactionContext = TransactionContextLocal.getInstance().get();
                    if (Objects.nonNull(distributedTransactionContext) && !Objects.equals(DistributedRoleEnum.LOCAL, distributedTransactionContext.getRole())) {
                        //上下文不为空时，进行传递
                        RpcContext.getContext().setAttachment(CommonConstant.DISTRIBUTED_TRANSACTION_CONTEXT, JSON.toJSONString(distributedTransactionContext));
                    }
                    return invoker.invoke(invocation);
                } catch (RpcException e) {
                    logger.error("RPC transmit distributedTransactionContext happen error !", e);
                    return new RpcResult();
                }
            }
        }
        return invoker.invoke(invocation);
    }

}

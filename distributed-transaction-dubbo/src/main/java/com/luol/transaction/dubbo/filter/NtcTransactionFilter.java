package com.luol.transaction.dubbo.filter;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.fastjson.JSON;
import com.luol.transaction.annotation.Ntc;
import com.luol.transaction.common.bean.context.NtcTransactionContext;
import com.luol.transaction.common.concurrent.threadlocal.TransactionContextLocal;
import com.luol.transaction.common.constant.CommonConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author luol
 * @date 2018/3/8
 * @time 17:20
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Activate(group = {Constants.SERVER_KEY, Constants.CONSUMER})
public class NtcTransactionFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(NtcTransactionFilter.class);

    /**
     * do invoke filter.
     * <p>
     * <code>
     * // before filter
     * Result result = invoker.invoke(invocation);
     * // after filter
     * return result;
     * </code>
     *
     * @param invoker    service
     * @param invocation invocation.
     * @return invoke result.
     * @throws RpcException
     * @see Invoker#invoke(Invocation)
     */
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        String methodName = invocation.getMethodName();
        Class clazz = invoker.getInterface();
        Class[] args = invocation.getParameterTypes();

        Method method = null;
        Ntc ntc = null;
        try {
            method = clazz.getDeclaredMethod(methodName, args);
            ntc = method.getAnnotation(Ntc.class);
        } catch (NoSuchMethodException e) {
            LOGGER.warn("dubbo拦截器获取注解出错NoSuchMethodException: {}", e);
        }
        if (Objects.nonNull(ntc)) {
            NtcTransactionContext ntcTransactionContext = null;
            try {
                ntcTransactionContext = TransactionContextLocal.getInstance().get();
                //context不为空时，进行rpc传递
                if (Objects.nonNull(ntcTransactionContext)) {
                    RpcContext.getContext().setAttachment(CommonConstant.NTC_TRANSACTION_CONTEXT, JSON.toJSONString(ntcTransactionContext));
                }
                return invoker.invoke(invocation);
            } catch (RpcException e) {
                LOGGER.warn("dubbo拦截器远程调用异常RpcException: {}", e);
                throw e;
            }
        } else {
            return invoker.invoke(invocation);
        }
    }

}

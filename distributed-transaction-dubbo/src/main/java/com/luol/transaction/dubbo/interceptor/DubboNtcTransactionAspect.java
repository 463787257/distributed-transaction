package com.luol.transaction.dubbo.interceptor;

import com.luol.transaction.core.interceptor.AbstractNtcTransactionAspect;
import com.luol.transaction.core.interceptor.NtcTransactionInterceptor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author luol
 * @date 2018/3/7
 * @time 17:13
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DubboNtcTransactionAspect extends AbstractNtcTransactionAspect {

    public DubboNtcTransactionAspect(DubboNtcTransactionInterceptor dubboNtcTransactionInterceptor) {
        super(dubboNtcTransactionInterceptor);
    }

}

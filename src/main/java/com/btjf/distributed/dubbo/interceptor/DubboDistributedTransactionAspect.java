package com.btjf.distributed.dubbo.interceptor;

import com.btjf.distributed.core.interceptor.AbstractDistributedTransactionAspect;
import com.btjf.distributed.core.interceptor.DistributedTransactionInterceptor;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * @author luol
 * @date 2018/1/29
 * @time 14:13
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Aspect
@Component
public class DubboDistributedTransactionAspect extends AbstractDistributedTransactionAspect implements Ordered {

    @Autowired
    public DubboDistributedTransactionAspect(DistributedTransactionInterceptor distributedTransactionInterceptor) {
        super.setDistributedTransactionInterceptor(distributedTransactionInterceptor);
    }

    /**
     * spring Order 接口，该值的返回直接会影响springBean的加载顺序
     *
     * @return int 类型
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}

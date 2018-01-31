package com.btjf.distributed.core.service.impl;

import com.btjf.distributed.common.bean.context.DistributedTransactionContext;
import com.btjf.distributed.common.bean.entity.DistributedInvocation;
import com.btjf.distributed.common.bean.entity.DistributedParticipant;
import com.btjf.distributed.common.bean.entity.DistributedTransactionInfo;
import com.btjf.distributed.common.enums.CoordinatorActionEnum;
import com.btjf.distributed.common.enums.DistributedRoleEnum;
import com.btjf.distributed.common.enums.DistributedStatusEnum;
import com.btjf.distributed.core.coordinator.CoordinatorService;
import com.btjf.distributed.core.coordinator.bean.CoordinatorAction;
import com.btjf.distributed.core.coordinator.impl.CoordinatorCommand;
import com.btjf.distributed.core.threadlocal.TransactionContextLocal;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author luol
 * @date 2018/1/29
 * @time 14:55
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Component
public class DistributedTransactionManager {

    private static final Logger logger = LoggerFactory.getLogger(DistributedTransactionManager.class);

    /**
     * 事物信息存放
     * */
    private static final ThreadLocal<DistributedTransactionInfo> TRANSACTION_INFO_THREAD_LOCAL = new ThreadLocal<>();

    @Resource
    private CoordinatorService coordinatorService;
    @Resource
    private CoordinatorCommand coordinatorCommand;

    /**
     * 事物是否已经开启
     * */
    public boolean isBegin() {
        return TRANSACTION_INFO_THREAD_LOCAL.get() != null;
    }

    /**
     * 事物开启
     * */
    public DistributedTransactionInfo begin(ProceedingJoinPoint point) {
        logger.info("exec DistributedTransaction begin!");
        DistributedTransactionInfo distributedTransactionInfo = getCurrentTransaction();
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Class<?> clazz = point.getTarget().getClass();
        //访问目标方法的参数：
        Object[] args = point.getArgs();
        Class<?>[] parameterTypes = method.getParameterTypes();

        if (Objects.isNull(distributedTransactionInfo)) {
            distributedTransactionInfo = new DistributedTransactionInfo();
            distributedTransactionInfo.setStatus(DistributedStatusEnum.BEGIN);
            distributedTransactionInfo.setRole(DistributedRoleEnum.START);
            distributedTransactionInfo.setTargetClass(clazz.getName());
            distributedTransactionInfo.setTargetMethod(method.getName());
        }

        //保存当前事务信息
        coordinatorCommand.execute(new CoordinatorAction(CoordinatorActionEnum.SAVE, distributedTransactionInfo));
        //当前事务保存到ThreadLocal
        TRANSACTION_INFO_THREAD_LOCAL.set(distributedTransactionInfo);
        //设置tcc事务上下文，这个类会传递给远端
        DistributedTransactionContext context = new DistributedTransactionContext();
        //设置事务id
        context.setTransID(distributedTransactionInfo.getTransID());
        //设置为发起者角色
        context.setRole(DistributedRoleEnum.START);

        DistributedInvocation key = new DistributedInvocation(clazz, method.getName(), DistributedStatusEnum.BEGIN, parameterTypes, args);
        //放入调用链的集合中
        context.addInvocationChain(key);

        TransactionContextLocal.getInstance().set(context);
        return distributedTransactionInfo;
    }

    /**
     * 保存事物日志
     * */
    public DistributedTransactionInfo actorTransaction(ProceedingJoinPoint point, DistributedTransactionContext distributedTransactionContext) {
        DistributedTransactionInfo distributedTransactionInfo = buildProviderTransaction(point, distributedTransactionContext.getTransID(), DistributedStatusEnum.BEGIN);
        //保存当前事务信息
        coordinatorCommand.execute(new CoordinatorAction(CoordinatorActionEnum.SAVE, distributedTransactionInfo));
        //当前事务保存到ThreadLocal
        TRANSACTION_INFO_THREAD_LOCAL.set(distributedTransactionInfo);
        //设置提供者角色
        distributedTransactionContext.setRole(DistributedRoleEnum.PROVIDER);
        TransactionContextLocal.getInstance().set(distributedTransactionContext);

        DistributedInvocation distributedInvocation = buildDistributedInvocation(point);
        //放入调用链的集合中
        distributedTransactionContext.addInvocationChain(distributedInvocation);
        distributedTransactionContext.addInvocationChainList(distributedInvocation);
        return distributedTransactionInfo;
    }

    public void commitStatus(ProceedingJoinPoint point) {
        DistributedTransactionContext distributedTransactionContext = TransactionContextLocal.getInstance().get();
        DistributedInvocation distributedInvocation = buildDistributedInvocation(point);

        //更新key的状态
        distributedTransactionContext.updateInvocationChain(distributedInvocation, DistributedStatusEnum.COMMIT);

        DistributedTransactionInfo distributedTransactionInfo = getCurrentTransaction();
        if (Objects.nonNull(distributedTransactionInfo)) {
            distributedTransactionInfo.setStatus(DistributedStatusEnum.COMMIT);
            coordinatorCommand.execute(new CoordinatorAction(CoordinatorActionEnum.SAVE, distributedTransactionInfo));
        }
    }

    public void failTransaction(ProceedingJoinPoint point, String errorMsg) {
        DistributedTransactionContext distributedTransactionContext = TransactionContextLocal.getInstance().get();
        DistributedInvocation distributedInvocation = buildDistributedInvocation(point);
        //更新key的状态
        distributedTransactionContext.updateInvocationChain(distributedInvocation, DistributedStatusEnum.COMMIT);

        DistributedTransactionInfo distributedTransactionInfo = getCurrentTransaction();
        if (Objects.nonNull(distributedTransactionInfo)) {
            distributedTransactionInfo.setStatus(DistributedStatusEnum.PRE_FAILURE);
            distributedTransactionInfo.setErrorMsg(errorMsg);
            coordinatorCommand.execute(new CoordinatorAction(CoordinatorActionEnum.SAVE, distributedTransactionInfo));
        }
    }

    public void successTransaction() {
        DistributedTransactionInfo distributedTransactionInfo = getCurrentTransaction();
        if (Objects.nonNull(distributedTransactionInfo) && Objects.equals(distributedTransactionInfo.getStatus(), DistributedStatusEnum.COMMIT)) {
            distributedTransactionInfo.setStatus(DistributedStatusEnum.SUCCESS);
            distributedTransactionInfo.setErrorMsg("分布式事物执行成功");
            coordinatorCommand.execute(new CoordinatorAction(CoordinatorActionEnum.SAVE, distributedTransactionInfo));
        }
    }

    public void cleanThreadLocal() {
        TRANSACTION_INFO_THREAD_LOCAL.remove();
    }

    public void sendMessage() {
        DistributedTransactionContext distributedTransactionContext = TransactionContextLocal.getInstance().get();
        if (Objects.nonNull(distributedTransactionContext)) {
            coordinatorService.sendMessage(distributedTransactionContext);
        }
    }

    private DistributedInvocation buildDistributedInvocation(ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Class<?> clazz = point.getTarget().getClass();
        Object[] args = point.getArgs();
        Class<?>[] parameterTypes = method.getParameterTypes();
        return new DistributedInvocation(clazz, method.getName(), DistributedStatusEnum.BEGIN, parameterTypes, args);
    }

    private DistributedTransactionInfo buildProviderTransaction(ProceedingJoinPoint point, String transID, DistributedStatusEnum status) {
        DistributedTransactionInfo distributedTransactionInfo = new DistributedTransactionInfo(transID);

        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        Class<?> clazz = point.getTarget().getClass();

        distributedTransactionInfo.setStatus(status);
        distributedTransactionInfo.setRole(DistributedRoleEnum.PROVIDER);
        distributedTransactionInfo.setTargetClass(clazz.getName());
        distributedTransactionInfo.setTargetMethod(method.getName());
        return distributedTransactionInfo;
    }

    private DistributedTransactionInfo getCurrentTransaction() {
        return TRANSACTION_INFO_THREAD_LOCAL.get();
    }

}

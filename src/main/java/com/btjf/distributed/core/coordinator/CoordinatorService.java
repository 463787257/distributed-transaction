package com.btjf.distributed.core.coordinator;

import com.btjf.distributed.annotation.DistributedTransaction;
import com.btjf.distributed.common.bean.context.DistributedTransactionContext;
import com.btjf.distributed.common.bean.entity.DistributedTransactionInfo;
import com.btjf.distributed.common.enums.DistributedStatusEnum;
import com.btjf.distributed.common.serializer.ObjectSerializer;
import com.btjf.distributed.core.coordinator.bean.CoordinatorAction;
import com.btjf.distributed.mq.bean.MessageContent;

import java.util.List;

/**
 * @author luol
 * @date 2018/1/29
 * @time 16:09
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public interface CoordinatorService {

    /**
     * 保存本地事务信息
     *
     * @param distributedTransactionInfo 实体对象
     * @return 主键 transID
     */
    String save(DistributedTransactionInfo distributedTransactionInfo, String applicationName);

    /**
     * 根据事务id获取DistributedTransactionInfo
     *
     * @param transID 事务ID
     * @return DistributedTransactionInfo
     */
    List<DistributedTransactionInfo> findByTransID(String transID, String applicationName);


    /**
     * 删除补偿事务信息
     *
     * @param transID 事务ID
     * @return true成功 false 失败
     */
    boolean remove(String transID);


    /**
     * 更新
     *
     * @param distributedTransactionInfo 实体对象
     * @return rows 1 成功
     */
    int update(DistributedTransactionInfo distributedTransactionInfo);


    /**
     * 更新事务失败日志
     * @param distributedTransactionInfo 实体对象
     * @return rows 1 成功
     */
    int updateFailTransaction(DistributedTransactionInfo distributedTransactionInfo);


    /**
     * 更新 List<MythParticipant>  只更新这一个字段数据
     *
     * @param distributedTransactionInfo 实体对象
     * @return rows 1 rows 1 成功
     */
    int updateParticipant(DistributedTransactionInfo distributedTransactionInfo);


    /**
     * 更新本地日志状态
     *
     * @param transID 事务ID
     * @param distributedStatusEnum  状态
     * @return rows 1 rows 1 成功
     */
    int updateStatus(String transID, DistributedStatusEnum distributedStatusEnum);

    /**
     * 提交补偿操作
     *
     * @param coordinatorAction 执行动作
     * @return true 成功
     */
    Boolean submit(CoordinatorAction coordinatorAction);

    /**
     * 接收到mq消息处理
     * @param message 消息体
     * @return true 处理成功  false 处理失败
     */
    Boolean processMessage(MessageContent message);


    /**
     * 发送消息
     * @param distributedTransactionContext 消息体
     * @return true 处理成功  false 处理失败
     */
    Boolean sendMessage(DistributedTransactionContext distributedTransactionContext);
}

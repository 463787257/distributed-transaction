package com.btjf.distributed.core.spi;

import com.btjf.distributed.common.bean.entity.DistributedTransactionInfo;
import com.btjf.distributed.common.enums.RepositorySupportEnum;

import java.util.List;

/**
 * @author luol
 * @date 2018/1/29
 * @time 17:30
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public interface CoordinatorRepository {

    /**
     * 创建本地事务对象
     *
     * @param distributedTransactionInfo 事务对象
     * @return rows 1 成功   0 失败
     */
    int create(DistributedTransactionInfo distributedTransactionInfo, String applicationName);

    /**
     * 删除对象
     *
     * @param transID 事务对象ID
     * @return rows 返回 1 成功  0 失败
     */
    int remove(String transID);

    /**
     * 更新数据
     *
     * @param distributedTransactionInfo 事务对象
     * @return rows 1 成功 0 失败 失败需要抛异常
     */
    int update(DistributedTransactionInfo distributedTransactionInfo);

    /**
     * 更新事务失败日志
     * @param distributedTransactionInfo 实体对象
     * @return rows 1 成功
     */
    int updateFailTransaction(DistributedTransactionInfo distributedTransactionInfo);

    /**
     * 更新 List<Participant>  只更新这一个字段数据
     *
     * @param distributedTransactionInfo 实体对象
     * @return rows 1 成功
     */
    int updateParticipant(DistributedTransactionInfo distributedTransactionInfo);

    /**
     * 更新补偿数据状态
     *
     * @param transID 事务ID
     * @param status  状态
     * @return rows 1 成功
     */
    int updateStatus(String transID, Integer status);

    /**
     * 根据id获取对象
     *
     * @param transID transID
     * @return DistributedTransactionInfo
     */
    List<DistributedTransactionInfo> findByTransID(String transID, String applicationName);

    /**
     * 设置scheme
     *
     * @return scheme 命名
     */
    RepositorySupportEnum getScheme();
}

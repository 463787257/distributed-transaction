package com.btjf.distributed.core.spi.repository;

import com.btjf.distributed.common.bean.entity.DistributedTransactionInfo;
import com.btjf.distributed.common.enums.DistributedStatusEnum;
import com.btjf.distributed.common.enums.RepositorySupportEnum;
import com.btjf.distributed.common.utils.BeanUtil;
import com.btjf.distributed.core.spi.CoordinatorRepository;
import com.btjf.distributed.core.spi.mapper.DistributedTransactionDataMapper;
import com.btjf.distributed.core.spi.model.DistributedTransactionData;
import com.btjf.distributed.core.spi.model.DistributedTransactionDataExample;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author luol
 * @date 2018/1/30
 * @time 14:14
 * @function 功能：该类代码手动注入，不通过注解
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public class JdbcCoordinatorRepository implements CoordinatorRepository {

    @Resource
    private DistributedTransactionDataMapper distributedTransactionDataMapper;

    /**
     * 创建本地事务对象
     *
     * @param distributedTransactionInfo 事务对象
     * @return rows 1 成功   0 失败
     */
    @Override
    public int create(DistributedTransactionInfo distributedTransactionInfo, String applicationName) {
        DistributedTransactionData distributedTransactionData = BeanUtil.convert(distributedTransactionInfo, DistributedTransactionData.class);
        distributedTransactionData.setApplicationName(applicationName);
        return distributedTransactionDataMapper.insertSelective(distributedTransactionData);
    }

    /**
     * 删除对象
     *
     * @param transID 事务对象ID
     * @return rows 返回 1 成功  0 失败
     */
    @Override
    public int remove(String transID) {
        DistributedTransactionDataExample example = new DistributedTransactionDataExample();
        DistributedTransactionDataExample.Criteria criteria = example.createCriteria();
        criteria.andTransIDEqualTo(transID);
        return distributedTransactionDataMapper.deleteByExample(example);
    }

    /**
     * 更新数据
     *
     * @param distributedTransactionInfo 事务对象
     * @return rows 1 成功 0 失败 失败需要抛异常
     */
    @Override
    public int update(DistributedTransactionInfo distributedTransactionInfo) {
        return 0;
    }

    /**
     * 更新事务失败日志
     *
     * @param distributedTransactionInfo 实体对象
     * @return rows 1 成功
     */
    @Override
    public int updateFailTransaction(DistributedTransactionInfo distributedTransactionInfo) {
        return 0;
    }

    /**
     * 更新 List<Participant>  只更新这一个字段数据
     *
     * @param distributedTransactionInfo 实体对象
     * @return rows 1 成功
     */
    @Override
    public int updateParticipant(DistributedTransactionInfo distributedTransactionInfo) {
        return 0;
    }

    /**
     * 更新补偿数据状态
     *
     * @param transID 事务ID
     * @param status  状态
     * @return rows 1 成功
     */
    @Override
    public int updateStatus(String transID, Integer status) {
        return 0;
    }

    /**
     * 根据id获取对象
     *
     * @param transID transID
     * @return DistributedTransactionInfo
     */
    @Override
    public List<DistributedTransactionInfo> findByTransID(String transID, String applicationName) {
        DistributedTransactionDataExample example = new DistributedTransactionDataExample();
        DistributedTransactionDataExample.Criteria criteria = example.createCriteria();
        criteria.andTransIDEqualTo(transID);
        criteria.andApplicationNameEqualTo(applicationName);
        criteria.andStatusNotEqualTo(DistributedStatusEnum.SUCCESS.getValue());
        return BeanUtil.convertList(distributedTransactionDataMapper.selectByExample(example), DistributedTransactionInfo.class);
    }

    /**
     * 设置scheme
     *
     * @return scheme 命名
     */
    @Override
    public RepositorySupportEnum getScheme() {
        return RepositorySupportEnum.DATABASE;
    }
}

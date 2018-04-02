package com.luol.transaction.common.spi.impl;

import com.alibaba.fastjson.JSON;
import com.luol.transaction.common.bean.adapter.MongoAdapter;
import com.luol.transaction.common.bean.model.NtcTransaction;
import com.luol.transaction.common.enums.RepositorySupportEnum;
import com.luol.transaction.common.exception.NtcException;
import com.luol.transaction.common.serializer.ObjectSerializer;
import com.luol.transaction.common.spi.CoordinatorRepository;
import com.luol.transaction.common.utils.AssertUtils;
import com.luol.transaction.common.utils.RepositoryPathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Date;
import java.util.List;

/**
 * @author luol
 * @date 2018/4/2
 * @time 9:47
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public class MongoCoordinatorRepository implements CoordinatorRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoCoordinatorRepository.class);

    private ObjectSerializer objectSerializer;

    //todo 初始化
    private MongoTemplate template;

    private String collectionName;

    private String modelName;

    /**
     * 创建本地事务对象
     *
     * @param ntcTransaction 事务对象
     * @return rows 1 成功   0 失败
     */
    @Override
    public int create(NtcTransaction ntcTransaction) {
        try {
            MongoAdapter mongoAdapter = new MongoAdapter();
            mongoAdapter.setTransID(ntcTransaction.getTransID());
            mongoAdapter.setCreateTime(ntcTransaction.getCreateTime());
            mongoAdapter.setLastTime(ntcTransaction.getLastTime());
            mongoAdapter.setCurrentRetryCounts(ntcTransaction.getCurrentRetryCounts());
            mongoAdapter.setMaxRetryCounts(ntcTransaction.getMaxRetryCounts());
            mongoAdapter.setStatus(ntcTransaction.getNtcStatusEnum().getValue());
            mongoAdapter.setRole(ntcTransaction.getNtcRoleEnum().getValue());
            mongoAdapter.setPattern(ntcTransaction.getPatternEnum().getValue());
            mongoAdapter.setTargetClass(ntcTransaction.getTargetClass());
            mongoAdapter.setTargetMethod(ntcTransaction.getTargetMethod());
            mongoAdapter.setRpcCallChain(JSON.toJSONString(ntcTransaction.getRpcNtcInvocations()));
            final byte[] cache = objectSerializer.serialize(ntcTransaction.getRpcNtcInvocations());
            mongoAdapter.setContents(cache);
            mongoAdapter.setModelName(modelName);
            template.save(mongoAdapter, collectionName);
        } catch (NtcException e) {
            LOGGER.warn("事物存储mongdb发生异常，数据信息：{}", JSON.toJSONString(ntcTransaction));
        }
        return 1;
    }

    /**
     * 删除对象
     *
     * @param transID 事务对象id
     * @return rows 返回 1 成功  0 失败
     */
    @Override
    public int remove(String transID) {
        AssertUtils.notNull(transID);
        Query query = new Query();
        query.addCriteria(new Criteria("transID").is(transID));
        template.remove(query, collectionName);
        return 1;
    }

    /**
     * 更新数据
     *
     * @param ntcTransaction 事务对象
     * @return rows 1 成功 0 失败 失败需要抛异常
     * @throws NtcException 异常
     */
    @Override
    public int update(NtcTransaction ntcTransaction) throws NtcException {
        Query query = new Query();
        query.addCriteria(new Criteria("transID").is(ntcTransaction.getTransID()));
        query.addCriteria(new Criteria("modelName").is(this.modelName));
        Update update = new Update();
        update.set("lastTime", new Date());
        update.set("retriedCount", ntcTransaction.getCurrentRetryCounts());
        update.set("version", ntcTransaction.getVersion() + 1);
        return 0;
    }

    /**
     * 更新事务失败日志
     *
     * @param ntcTransaction 实体对象
     * @return rows 1 成功
     * @throws NtcException 异常信息
     */
    @Override
    public int updateFailTransaction(NtcTransaction ntcTransaction) throws NtcException {
        return 0;
    }

    /**
     * 更新 List<Participant>  只更新这一个字段数据
     *
     * @param ntcTransaction 实体对象
     * @return rows 1 成功
     * @throws NtcException 异常
     */
    @Override
    public int updateParticipant(NtcTransaction ntcTransaction) throws NtcException {
        return 0;
    }

    /**
     * 更新补偿数据状态
     *
     * @param transId 事务id
     * @param status  状态
     * @return rows 1 成功
     * @throws NtcException 异常
     */
    @Override
    public int updateStatus(String transId, Integer status) throws NtcException {
        return 0;
    }

    /**
     * 根据id获取对象
     *
     * @param transID transID
     * @return NtcTransaction
     */
    @Override
    public NtcTransaction findByTransId(String transID) {
        return null;
    }

    /**
     * 获取延迟多长时间后的事务信息,只要为了防止并发的时候，刚新增的数据被执行
     *
     * @param date 延迟后的时间
     * @return List<NtcTransaction>
     */
    @Override
    public List<NtcTransaction> listAllByDelay(Date date) {
        return null;
    }

    /**
     * 初始化操作
     *
     * @param modelName 模块名称
     * @throws NtcException 自定义异常
     */
    @Override
    public void init(String modelName) throws NtcException {
        this.collectionName = RepositoryPathUtils.buildMongoTableName(modelName);
        this.modelName = RepositoryPathUtils.buildMongoTableName(modelName);
        //todo 开启mongdb连接
    }

    /**
     * 设置scheme
     *
     * @return scheme 命名
     */
    @Override
    public RepositorySupportEnum getScheme() {
        return RepositorySupportEnum.MONGODB;
    }

    /**
     * 设置序列化信息
     *
     * @param objectSerializer 序列化实现
     */
    @Override
    public void setSerializer(ObjectSerializer objectSerializer) {
        this.objectSerializer = objectSerializer;
    }
}

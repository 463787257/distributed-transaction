package com.luol.transaction.common.spi.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.luol.transaction.common.bean.adapter.MongoAdapter;
import com.luol.transaction.common.bean.entity.NtcInvocation;
import com.luol.transaction.common.bean.model.NtcTransaction;
import com.luol.transaction.common.enums.NtcRoleEnum;
import com.luol.transaction.common.enums.NtcStatusEnum;
import com.luol.transaction.common.enums.PatternEnum;
import com.luol.transaction.common.enums.RepositorySupportEnum;
import com.luol.transaction.common.exception.NtcException;
import com.luol.transaction.common.serializer.ObjectSerializer;
import com.luol.transaction.common.spi.CoordinatorRepository;
import com.luol.transaction.common.utils.AssertUtils;
import com.luol.transaction.common.utils.RepositoryPathUtils;
import com.mongodb.WriteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

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

    /**
     * 创建本地事务对象
     *
     * @param ntcTransaction 事务对象
     * @return rows 1 成功   0 失败
     */
    @Override
    public int create(NtcTransaction ntcTransaction) {
        AssertUtils.notNull(ntcTransaction, "ntcTransaction不能为空");
        try {
            MongoAdapter mongoAdapter = new MongoAdapter(ntcTransaction);
            final byte[] cache = objectSerializer.serialize(ntcTransaction.getRpcNtcInvocations());
            mongoAdapter.setContents(cache);
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
        AssertUtils.notNull(transID, "事物ID不能为空");
        Query query = new Query();
        query.addCriteria(new Criteria("transID").is(transID));
        template.remove(query, collectionName);
        return 1;
    }

    /**
     * 更新数据 todo 查询条件中 targetClass 是否需要加上？
     *
     * @param ntcTransaction 事务对象
     * @return rows 1 成功 0 失败 失败需要抛异常
     * @throws NtcException 异常
     */
    @Override
    public int update(NtcTransaction ntcTransaction) throws NtcException {
        AssertUtils.notNull(ntcTransaction, "ntcTransaction不能为空");
        Query query = new Query();
        Criteria criteria = Criteria.where("transID").is(ntcTransaction.getTransID())
                .and("targetClass").is(ntcTransaction.getTargetClass()).and("targetMethod").is(ntcTransaction.getTargetMethod());
        query.addCriteria(criteria);
        Update update = new Update();
        update.set("lastTime", new Date());
        update.set("currentRetryCounts", ntcTransaction.getCurrentRetryCounts());
        update.set("version", ntcTransaction.getVersion() + 1);
        if (Objects.nonNull(ntcTransaction.getNtcStatusEnum())) {
            update.set("status", ntcTransaction.getNtcStatusEnum().getValue());
        }
        if (Objects.nonNull(ntcTransaction.getPatternEnum())) {
            update.set("pattern", ntcTransaction.getPatternEnum().getValue());
        }
        final WriteResult writeResult = template.updateFirst(query, update, MongoAdapter.class, collectionName);
        if (writeResult.getN() <= 0) {
            //throw new NtcRuntimeException("更新数据异常!");
        }
        return 1;
    }

    /**
     * 根据id获取对象
     *
     * @param transID transID
     * @return NtcTransaction
     */
    @Override
    public NtcTransaction findByTransID(String transID) {
        AssertUtils.notNull(transID, "transID不能为空");
        Criteria criteria = Criteria.where("transID").is(transID);
        Query query = new Query(criteria);
        MongoAdapter cache = template.findOne(query, MongoAdapter.class, collectionName);
        return buildByCache(cache);
    }

    /**
     * 根据id,类名，方法名获取对象
     *
     * @param transID      transID
     * @param targetClass  类名
     * @param targetMethod 方法名
     * @return NtcTransaction
     */
    @Override
    public NtcTransaction getByTransIDAndName(String transID, String targetClass, String targetMethod) {
        AssertUtils.notNull(transID, "transID不能为空");
        Criteria criteria = Criteria.where("transID").is(transID).and("targetClass").is(targetClass).and("targetMethod").is(targetMethod);
        Query query = new Query(criteria);
        MongoAdapter cache = template.findOne(query, MongoAdapter.class, collectionName);
        return buildByCache(cache);
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

    private NtcTransaction buildByCache(MongoAdapter cache) {
        NtcTransaction ntcTransaction = new NtcTransaction();
        ntcTransaction.setTransID(cache.getTransID());
        ntcTransaction.setCreateTime(cache.getCreateTime());
        ntcTransaction.setLastTime(cache.getLastTime());
        ntcTransaction.setCurrentRetryCounts(cache.getCurrentRetryCounts());
        ntcTransaction.setVersion(cache.getVersion());
        ntcTransaction.setMaxRetryCounts(cache.getMaxRetryCounts());
        ntcTransaction.setNtcRoleEnum(NtcRoleEnum.objectOf(cache.getRole()));
        ntcTransaction.setNtcStatusEnum(NtcStatusEnum.objectOf(cache.getStatus()));
        ntcTransaction.setTargetClass(cache.getTargetClass());
        ntcTransaction.setTargetMethod(cache.getTargetMethod());
        ntcTransaction.setPatternEnum(PatternEnum.objectOf(cache.getPattern()));
        //ntcTransaction.setRpcNtcInvocations(JSON.parseObject(cache.getRpcCallChain(), new TypeReference<List<NtcInvocation>>() {}));
        try {
            List<NtcInvocation> rpcNtcInvocations = (List<NtcInvocation>) objectSerializer.deSerialize(cache.getContents(), CopyOnWriteArrayList.class);
            ntcTransaction.setRpcNtcInvocations(rpcNtcInvocations);
        } catch (NtcException e) {
            LOGGER.error("mongodb 反序列化异常:{}", e.getLocalizedMessage());
        }
        return ntcTransaction;
    }
}

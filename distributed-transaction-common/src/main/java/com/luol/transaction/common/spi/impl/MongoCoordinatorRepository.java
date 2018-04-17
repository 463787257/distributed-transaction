package com.luol.transaction.common.spi.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.base.Splitter;
import com.luol.transaction.common.bean.adapter.MongoAdapter;
import com.luol.transaction.common.bean.entity.NtcInvocation;
import com.luol.transaction.common.bean.model.NtcTransaction;
import com.luol.transaction.common.config.NtcConfig;
import com.luol.transaction.common.config.NtcMongoConfig;
import com.luol.transaction.common.enums.NtcRoleEnum;
import com.luol.transaction.common.enums.NtcStatusEnum;
import com.luol.transaction.common.enums.PatternEnum;
import com.luol.transaction.common.enums.RepositorySupportEnum;
import com.luol.transaction.common.exception.NtcException;
import com.luol.transaction.common.serializer.ObjectSerializer;
import com.luol.transaction.common.spi.CoordinatorRepository;
import com.luol.transaction.common.utils.AssertUtils;
import com.luol.transaction.common.utils.RepositoryPathUtils;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.WriteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

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

    private MongoTemplate template;

    private String collectionName;

    private String taskName;

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
     * 更新数据
     *
     * @param ntcTransaction 事务对象
     * @return rows 1 成功 0 失败 失败需要抛异常
     * @throws NtcException 异常
     */
    @Override
    public int update(NtcTransaction ntcTransaction) throws NtcException {
        AssertUtils.notNull(ntcTransaction, "ntcTransaction不能为空");
        Query query = new Query();
        Criteria criteria = Criteria.where("transID").is(ntcTransaction.getTransID());
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
     * @param ntcConfig
     * @throws NtcException 自定义异常
     */
    @Override
    public void init(NtcConfig ntcConfig) throws NtcException {
        LOGGER.warn("构建mongdb连接===开始");
        NtcMongoConfig ntcMongoConfig = ntcConfig.getNtcMongoConfig();
        if (Objects.isNull(ntcMongoConfig)) {
            throw new NtcException("请设置mongdb初始化配置");
        }
        this.collectionName = ntcMongoConfig.getCollectionName();
        this.taskName = ntcMongoConfig.getTaskName();
        MongoClientFactoryBean clientFactoryBean = new MongoClientFactoryBean();
        MongoCredential credential = MongoCredential.createScramSha1Credential(ntcMongoConfig.getMongoUserName(),
                ntcMongoConfig.getMongoDbName(),
                ntcMongoConfig.getMongoUserPwd().toCharArray());
        clientFactoryBean.setCredentials(new MongoCredential[]{
                credential
        });
        List<String> urls = Splitter.on(",").trimResults().splitToList(ntcMongoConfig.getMongoDbUrl());

        final ServerAddress[] sds = urls.stream().map(url -> {
            List<String> adds = Splitter.on(":").trimResults().splitToList(url);
            InetSocketAddress address = new InetSocketAddress(adds.get(0), Integer.parseInt(adds.get(1)));
            return new ServerAddress(address);
        }).collect(Collectors.toList()).toArray(new ServerAddress[]{});

        clientFactoryBean.setReplicaSetSeeds(sds);
        try {
            clientFactoryBean.afterPropertiesSet();
            template = new MongoTemplate(clientFactoryBean.getObject(), ntcMongoConfig.getMongoDbName());
        } catch (Exception e) {
            LOGGER.warn("构建mongdb连接===出错：", e);
        }
        LOGGER.warn("构建mongdb连接===结束");
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

    /**
     * 添加记录正在补偿任务
     *
     * @param transID
     */
    @Override
    public int addCompensationTask(String transID) {
        AssertUtils.notNull(transID, "ntcTransaction不能为空");
        Map<String, String> hashMap = new HashMap<>();
        try {
            hashMap.put("transID", transID);
            hashMap.put("isEnable", "1");
            template.save(hashMap, taskName);
        } catch (NtcException e) {
            LOGGER.warn("事物存储mongdb发生异常，数据信息：{}", JSON.toJSONString(hashMap));
        }
        return 1;
    }

    /**
     * 查询补偿任务
     *
     * @param transID 事物ID
     * @return NtcTransaction
     */
    @Override
    public Boolean isExitCompensationTask(String transID) {
        AssertUtils.notNull(transID, "transID不能为空");
        Criteria criteria = Criteria.where("transID").is(transID);
        Query query = new Query(criteria);
        Map<String, String> hashMap = template.findOne(query, HashMap.class, taskName);
        if (Objects.nonNull(hashMap) && Objects.equals(hashMap.get("isEnable"), "1")) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    /**
     * 获取date之前失败的事物信息
     *
     * @param date 时间
     * @return List<NtcTransaction>
     */
    @Override
    public List<NtcTransaction> findAllNeedCompensation(Date date) {
        return null;
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

package com.luol.transaction.common.spi.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.luol.transaction.common.bean.model.NtcTransaction;
import com.luol.transaction.common.config.NtcConfig;
import com.luol.transaction.common.config.NtcRedisConfig;
import com.luol.transaction.common.enums.NtcStatusEnum;
import com.luol.transaction.common.enums.RepositorySupportEnum;
import com.luol.transaction.common.exception.NtcException;
import com.luol.transaction.common.jedis.JedisClient;
import com.luol.transaction.common.jedis.JedisClientCluster;
import com.luol.transaction.common.jedis.JedisClientSingle;
import com.luol.transaction.common.serializer.ObjectSerializer;
import com.luol.transaction.common.spi.CoordinatorRepository;
import com.luol.transaction.common.utils.RepositoryConvertUtils;
import com.luol.transaction.common.utils.RepositoryPathUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author luol
 * @date 2018/4/4
 * @time 11:38
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public class RedisCoordinatorRepository implements CoordinatorRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisCoordinatorRepository.class);

    private ObjectSerializer objectSerializer;

    private JedisClient jedisClient;

    private String keyPrefix;

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
            final String redisKey = RepositoryPathUtils.buildRedisKey(keyPrefix, ntcTransaction.getTransID());
            jedisClient.set(redisKey, RepositoryConvertUtils.convert(ntcTransaction, objectSerializer));
            return 1;
        } catch (Exception e) {
            throw new NtcException(e);
        }
    }

    /**
     * 删除对象
     *
     * @param transID 事务对象id
     * @return rows 返回 1 成功  0 失败
     */
    @Override
    public int remove(String transID) {
        try {
            final String redisKey = RepositoryPathUtils.buildRedisKey(keyPrefix, transID);
            return jedisClient.del(redisKey).intValue();
        } catch (Exception e) {
            throw new NtcException(e);
        }
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
        try {
            final String redisKey = RepositoryPathUtils.buildRedisKey(keyPrefix, ntcTransaction.getTransID());
            byte[] contents = jedisClient.get(redisKey.getBytes());
            NtcTransaction transformBean = RepositoryConvertUtils.transformBean(contents, objectSerializer);
            transformBean.setVersion(transformBean.getVersion() + 1);
            transformBean.setLastTime(new Date());
            transformBean.setCurrentRetryCounts(ntcTransaction.getCurrentRetryCounts());
            transformBean.setNtcRoleEnum(Objects.nonNull(ntcTransaction.getNtcRoleEnum()) ? ntcTransaction.getNtcRoleEnum() : transformBean.getNtcRoleEnum());
            transformBean.setNtcStatusEnum(Objects.nonNull(ntcTransaction.getNtcStatusEnum()) ? ntcTransaction.getNtcStatusEnum() : transformBean.getNtcStatusEnum());
            transformBean.setPatternEnum(Objects.nonNull(ntcTransaction.getPatternEnum()) ? ntcTransaction.getPatternEnum() : transformBean.getPatternEnum());
            transformBean.setRpcNtcInvocations(CollectionUtils.isEmpty(ntcTransaction.getRpcNtcInvocations()) ? transformBean.getRpcNtcInvocations() : ntcTransaction.getRpcNtcInvocations());
            jedisClient.set(redisKey, RepositoryConvertUtils.convert(transformBean, objectSerializer));
            return 1;
        } catch (Exception e) {
            throw new NtcException(e);
        }
    }

    /**
     * 根据id获取对象
     *
     * @param transID transID
     * @return NtcTransaction
     */
    @Override
    public NtcTransaction findByTransID(String transID) {
        try {
            final String redisKey = RepositoryPathUtils.buildRedisKey(keyPrefix, transID);
            byte[] contents = jedisClient.get(redisKey.getBytes());
            return RepositoryConvertUtils.transformBean(contents, objectSerializer);
        } catch (Exception e) {
            throw new NtcException(e);
        }
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
        try {
            final String redisKey = RepositoryPathUtils.buildRedisKey(transID, targetClass + targetMethod);
            byte[] contents = jedisClient.get(redisKey.getBytes());
            return RepositoryConvertUtils.transformBean(contents, objectSerializer);
        } catch (Exception e) {
            throw new NtcException(e);
        }
    }

    /**
     * 初始化操作
     *
     * @param ntcConfig 模块名称
     * @throws NtcException 自定义异常
     */
    @Override
    public void init(NtcConfig ntcConfig) throws NtcException {
        this.keyPrefix = "ntc-redis-config" + ntcConfig.getModelName();
        this.modelName = ntcConfig.getModelName();
        buildJedisPool(ntcConfig);
    }

    private void buildJedisPool(NtcConfig ntcConfig) {
        LOGGER.warn("构建redis配置信息===开始");
        NtcRedisConfig ntcRedisConfig = ntcConfig.getNtcRedisConfig();
        JedisPoolConfig config = new JedisPoolConfig();
        if (Objects.isNull(config)) {
            throw new NtcException("请设置redis初始化配置");
        }
        config.setMaxIdle(ntcRedisConfig.getMaxIdle());
        //最小空闲连接数, 默认0
        config.setMinIdle(ntcRedisConfig.getMinIdle());
        //最大连接数, 默认8个
        config.setMaxTotal(ntcRedisConfig.getMaxTotal());
        //获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1
        config.setMaxWaitMillis(ntcRedisConfig.getMaxWaitMillis());
        //在获取连接的时候检查有效性, 默认false
        config.setTestOnBorrow(ntcRedisConfig.getTestOnBorrow());
        //返回一个jedis实例给连接池时，是否检查连接可用性（ping()）
        config.setTestOnReturn(ntcRedisConfig.getTestOnReturn());
        //在空闲时检查有效性, 默认false
        config.setTestWhileIdle(ntcRedisConfig.getTestWhileIdle());
        //逐出连接的最小空闲时间 默认1800000毫秒(30分钟 )
        config.setMinEvictableIdleTimeMillis(ntcRedisConfig.getMinEvictableIdleTimeMillis());
        //对象空闲多久后逐出, 当空闲时间>该值 ，且 空闲连接>最大空闲数 时直接逐出,不再根据MinEvictableIdleTimeMillis判断  (默认逐出策略)，默认30m
        config.setSoftMinEvictableIdleTimeMillis(ntcRedisConfig.getSoftMinEvictableIdleTimeMillis());
        //逐出扫描的时间间隔(毫秒) 如果为负数,则不运行逐出线程, 默认-1
        config.setTimeBetweenEvictionRunsMillis(ntcRedisConfig.getTimeBetweenEvictionRunsMillis());
        //每次逐出检查时 逐出的最大数目 如果为负数就是 : 1/abs(n), 默认3
        config.setNumTestsPerEvictionRun(ntcRedisConfig.getNumTestsPerEvictionRun());

        JedisPool jedisPool;
        //如果是集群模式
        if (ntcRedisConfig.getCluster()) {
            LOGGER.warn("构造redis集群模式");
            final String clusterUrl = ntcRedisConfig.getClusterUrl();
            final Set<HostAndPort> hostAndPorts = Splitter.on(clusterUrl)
                    .splitToList(";").stream()
                    .map(HostAndPort::parseString).collect(Collectors.toSet());
            JedisCluster jedisCluster = new JedisCluster(hostAndPorts, config);
            jedisClient = new JedisClientCluster(jedisCluster);
        } else {
            if (StringUtils.isNoneBlank(ntcRedisConfig.getPassword())) {
                jedisPool = new JedisPool(config, ntcRedisConfig.getHostName(), ntcRedisConfig.getPort(), ntcRedisConfig.getTimeOut(), ntcRedisConfig.getPassword());
            } else {
                jedisPool = new JedisPool(config, ntcRedisConfig.getHostName(), ntcRedisConfig.getPort(), ntcRedisConfig.getTimeOut());
            }
            jedisClient = new JedisClientSingle(jedisPool);
        }
        LOGGER.warn("构建redis配置信息===结束");
    }

    /**
     * 设置scheme
     *
     * @return scheme 命名
     */
    @Override
    public RepositorySupportEnum getScheme() {
        return RepositorySupportEnum.REDIS;
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
        try {
            jedisClient.hset(modelName + keyPrefix , transID, "1");
            return 1;
        } catch (Exception e) {
            throw new NtcException(e);
        }
    }

    /**
     * 查询补偿任务
     *
     * @param transID 事物ID
     * @return NtcTransaction
     */
    @Override
    public Boolean isExitCompensationTask(String transID) {
        try {
            String compensationTask = jedisClient.hget(modelName + keyPrefix, transID);
            if (StringUtils.isNotBlank(compensationTask)) {
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        } catch (Exception e) {
            throw new NtcException(e);
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
        List<NtcTransaction> transactions = Lists.newArrayList();
        Set<byte[]> keys = jedisClient.keys((keyPrefix + "*").getBytes());
        for (byte[] key : keys) {
            byte[] contents = jedisClient.get(key);
            if (contents != null) {
                transactions.add(RepositoryConvertUtils.transformBean(contents, objectSerializer));
            }
        }
        if (!CollectionUtils.isEmpty(transactions)) {
            return transactions.stream().filter(ntcTransaction -> !Objects.equals(ntcTransaction.getNtcStatusEnum(), NtcStatusEnum.SUCCESS))
                    .filter(ntcTransaction -> ntcTransaction.getLastTime().compareTo(date) > 0).collect(Collectors.toList());
        }
        return null;
    }
}

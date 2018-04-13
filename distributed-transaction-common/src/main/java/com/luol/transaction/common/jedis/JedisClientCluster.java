package com.luol.transaction.common.jedis;

import redis.clients.jedis.JedisCluster;

import java.util.Set;

/**
 * @author luol
 * @date 2018/4/4
 * @time 14:25
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public class JedisClientCluster implements JedisClient {

    private JedisCluster jedisCluster;

    public JedisClientCluster(JedisCluster jedisCluster) {
        this.jedisCluster = jedisCluster;
    }

    /**
     * set 操作
     *
     * @param key   key
     * @param value key
     * @return
     */
    @Override
    public String set(String key, String value) {
        return jedisCluster.set(key, value);
    }

    /**
     * set 操作
     *
     * @param key   key
     * @param value key
     * @return
     */
    @Override
    public String set(String key, byte[] value) {
        return jedisCluster.set(key.getBytes(),value);
    }

    /**
     * 批量删除key
     *
     * @param keys key集合
     * @return 数量
     */
    @Override
    public Long del(String... keys) {
        return jedisCluster.del(keys);
    }

    /**
     * 根据key获取
     *
     * @param key redis key
     * @return String
     */
    @Override
    public String get(String key) {
        return jedisCluster.get(key);
    }

    /**
     * 根据key获取
     *
     * @param key redis key
     * @return byte[]
     */
    @Override
    public byte[] get(byte[] key) {
        return jedisCluster.get(key);
    }

    /**
     * 根据key 模糊匹配
     *
     * @param pattern redis key
     * @return Set<byte[]>
     */
    @Override
    public Set<byte[]> keys(byte[] pattern) {
        return jedisCluster.hkeys(pattern);
    }

    /**
     * 根据key 模糊匹配
     *
     * @param key redis key
     * @return Set<String>
     */
    @Override
    public Set<String> keys(String key) {
        return jedisCluster.hkeys(key);
    }

    /**
     * hash set值
     *
     * @param key   redis key
     * @param item  hash key
     * @param value 值
     * @return 条数
     */
    @Override
    public Long hset(String key, String item, String value) {
        return jedisCluster.hset(key, item, value);
    }

    /**
     * hash get 值
     *
     * @param key  key
     * @param item hash key
     * @return value
     */
    @Override
    public String hget(String key, String item) {
        return jedisCluster.hget(key, item);
    }

    /**
     * hash del 值
     *
     * @param key  key
     * @param item hash key
     * @return 数量
     */
    @Override
    public Long hdel(String key, String item) {
        return jedisCluster.hdel(key, item);
    }

    /**
     * 增加
     *
     * @param key key
     * @return Long
     */
    @Override
    public Long incr(String key) {
        return jedisCluster.incr(key);
    }

    /**
     * 减少
     *
     * @param key key
     * @return Long
     */
    @Override
    public Long decr(String key) {
        return jedisCluster.decr(key);
    }

    /**
     * 设置key的过期时间
     *
     * @param key    key
     * @param second 过期时间 秒
     * @return Long
     */
    @Override
    public Long expire(String key, int second) {
        return jedisCluster.expire(key, second);
    }

    /**
     * 分页获取zsort
     *
     * @param key   key
     * @param start 开始
     * @param end   结束
     * @return Set<String>
     */
    @Override
    public Set<String> zrange(String key, long start, long end) {
        return jedisCluster.zrange(key, start, end);
    }
}

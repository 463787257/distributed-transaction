package com.luol.transaction.common.jedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Set;

/**
 * @author luol
 * @date 2018/4/4
 * @time 14:29
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public class JedisClientSingle implements JedisClient {

    private JedisPool jedisPool;

    public JedisClientSingle(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
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
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.set(key, value);
        }
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
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.set(key.getBytes(), value);
        }
    }

    /**
     * 批量删除key
     *
     * @param keys key集合
     * @return 数量
     */
    @Override
    public Long del(String... keys) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.del(keys);
        }
    }

    /**
     * 根据key获取
     *
     * @param key redis key
     * @return String
     */
    @Override
    public String get(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        }
    }

    /**
     * 根据key获取
     *
     * @param key redis key
     * @return byte[]
     */
    @Override
    public byte[] get(byte[] key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        }
    }

    /**
     * 根据key 模糊匹配
     *
     * @param pattern redis key
     * @return Set<byte[]>
     */
    @Override
    public Set<byte[]> keys(byte[] pattern) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.keys(pattern);
        }
    }

    /**
     * 根据key 模糊匹配
     *
     * @param key redis key
     * @return Set<String>
     */
    @Override
    public Set<String> keys(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.keys(key);
        }
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
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hset(key, item, value);
        }
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
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hget(key, item);
        }
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
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hdel(key, item);
        }
    }

    /**
     * 增加
     *
     * @param key key
     * @return Long
     */
    @Override
    public Long incr(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.incr(key);
        }
    }

    /**
     * 减少
     *
     * @param key key
     * @return Long
     */
    @Override
    public Long decr(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.decr(key);
        }
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
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.expire(key, second);
        }
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
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrange(key, start, end);
        }
    }
}

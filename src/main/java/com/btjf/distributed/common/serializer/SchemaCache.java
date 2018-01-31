package com.btjf.distributed.common.serializer;

import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


/**
 * @author luol
 * @date 2018/1/30
 * @time 9:45
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public class SchemaCache {
    private static class SchemaCacheHolder {
        private static SchemaCache cache = new SchemaCache();
    }

    protected static SchemaCache getInstance() {
        return SchemaCacheHolder.cache;
    }

    private Cache<Class<?>, Schema<?>> cache = CacheBuilder.newBuilder()
            .maximumSize(1024).expireAfterWrite(1, TimeUnit.HOURS)
            .build();

    private Schema<?> get(final Class<?> cls, Cache<Class<?>, Schema<?>> cache) {
        try {
            return cache.get(cls, () -> RuntimeSchema.createFrom(cls));
        } catch (ExecutionException e) {
            return null;
        }
    }

    public Schema<?> get(final Class<?> cls) {
        return get(cls, cache);
    }
}

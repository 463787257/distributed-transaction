package com.btjf.distributed.common.serializer;

import com.btjf.distributed.common.enums.SerializeEnum;
import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author luol
 * @date 2018/1/30
 * @time 9:44
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public class ProtostuffSerializer implements ObjectSerializer {
    private static final SchemaCache CACHED_SCHEMA = SchemaCache.getInstance();
    private static final Objenesis OBJENESIS_STD = new ObjenesisStd(true);

    private static <T> Schema<T> getSchema(Class<T> cls) {
        return (Schema<T>) CACHED_SCHEMA.get(cls);
    }


    /**
     * 序列化对象
     *
     * @param obj 需要序更列化的对象
     * @return byte []
     */
    @Override
    public byte[] serialize(Object obj){
        Class cls = obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            Schema schema = getSchema(cls);
            ProtostuffIOUtil.writeTo(outputStream, obj, schema, buffer);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
        return outputStream.toByteArray();
    }

    /**
     * 反序列化对象
     *
     * @param param 需要反序列化的byte []
     * @param clazz clazz
     * @return 对象
     */
    @Override
    public <T> T deSerialize(byte[] param, Class<T> clazz){
        T object;
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(param);
            Class cls = clazz;
            object = OBJENESIS_STD.newInstance((Class<T>) cls);
            Schema schema = getSchema(cls);
            ProtostuffIOUtil.mergeFrom(inputStream, object, schema);
            return object;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 设置scheme
     *
     * @return scheme 命名
     */
    @Override
    public SerializeEnum getScheme() {
        return SerializeEnum.PROTOSTUFF;
    }
}

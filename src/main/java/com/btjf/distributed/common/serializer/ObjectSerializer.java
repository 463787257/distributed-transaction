package com.btjf.distributed.common.serializer;

import com.btjf.distributed.common.enums.SerializeEnum;

/**
 * @author luol
 * @date 2018/1/30
 * @time 9:29
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public interface ObjectSerializer {
    /**
     * 序列化对象
     *
     * @param obj 需要序更列化的对象
     * @return byte []
     */
    byte[] serialize(Object obj);


    /**
     * 反序列化对象
     *
     * @param param 需要反序列化的byte []
     * @param clazz java对象
     * @param <T>   泛型支持
     * @return 对象
     */
    <T> T deSerialize(byte[] param, Class<T> clazz);


    /**
     * 设置scheme
     *
     * @return scheme 命名
     */
    SerializeEnum getScheme();
}

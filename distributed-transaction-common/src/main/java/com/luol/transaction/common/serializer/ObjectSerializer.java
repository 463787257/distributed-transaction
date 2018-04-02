package com.luol.transaction.common.serializer;

import com.luol.transaction.common.enums.SerializeEnum;
import com.luol.transaction.common.exception.NtcException;

/**
 * @author luol
 * @date 2018/3/30
 * @time 11:44
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
     * @throws NtcException 异常信息
     */
    byte[] serialize(Object obj) throws NtcException;


    /**
     * 反序列化对象
     *
     * @param param 需要反序列化的byte []
     * @param clazz java对象
     * @param <T>   泛型支持
     * @return 对象
     * @throws NtcException 异常信息
     */
    <T> T deSerialize(byte[] param, Class<T> clazz) throws NtcException;


    /**
     * 设置scheme
     *
     * @return scheme 命名
     */
    SerializeEnum getScheme();
}

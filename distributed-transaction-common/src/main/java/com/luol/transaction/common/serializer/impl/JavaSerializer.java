package com.luol.transaction.common.serializer.impl;

import com.luol.transaction.common.enums.SerializeEnum;
import com.luol.transaction.common.exception.NtcException;
import com.luol.transaction.common.serializer.ObjectSerializer;

import java.io.*;

/**
 * @author luol
 * @date 2018/3/30
 * @time 14:37
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public class JavaSerializer implements ObjectSerializer {
    /**
     * 序列化对象
     *
     * @param obj 需要序更列化的对象
     * @return byte []
     * @throws NtcException 异常信息
     */
    @Override
    public byte[] serialize(Object obj) throws NtcException {
        try (ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream()) {
            ObjectOutput objectOutput = new ObjectOutputStream(arrayOutputStream);
            objectOutput.writeObject(obj);
            objectOutput.flush();
            objectOutput.close();
            return arrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new NtcException("JAVA serialize error " + e.getMessage());
        }
    }

    /**
     * 反序列化对象
     *
     * @param param 需要反序列化的byte []
     * @param clazz java对象
     * @return 对象
     * @throws NtcException 异常信息
     */
    @Override
    public <T> T deSerialize(byte[] param, Class<T> clazz) throws NtcException {
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(param);
        try {
            ObjectInput input = new ObjectInputStream(arrayInputStream);
            return (T) input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new NtcException("JAVA deSerialize error " + e.getMessage());
        }
    }

    /**
     * 设置scheme
     *
     * @return scheme 命名
     */
    @Override
    public SerializeEnum getScheme() {
        return SerializeEnum.JDK;
    }
}

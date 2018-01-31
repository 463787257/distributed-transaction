package com.btjf.distributed.common.serializer;

import com.btjf.distributed.common.enums.SerializeEnum;
import java.io.*;

/**
 * @author luol
 * @date 2018/1/30
 * @time 9:29
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public class JavaSerializer implements ObjectSerializer {
    @Override
    public byte[] serialize(Object obj){
        try (ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream()
             ; ObjectOutput objectOutput = new ObjectOutputStream(arrayOutputStream);) {
            objectOutput.writeObject(obj);
            objectOutput.flush();
            objectOutput.close();
            return arrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("JAVA serialize error " + e.getMessage());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T deSerialize(byte[] param, Class<T> clazz){
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(param);
        try {
            ObjectInput input = new ObjectInputStream(arrayInputStream);
            return (T) input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("JAVA deSerialize error " + e.getMessage());
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

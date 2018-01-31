package com.btjf.distributed.common.serializer;

import com.btjf.distributed.common.enums.SerializeEnum;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author luol
 * @date 2018/1/30
 * @time 9:36
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public class KryoSerializer implements ObjectSerializer {

    /**
     * 序列化
     *
     * @param obj 需要序更列化的对象
     * @return 序列化后的byte 数组
     */
    @Override
    public byte[] serialize(Object obj){
        byte[] bytes;
        //try(){}catch(){},try括号后面的会在try后自动释放资源
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            //获取kryo对象
            Kryo kryo = new Kryo();
            Output output = new Output(outputStream);
            kryo.writeObject(output, obj);
            bytes = output.toBytes();
            output.flush();
        } catch (Exception ex) {
            throw new RuntimeException("kryo serialize error" + ex.getMessage());
        }
        return bytes;
    }

    /**
     * 反序列化
     *
     * @param param 需要反序列化的byte []
     * @return 序列化对象
     */
    @Override
    public <T> T deSerialize(byte[] param, Class<T> clazz) {
        T object;
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(param)) {
            Kryo kryo = new Kryo();
            Input input = new Input(inputStream);
            object = kryo.readObject(input, clazz);
            input.close();
        } catch (Exception e) {
            throw new RuntimeException("kryo deSerialize error" + e.getMessage());
        }
        return object;
    }

    /**
     * 设置scheme
     *
     * @return scheme 命名
     */
    @Override
    public SerializeEnum getScheme() {
        return SerializeEnum.KRYO;
    }

}

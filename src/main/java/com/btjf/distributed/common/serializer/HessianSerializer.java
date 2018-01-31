package com.btjf.distributed.common.serializer;

import com.alibaba.com.caucho.hessian.io.Hessian2Input;
import com.alibaba.com.caucho.hessian.io.Hessian2Output;
import com.btjf.distributed.common.enums.SerializeEnum;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author luol
 * @date 2018/1/30
 * @time 9:33
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public class HessianSerializer implements ObjectSerializer {
    /**
     * 序列化对象
     *
     * @param obj 需要序更列化的对象
     * @return byte []
     */
    @Override
    public byte[] serialize(Object obj) {
        Hessian2Output hos;
        try(ByteArrayOutputStream baos=new ByteArrayOutputStream();) {
            hos = new Hessian2Output(baos);
            hos.writeObject(obj);
            hos.flush();
            return baos.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException("Hessian serialize error " + ex.getMessage());
        }
    }

    /**
     * 反序列化对象
     *
     * @param param 需要反序列化的byte []
     * @param clazz java对象
     * @return 对象
     */
    @Override
    public <T> T deSerialize(byte[] param, Class<T> clazz) {
        ByteArrayInputStream bios;
        try {
            bios = new ByteArrayInputStream(param);
            Hessian2Input his = new Hessian2Input(bios);
            return (T) his.readObject();
        } catch (IOException e) {
            throw new RuntimeException("Hessian deSerialize error " + e.getMessage());
        }
    }

    /**
     * 设置scheme
     *
     * @return scheme 命名
     */
    @Override
    public SerializeEnum getScheme() {
        return SerializeEnum.HESSIAN;
    }
}

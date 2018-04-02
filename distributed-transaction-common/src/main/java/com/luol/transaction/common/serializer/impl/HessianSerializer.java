package com.luol.transaction.common.serializer.impl;

import com.alibaba.com.caucho.hessian.io.Hessian2Input;
import com.alibaba.com.caucho.hessian.io.Hessian2Output;
import com.luol.transaction.common.enums.SerializeEnum;
import com.luol.transaction.common.exception.NtcException;
import com.luol.transaction.common.serializer.ObjectSerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author luol
 * @date 2018/3/30
 * @time 14:36
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
     * @throws NtcException 异常信息
     */
    @Override
    public byte[] serialize(Object obj) throws NtcException {
        ByteArrayOutputStream baos;
        try {
            baos = new ByteArrayOutputStream();
            Hessian2Output hos = new Hessian2Output(baos);
            hos.writeObject(obj);
            hos.flush();
            hos.close();
        } catch (IOException ex) {
            throw new NtcException("Hessian serialize error " + ex.getMessage());
        }
        return baos.toByteArray();
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
        ByteArrayInputStream bios;
        try {
            bios = new ByteArrayInputStream(param);
            Hessian2Input his = new Hessian2Input(bios);
            return (T) his.readObject();
        } catch (IOException e) {
            throw new NtcException("Hessian deSerialize error " + e.getMessage());
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

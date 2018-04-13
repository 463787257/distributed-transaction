package com.luol.transaction.common.spi;

import com.luol.transaction.common.bean.model.NtcTransaction;
import com.luol.transaction.common.enums.RepositorySupportEnum;
import com.luol.transaction.common.exception.NtcException;
import com.luol.transaction.common.serializer.ObjectSerializer;

import java.util.Date;
import java.util.List;

/**
 * @author luol
 * @date 2018/3/30
 * @time 15:16
 * @function 功能：todo 当前日志细化到项目上 ，后面增加调用链长度时，日志也要细化
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public interface CoordinatorRepository {

    /**
     * 创建本地事务对象
     *
     * @param ntcTransaction 事务对象
     * @return rows 1 成功   0 失败
     */
    int create(NtcTransaction ntcTransaction);

    /**
     * 删除对象
     *
     * @param transID 事务对象id
     * @return rows 返回 1 成功  0 失败
     */
    int remove(String transID);

    /**
     * 更新数据
     *
     * @param ntcTransaction 事务对象
     * @return rows 1 成功 0 失败 失败需要抛异常
     * @throws NtcException 异常
     */
    int update(NtcTransaction ntcTransaction) throws NtcException;

    /**
     * 根据id获取对象
     *
     * @param transID transID
     * @return NtcTransaction
     */
    NtcTransaction findByTransID(String transID);

    /**
     * 根据id,类名，方法名获取对象
     *
     * @param transID transID
     * @param targetClass 类名
     * @param targetMethod 方法名
     * @return NtcTransaction
     */
    NtcTransaction getByTransIDAndName(String transID, String targetClass, String targetMethod);

    /**
     * 初始化操作
     *
     * @param modelName  模块名称
     * @throws NtcException 自定义异常
     */
    void init(String modelName) throws NtcException;

    /**
     * 设置scheme
     *
     * @return scheme 命名
     */
    RepositorySupportEnum getScheme();

    /**
     * 设置序列化信息
     *
     * @param objectSerializer 序列化实现
     */
    void setSerializer(ObjectSerializer objectSerializer);
}

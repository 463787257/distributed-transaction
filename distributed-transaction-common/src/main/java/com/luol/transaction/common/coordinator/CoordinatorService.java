package com.luol.transaction.common.coordinator;

import com.luol.transaction.common.bean.message.MessageEntity;
import com.luol.transaction.common.bean.model.NtcTransaction;
import com.luol.transaction.common.config.NtcConfig;
import com.luol.transaction.common.enums.EventTypeEnum;
import com.luol.transaction.common.serializer.ObjectSerializer;

/**
 * @author luol
 * @date 2018/4/4
 * @time 9:07
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public interface CoordinatorService {

    /**
     * 启动本地补偿事务，根据配置是否进行补偿
     *
     * @throws Exception 异常
     */
    void start() throws Exception;

    /**
     * 保存补偿事务信息
     *
     * @param ntcTransaction 实体对象
     * @return 主键id
     */
    String save(NtcTransaction ntcTransaction);

    /**
     * 根据事务id获取NtcTransaction
     *
     * @param transID 事务id
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
     * 删除补偿事务信息
     *
     * @param transID 事务id
     * @return true成功 false 失败
     */
    boolean remove(String transID);

    /**
     * 更新
     *
     * @param ntcTransaction 实体对象
     */
    void update(NtcTransaction ntcTransaction);

    /**
     * 发送消息
     * */
    Boolean sendMessage(MessageEntity message);

    /**
     * 接收到mq消息处理
     * @param message 消息体
     * @return true 处理成功  false 处理失败
     * */
    Boolean processMessage(byte[] message);

    /**
     * 设置序列化方式
     * @param serializer 序列化方式
     */
    void setSerializer(ObjectSerializer serializer);

    /**
     * 日志处理调用，逻辑不想写两遍
     * */
    void handlerLogs(NtcTransaction ntcTransaction, EventTypeEnum eventTypeEnum);

    /**
     * 反射处理调用
     * */
    Boolean handlerInvocation(NtcTransaction ntcTransaction);
}

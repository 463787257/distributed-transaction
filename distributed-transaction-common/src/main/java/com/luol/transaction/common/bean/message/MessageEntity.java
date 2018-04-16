package com.luol.transaction.common.bean.message;

import com.luol.transaction.common.bean.model.NtcTransaction;
import com.luol.transaction.common.enums.AsynchronousTypeEnums;
import com.luol.transaction.common.enums.EventTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author luol
 * @date 2018/4/16
 * @time 14:15
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageEntity {

    /**
     * 消息类型
     * */
    private AsynchronousTypeEnums asynchronousTypeEnums;

    /**
     * 消息内容
     * */
    private NtcTransaction ntcTransaction;

    /**
     * 日志操作类型
     * */
    private EventTypeEnum eventTypeEnum;
}

package com.btjf.distributed.mq.bean;

import com.btjf.distributed.common.bean.entity.DistributedInvocation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author luol
 * @date 2018/1/30
 * @time 11:47
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageContent {

    /**
     * 事务id
     */
    private String transID;

    /**
     * 重试次数
     */
    private volatile int retriedCount = 0;

    /**
     * 调用信息
     * */
    private DistributedInvocation distributedInvocation;

}

package com.btjf.distributed.common.bean.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author luol
 * @date 2018/1/29
 * @time 15:38
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DistributedParticipant implements Serializable{

    private static final long serialVersionUID = 8762236598908885331L;

    /**
     * 事务id
     */
    private String transID;


    /**
     * 消息队列
     */
    private String topic;


    /**
     * 消息subscribe
     */
    private String subscribe;

    /**
     * 执行器
     */
    private DistributedInvocation distributedInvocation;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return Boolean.TRUE;
        }
        if (o != null && o instanceof DistributedParticipant) {
            DistributedParticipant participant = (DistributedParticipant) o;
            return Objects.equals(participant.getTopic(), getTopic());
        }
        return Boolean.FALSE;
    }

}

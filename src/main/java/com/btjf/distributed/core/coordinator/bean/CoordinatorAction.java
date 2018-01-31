package com.btjf.distributed.core.coordinator.bean;

import com.btjf.distributed.common.bean.entity.DistributedTransactionInfo;
import com.btjf.distributed.common.enums.CoordinatorActionEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author luol
 * @date 2018/1/29
 * @time 16:17
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Data
@AllArgsConstructor
public class CoordinatorAction implements Serializable {
    private static final long serialVersionUID = 295489272897009039L;

    /**
     * 操作事物动作
     * */
    private CoordinatorActionEnum coordinatorActionEnum;

    /**
     * 事物传递信息
     * */
    private DistributedTransactionInfo distributedTransactionInfo;
}

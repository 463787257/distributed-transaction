package com.btjf.distributed.core.coordinator;

import com.btjf.distributed.core.coordinator.bean.CoordinatorAction;

/**
 * @author luol
 * @date 2018/1/29
 * @time 16:20
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public interface Command {

    /**
     * 执行协调命令接口
     *
     * @param coordinatorAction 协调数据
     */
    void execute(CoordinatorAction coordinatorAction);


}

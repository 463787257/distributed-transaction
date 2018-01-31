package com.btjf.distributed.core.coordinator.impl;

import com.btjf.distributed.core.coordinator.Command;
import com.btjf.distributed.core.coordinator.CoordinatorService;
import com.btjf.distributed.core.coordinator.bean.CoordinatorAction;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author luol
 * @date 2018/1/29
 * @time 16:21
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Component
public class CoordinatorCommand implements Command {

    @Resource
    private CoordinatorService coordinatorService;

    /**
     * 执行协调命令接口
     *
     * @param coordinatorAction 协调数据
     */
    @Override
    public void execute(CoordinatorAction coordinatorAction) {
        coordinatorService.submit(coordinatorAction);
    }
}

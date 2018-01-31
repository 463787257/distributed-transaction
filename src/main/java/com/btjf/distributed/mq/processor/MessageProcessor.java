package com.btjf.distributed.mq.processor;


/**
 * @author L.C
 * @version 0.0.1
 * @date 16/7/6
 * @time 上午10:05
 * @function 功能: 处理器接口定义
 * @describe 版本描述:
 * @modifyLog 修改日志:
 */
public interface MessageProcessor {

    void processMessage(String message);

}

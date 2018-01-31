package com.btjf.distributed.common.constants;

/**
 * @author luol
 * @date 2018/1/29
 * @time 13:50
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public class CommonConstant {

    public final static String DISTRIBUTED_TRANSACTION_CONTEXT = "DISTRIBUTED_TRANSACTION_CONTEXT";

    /**
     * 监听回滚队列线程数---cpu核数 << 1
     */
    public final static int coordinatorThreadMax = Runtime.getRuntime().availableProcessors() << 1;

    /**
     * 消息队列的 GroupName 后缀
     * */
    public final static String GROUP_NAME_SUFFIX = "GroupName";

    /**
     * 消息队列的 Subscribe 后缀
     * */
    public final static String SUBCRIBE_SUFFIX = "Subscribe";
}

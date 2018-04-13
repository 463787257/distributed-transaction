package com.luol.transaction.core.service;

/**
 * @author luol
 * @date 2018/4/3
 * @time 9:49
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@FunctionalInterface
public interface NtcInitService {

    /**
     * ntc分布式事务初始化方法
     *
     */
    void initialization();
}

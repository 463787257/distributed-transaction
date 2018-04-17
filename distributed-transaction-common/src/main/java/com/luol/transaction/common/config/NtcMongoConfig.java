package com.luol.transaction.common.config;

import lombok.Data;

/**
 * @author luol
 * @date 2018/4/17
 * @time 14:11
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Data
public class NtcMongoConfig {

    /**
     * mongo数据库设置
     */
    private String mongoDbName;

    /**
     * mongo数据库URL
     */
    private String mongoDbUrl;
    /**
     * mongo数据库用户名
     */
    private String mongoUserName;

    /**
     * mongo数据库密码
     */
    private String mongoUserPwd;

    /**
     * 日志表名
     * */
    private String collectionName;

    /**
     * 任务表名
     * */
    private String taskName;

}

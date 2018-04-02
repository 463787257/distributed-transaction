package com.luol.transaction.common.constant;

/**
 * @author luol
 * @date 2018/3/7
 * @time 17:27
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public interface CommonConstant {
    String PATH_SUFFIX = "/ntc";

    String DB_SUFFIX = "ntc_";

    String RECOVER_REDIS_KEY_PRE="ntc:transaction:%s";

    String NTC_TRANSACTION_CONTEXT = "NTC_TRANSACTION_CONTEXT";
}

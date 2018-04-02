package com.luol.transaction.common.exception;

/**
 * @author luol
 * @date 2018/3/7
 * @time 17:42
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public class NtcException extends RuntimeException {

    public NtcException() {}

    public NtcException(String message) {
        super(message);
    }

    public NtcException(String message, Throwable cause) {
        super(message, cause);
    }

    public NtcException(Throwable cause) {
        super(cause);
    }
}

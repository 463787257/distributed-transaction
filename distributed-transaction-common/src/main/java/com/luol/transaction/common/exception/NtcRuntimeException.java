package com.luol.transaction.common.exception;

/**
 * @author luol
 * @date 2018/3/7
 * @time 17:42
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public class NtcRuntimeException extends RuntimeException {

    public NtcRuntimeException() {}

    public NtcRuntimeException(String message) {
        super(message);
    }

    public NtcRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public NtcRuntimeException(Throwable cause) {
        super(cause);
    }
}

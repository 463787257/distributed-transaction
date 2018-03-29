package com.luol.transaction.common.utils;

/**
 * @author luol
 * @date 2018/3/28
 * @time 10:43
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public class DefaultValueUtils {

    private static final int ZERO = 0;

    public static  Object getDefaultValue(Class type) {
        if (boolean.class.equals(type)) {
            return Boolean.FALSE;
        } else if (byte.class.equals(type)) {
            return ZERO;
        } else if (short.class.equals(type)) {
            return ZERO;
        } else if (int.class.equals(type)) {
            return ZERO;
        } else if (long.class.equals(type)) {
            return ZERO;
        } else if (float.class.equals(type)) {
            return ZERO;
        } else if (double.class.equals(type)) {
            return ZERO;
        }
        return null;
    }
}

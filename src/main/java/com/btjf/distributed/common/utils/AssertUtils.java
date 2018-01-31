package com.btjf.distributed.common.utils;



/**
 * @author xiaoyu
 */
public class AssertUtils {

    private AssertUtils() {

    }

    public static void notNull(Object obj, String message) {
        if (obj == null) {
            throw new RuntimeException(message);
        }
    }

    public static void notNull(Object obj) {
        if (obj == null) {
            throw new RuntimeException("argument invalid,Please check");
        }
    }

    public static void checkConditionArgument(boolean condition, String message) {
        if (!condition) {
            throw new RuntimeException(message);
        }
    }

}

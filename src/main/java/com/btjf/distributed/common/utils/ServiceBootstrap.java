package com.btjf.distributed.common.utils;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @author luol
 * @date 2018/1/30
 * @time 9:16
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public class ServiceBootstrap {
    public static <T> T loadFirst(Class<T> clazz) {
        final ServiceLoader<T> loader = loadAll(clazz);
        final Iterator<T> iterator = loader.iterator();
        if (!iterator.hasNext()) {
            throw new IllegalStateException(String.format(
                    "No implementation defined in /META-INF/services/%s, please check whether the file exists and has the right implementation class!",
                    clazz.getName()));
        }
        return iterator.next();
    }

    public static <T> ServiceLoader<T> loadAll(Class<T> clazz) {
        return ServiceLoader.load(clazz);
    }
}

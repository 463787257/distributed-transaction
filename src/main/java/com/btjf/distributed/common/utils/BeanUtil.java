package com.btjf.distributed.common.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.util.CollectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class BeanUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanUtil.class);

    /**
     * 转换一个List
     */
    public static <T> List<T> convertList(List<?> list, Class<T> targetClass) {
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<T>();
        }
        List<T> listDest = new ArrayList<T>();
        for (Object source : list) {
            T target = convert(source, targetClass);
            listDest.add(target);
        }
        return listDest;
    }

    /**
     * 转换一个
     */
    public static <T> T convert(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        try {
            T result = targetClass.newInstance();
            copyProperties(source, result);
            return result;
        } catch (InstantiationException | IllegalAccessException | BeansException e) {
            LOGGER.warn(source.getClass().getSimpleName() + " convert to " + targetClass.getSimpleName() + " fail");
            throw new RuntimeException(e);
        }
    }

    /**
     * 复制属性
     */
    public static void copyProperties(Object source, Object target) throws BeansException {
        org.springframework.beans.BeanUtils.copyProperties(source, target);
        if (target instanceof ConversionCustomizble) {
            ((ConversionCustomizble) target).convertOthers(source);
        }
    }

    /**
     * 获取一个私有属性的值，注意：不考虑私有方法和私有类
     */
    public static Object getPropValue(Object obj, String prop) {
        if (obj == null || prop == null) {
            return null;
        }
        PropertyDescriptor propertyDescriptor = org.springframework.beans.BeanUtils.getPropertyDescriptor(obj.getClass(), prop);
        try {
            Method readMethod = propertyDescriptor.getReadMethod();
            return propertyDescriptor.getReadMethod().invoke(obj);
        } catch (Exception e) {
            return null;
        }
    }


    public interface ConversionCustomizble {
        public void convertOthers(Object srcObj);//其它无法通过copyProperties获取的属性通过继承实现这个方法手工处理
    }
}

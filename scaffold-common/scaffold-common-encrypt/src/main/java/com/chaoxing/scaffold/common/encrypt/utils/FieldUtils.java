package com.chaoxing.scaffold.common.encrypt.utils;

import com.chaoxing.scaffold.common.encrypt.annotation.Encrypt;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author SK
 * @since 2023/6/7
 */
public class FieldUtils {
    /**
     * 类加密字段缓存
     */
    private static final Map<Class<?>, Set<Field>> fieldCache = new ConcurrentHashMap<>();

    public static Set<Field> getFieldCaches(Class<?> sourceClazz){
        return fieldCache.computeIfAbsent(sourceClazz, clazz -> {
            Field[] declaredFields = clazz.getDeclaredFields();
            Set<Field> fieldSet = Arrays.stream(declaredFields).filter(field ->
                            field.isAnnotationPresent(Encrypt.class) && field.getType() == String.class)
                    .collect(Collectors.toSet());
            for (Field field : fieldSet) {
                field.setAccessible(true);
            }
            return fieldSet;
        });
    }
}

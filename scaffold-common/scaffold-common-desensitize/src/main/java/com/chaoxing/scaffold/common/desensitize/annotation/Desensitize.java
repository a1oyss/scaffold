package com.chaoxing.scaffold.common.desensitize.annotation;


import com.chaoxing.scaffold.common.desensitize.serialize.DesensitizeSerializer;
import com.chaoxing.scaffold.common.desensitize.strategy.DesensitizeStrategy;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 脱敏注解
 * @author SK
 * @date 2023/5/24
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@JacksonAnnotationsInside
@JsonSerialize(using = DesensitizeSerializer.class)
public @interface Desensitize {
    DesensitizeStrategy strategy();
}

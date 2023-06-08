package com.chaoxing.scaffold.starter.desensitize.serialize;



import com.chaoxing.scaffold.starter.desensitize.annotation.Desensitize;
import com.chaoxing.scaffold.starter.desensitize.strategy.DesensitizeStrategy;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

import java.io.IOException;
import java.util.Objects;

/**
 * 脱敏序列化处理类
 * @author SK
 * @date 2023/5/24
 */
public class DesensitizeSerializer extends JsonSerializer<String> implements ContextualSerializer {
    private DesensitizeStrategy strategy;

    @Override
    public void serialize(String value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(strategy.getDesensitize().apply(value));
    }

    /**
     * 获取属性上的注解属性
     */
    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        Desensitize annotation = property.getAnnotation(Desensitize.class);
        if (Objects.nonNull(annotation)&&Objects.equals(String.class, property.getType().getRawClass())) {
            this.strategy = annotation.strategy();
            return this;
        }
        return prov.findValueSerializer(property.getType(), property);

    }
}

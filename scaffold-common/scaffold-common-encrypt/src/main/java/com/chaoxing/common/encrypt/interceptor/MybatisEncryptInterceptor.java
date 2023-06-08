package com.chaoxing.common.encrypt.interceptor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.chaoxing.common.encrypt.algorithm.Algorithm;
import com.chaoxing.common.encrypt.annotation.Encrypt;
import com.chaoxing.common.encrypt.context.EncryptContext;
import com.chaoxing.common.encrypt.utils.FieldUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.springframework.cglib.core.ReflectUtils;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.util.*;

/**
 * 入参加密拦截器
 *
 * @author 老马
 * @version 4.6.0
 */
@Slf4j
@Intercepts({@Signature(
    type = ParameterHandler.class,
    method = "setParameters",
    args = {PreparedStatement.class})
})
@AllArgsConstructor
public class MybatisEncryptInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        return invocation;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof ParameterHandler) {
            // 进行加密操作
            ParameterHandler parameterHandler = (ParameterHandler) target;
            Object parameterObject = parameterHandler.getParameterObject();
            if (ObjectUtil.isNotNull(parameterObject) && !(parameterObject instanceof String)) {
                this.encryptHandler(parameterObject);
            }
        }
        return target;
    }

    /**
     * 加密对象
     *
     * @param sourceObject 待加密对象
     */
    private void encryptHandler(Object sourceObject) {
        if (ObjectUtil.isNull(sourceObject)) {
            return;
        }
        if (sourceObject instanceof Map<?, ?>) {
            new HashSet<>(((Map<?, ?>) sourceObject).values()).forEach(this::encryptHandler);
            return;
        }
        if (sourceObject instanceof List<?>) {
            List<?> sourceList = (List<?>) sourceObject;
            if(CollUtil.isEmpty(sourceList)) {
                return;
            }
            // 判断第一个元素是否含有注解。如果没有直接返回，提高效率
            Object firstItem = sourceList.get(0);
            if (ObjectUtil.isNull(firstItem) || CollUtil.isEmpty(FieldUtils.getFieldCaches(firstItem.getClass()))) {
                return;
            }
            ((List<?>) sourceObject).forEach(this::encryptHandler);
            return;
        }
        Set<Field> fields = FieldUtils.getFieldCaches(sourceObject.getClass());
        try {
            for (Field field : fields) {
                field.set(sourceObject, this.encryptField(String.valueOf(field.get(sourceObject)), field));
            }
        } catch (Exception e) {
            log.error("处理加密字段时出错", e);
        }
    }

    /**
     * 字段值进行加密。通过字段的批注注册新的加密算法
     *
     * @param value 待加密的值
     * @param field 待加密字段
     * @return 加密后结果
     */
    private String encryptField(String value, Field field) {
        if (ObjectUtil.isNull(value)) {
            return null;
        }
        Encrypt encryptField = field.getAnnotation(Encrypt.class);
        EncryptContext encryptContext = new EncryptContext();
        encryptContext.setSecret(encryptField.secret());
        encryptContext.setPrivateKey(encryptField.privateKey());
        encryptContext.setPublicKey(encryptField.publicKey());
        Class<? extends Algorithm> algorithm = encryptField.algorithm();
        Algorithm algorithmInstance;
        algorithmInstance = (Algorithm) ReflectUtils.newInstance(algorithm);
        return algorithmInstance.encrypt(value);
    }

}

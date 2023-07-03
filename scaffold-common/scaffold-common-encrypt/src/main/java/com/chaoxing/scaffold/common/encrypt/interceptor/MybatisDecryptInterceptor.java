package com.chaoxing.scaffold.common.encrypt.interceptor;

import com.chaoxing.scaffold.common.encrypt.algorithm.Algorithm;
import com.chaoxing.scaffold.common.encrypt.annotation.Encrypt;
import com.chaoxing.scaffold.common.encrypt.context.EncryptContext;
import com.chaoxing.scaffold.common.encrypt.context.EncryptContextHolder;
import com.chaoxing.scaffold.common.encrypt.utils.FieldUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.springframework.cglib.core.ReflectUtils;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 出参解密拦截器
 *
 * @author 老马
 * @version 4.6.0
 */
@Slf4j
@Intercepts({@Signature(
    type = ResultSetHandler.class,
    method = "handleResultSets",
    args = {Statement.class})
})
public class MybatisDecryptInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 获取执行mysql执行结果
        Object result = invocation.proceed();
        if (result == null) {
            return null;
        }
        decryptHandler(result);
        return result;
    }

    /**
     * 解密对象
     *
     * @param sourceObject 待加密对象
     */
    private void decryptHandler(Object sourceObject) {
        if (sourceObject instanceof Map<?, ?>) {
            new HashSet<>(((Map<?, ?>) sourceObject).values()).forEach(this::decryptHandler);
            return;
        }
        if (sourceObject instanceof List<?>) {
            List<?> sourceList = (List<?>) sourceObject;
            if(CollectionUtils.isEmpty(sourceList)) {
                return;
            }
            // 判断第一个元素是否含有注解。如果没有直接返回，提高效率
            Object firstItem = sourceList.get(0);
            if (ObjectUtils.isEmpty(firstItem) || CollectionUtils.isEmpty(FieldUtils.getFieldCaches(firstItem.getClass()))) {
                return;
            }
            ((List<?>) sourceObject).forEach(this::decryptHandler);
            return;
        }
        Set<Field> fields = FieldUtils.getFieldCaches(sourceObject.getClass());
        try {
            for (Field field : fields) {
                field.set(sourceObject, this.decryptField(String.valueOf(field.get(sourceObject)), field));
            }
        } catch (Exception e) {
            log.error("处理解密字段时出错", e);
        }
    }

    /**
     * 字段值进行加密。通过字段的批注注册新的加密算法
     *
     * @param value 待加密的值
     * @param field 待加密字段
     * @return 加密后结果
     */
    private String decryptField(String value, Field field) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        Encrypt encryptField = field.getAnnotation(Encrypt.class);
        EncryptContext encryptContext = new EncryptContext();
        encryptContext.setSecret(encryptField.secret());
        encryptContext.setPrivateKey(encryptField.privateKey());
        encryptContext.setPublicKey(encryptField.publicKey());
        EncryptContextHolder.setContext(encryptContext);
        Class<? extends Algorithm> algorithm = encryptField.algorithm();
        Algorithm algorithmInstance;
        algorithmInstance = (Algorithm) ReflectUtils.newInstance(algorithm);
        return algorithmInstance.decrypt(value);
    }

}

package com.chaoxing.scaffold.common.encrypt.annotation;

import com.chaoxing.scaffold.common.encrypt.algorithm.Algorithm;
import com.chaoxing.scaffold.common.encrypt.algorithm.impl.AESEncryptor;

import java.lang.annotation.*;

/**
 * 字段加密注解
 *
 * @author SK
 * @since 2023/06/07
 */
@Documented
@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Encrypt {

    /**
     * 加密算法
     */
    Class<? extends Algorithm> algorithm() default AESEncryptor.class;

    /**
     * 秘钥。AES、SM4需要
     */
    String secret() default "";

    /**
     * 公钥。RSA、SM2需要
     */
    String publicKey() default "";

    /**
     * 公钥。RSA、SM2需要
     */
    String privateKey() default "";

}

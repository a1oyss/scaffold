package com.chaoxing.scaffold.autoconfigure.encrypt;

import com.chaoxing.common.encrypt.interceptor.MybatisDecryptInterceptor;
import com.chaoxing.common.encrypt.interceptor.MybatisEncryptInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * @author SK
 * @since 2023/6/7
 */
@AutoConfiguration
public class EncryptAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public MybatisDecryptInterceptor mybatisDecryptInterceptor(){
        return new MybatisDecryptInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean
    public MybatisEncryptInterceptor mybatisEncryptInterceptor(){
        return new MybatisEncryptInterceptor();
    }
}

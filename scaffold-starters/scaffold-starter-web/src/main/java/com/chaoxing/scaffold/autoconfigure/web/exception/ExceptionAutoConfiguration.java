package com.chaoxing.scaffold.autoconfigure.web.exception;

import com.chaoxing.scaffold.autoconfigure.web.exception.resolver.GlobalExceptionResolver;
import com.chaoxing.scaffold.autoconfigure.web.exception.resolver.SecurityHandlerExceptionResolver;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDeniedException;

/**
 * @author SK
 * @since 2023/6/6
 */
@AutoConfiguration
public class ExceptionAutoConfiguration {
    /**
     * 默认的异常处理器
     * @return GlobalHandlerExceptionResolver
     */
    @Bean
    @ConditionalOnMissingBean(GlobalExceptionResolver.class)
    public GlobalExceptionResolver globalExceptionHandlerResolver() {
        return new GlobalExceptionResolver();
    }

    /**
     * Security 异常处理，隔离出一个配置类
     */
    @ConditionalOnClass(AccessDeniedException.class)
    static class SecurityExceptionConfiguration {

        /**
         * security 相关的异常处理
         * @return SecurityHandlerExceptionResolver
         */
        @Bean
        @ConditionalOnMissingBean(SecurityHandlerExceptionResolver.class)
        public SecurityHandlerExceptionResolver securityHandlerExceptionResolver() {
            return new SecurityHandlerExceptionResolver();
        }

    }
}

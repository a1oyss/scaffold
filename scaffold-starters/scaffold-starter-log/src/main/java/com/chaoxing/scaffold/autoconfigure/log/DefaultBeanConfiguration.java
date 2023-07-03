package com.chaoxing.scaffold.autoconfigure.log;

import com.chaoxing.scaffold.common.log.handler.AccessLogHandler;
import com.chaoxing.scaffold.autoconfigure.log.logImpl.DefaultAccessLogHandlerImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * @author SK
 * @since 2023/6/14
 */
@AutoConfiguration
public class DefaultBeanConfiguration {
    @Bean
    @ConditionalOnMissingBean(AccessLogHandler.class)
    public AccessLogHandler<?> accessLogHandler(){
        return new DefaultAccessLogHandlerImpl();
    }
}

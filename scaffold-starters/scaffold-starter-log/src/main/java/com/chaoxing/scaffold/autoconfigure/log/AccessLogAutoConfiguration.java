package com.chaoxing.scaffold.autoconfigure.log;

import com.chaoxing.scaffold.autoconfigure.log.properties.AccessLogProperties;
import com.chaoxing.quickstart.common.log.filter.AccessLogFilter;
import com.chaoxing.quickstart.common.log.handler.AccessLogHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * 请求日志自动配置
 * @author SK
 * @since 2023/6/2
 */
@Slf4j
@AutoConfiguration
@ConditionalOnWebApplication
@EnableConfigurationProperties(AccessLogProperties.class)
@ConditionalOnProperty(prefix = AccessLogProperties.PREFIX, name = "enabled", matchIfMissing = true, havingValue = "true")
public class AccessLogAutoConfiguration {
    private final AccessLogHandler<?> accessLogService;
    private final AccessLogProperties accessLogProperties;

    @Bean
    @ConditionalOnBean(AccessLogHandler.class)
    public FilterRegistrationBean<AccessLogFilter> accessLogFilterRegistrationBean() {
        log.debug("access log 记录拦截器已开启====");
        FilterRegistrationBean<AccessLogFilter> registrationBean = new FilterRegistrationBean<>();
        AccessLogFilter accessLogFilter = new AccessLogFilter();
        accessLogFilter.setAccessLogService(accessLogService);
        accessLogFilter.setIgnoreUrlPatterns(accessLogProperties.getIgnoreUrlPatterns());
        registrationBean.setOrder(-1000);
        return registrationBean;
    }

    public AccessLogAutoConfiguration(AccessLogHandler<?> accessLogService, AccessLogProperties accessLogProperties) {
        this.accessLogService = accessLogService;
        this.accessLogProperties = accessLogProperties;
    }
}

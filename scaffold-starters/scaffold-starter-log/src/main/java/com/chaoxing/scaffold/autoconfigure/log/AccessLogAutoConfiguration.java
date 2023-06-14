package com.chaoxing.scaffold.autoconfigure.log;

import com.chaoxing.common.log.filter.AccessLogFilter;
import com.chaoxing.common.log.handler.AccessLogHandler;
import com.chaoxing.scaffold.autoconfigure.log.logImpl.DefaultAccessLogHandlerImpl;
import com.chaoxing.scaffold.autoconfigure.log.properties.AccessLogProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
@RequiredArgsConstructor
@ConditionalOnWebApplication
@EnableConfigurationProperties(AccessLogProperties.class)
@ConditionalOnProperty(prefix = AccessLogProperties.PREFIX, name = "enabled", matchIfMissing = true, havingValue = "true")
public class AccessLogAutoConfiguration {
    private final AccessLogHandler<?> accessLogHandler;
    private final AccessLogProperties accessLogProperties;

    @Bean
    @ConditionalOnClass(AccessLogHandler.class)
    public FilterRegistrationBean<AccessLogFilter> accessLogFilterRegistrationBean() {
        log.debug("access log 记录拦截器已开启====");
        FilterRegistrationBean<AccessLogFilter> registrationBean = new FilterRegistrationBean<>();
        AccessLogFilter accessLogFilter = new AccessLogFilter();
        accessLogFilter.setAccessLogService(accessLogHandler);
        accessLogFilter.setIgnoreUrlPatterns(accessLogProperties.getIgnoreUrlPatterns());
        registrationBean.setFilter(accessLogFilter);
        registrationBean.setOrder(-1000);
        return registrationBean;
    }
}

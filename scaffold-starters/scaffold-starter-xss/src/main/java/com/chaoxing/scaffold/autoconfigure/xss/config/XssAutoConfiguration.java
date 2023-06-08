package com.chaoxing.scaffold.autoconfigure.xss.config;


import com.chaoxing.scaffold.autoconfigure.xss.properties.XssProperties;
import com.chaoxing.scaffold.common.xss.cleaner.JsoupXssCleaner;
import com.chaoxing.scaffold.common.xss.cleaner.XssCleaner;
import com.chaoxing.scaffold.common.xss.core.XssFilter;
import com.chaoxing.scaffold.common.xss.core.XssStringJsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import java.util.Set;

/**
 * @author SK
 * @date 2023/5/30
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(XssProperties.class)
public class XssAutoConfiguration {
    /**
     * Xss 清理者
     * @return XssCleaner
     */
    @Bean
    @ConditionalOnMissingBean(XssCleaner.class)
    public XssCleaner xssCleaner() {
        return new JsoupXssCleaner();
    }

    /**
     * 主要用于过滤 QueryString, Header 以及 form 中的参数
     * @param xssProperties 安全配置类
     * @return FilterRegistrationBean
     */
    @Bean
    @ConditionalOnProperty(prefix = XssProperties.PREFIX,name = "enabled", havingValue = "true")
    public FilterRegistrationBean<XssFilter> xssFilterRegistrationBean(XssProperties xssProperties,
                                                                       XssCleaner xssCleaner) {
        XssFilter xssFilter = new XssFilter(xssCleaner);
        FilterRegistrationBean<XssFilter> registrationBean = new FilterRegistrationBean<>(xssFilter);
        Set<String> includePaths = xssProperties.getIncludePaths();
        registrationBean.addUrlPatterns(includePaths.toArray(new String[0]));
        Set<String> excludePaths = xssProperties.getExcludePaths();
        xssFilter.setExcludeUrls(Lists.newArrayList(excludePaths));
        registrationBean.setOrder(-1);
        return registrationBean;
    }

    /**
     * 注册 Jackson 的序列化器，用于处理 json 类型参数的 xss 过滤
     * @return Jackson2ObjectMapperBuilderCustomizer
     */
    @Bean
    @ConditionalOnBean(ObjectMapper.class)
    public Jackson2ObjectMapperBuilderCustomizer xssJacksonCustomizer(XssCleaner xssCleaner) {
        // 在反序列化时进行 xss 过滤，可以替换使用 XssStringJsonSerializer，在序列化时进行处理
        return builder -> builder.deserializerByType(String.class, new XssStringJsonDeserializer(xssCleaner));
    }
}

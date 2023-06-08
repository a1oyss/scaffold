package com.chaoxing.scaffold.autoconfigure.web.restTemplate.config;

import cn.hutool.core.util.StrUtil;
import com.chaoxing.scaffold.autoconfigure.web.restTemplate.interceptor.LogClientHttpRequestInterceptor;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class RestTemplateConfig {
    private static final int maxTotalConnect = 200;
    private static final int keepAliveTime = 2;
    private static final int connectTimeout = 20 * 1000;
    private static final int readTimeout = 20 * 1000;
    private static final int writeTimeout = 20 * 1000;

    @Autowired
    private LogClientHttpRequestInterceptor logClientHttpRequestInterceptor;

    /**
     * 设置httpclient参数
     */
    @Bean
    public OkHttpClient httpClient() {
        ConnectionPool connectionPool = new ConnectionPool(maxTotalConnect, keepAliveTime, TimeUnit.MINUTES);
        return new OkHttpClient().newBuilder()
                .connectionPool(connectionPool)
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout,TimeUnit.SECONDS)
                .writeTimeout(writeTimeout,TimeUnit.SECONDS)
                .build();
    }

    /**
     * 创建HTTP客户端工厂
     */
    @Bean
    public OkHttp3ClientHttpRequestFactory okHttp3ClientHttpRequestFactory(OkHttpClient httpClient) {
        return new OkHttp3ClientHttpRequestFactory(httpClient);
    }

    @Bean
    public RestTemplate restTemplate(OkHttp3ClientHttpRequestFactory okHttp3ClientHttpRequestFactory) {
        RestTemplate restTemplate = new RestTemplate(okHttp3ClientHttpRequestFactory);
        restTemplate.setInterceptors(Lists.newArrayList(logClientHttpRequestInterceptor));
        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(okHttp3ClientHttpRequestFactory));
        modifyDefaultCharset(restTemplate);
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
                throw new RuntimeException(StrUtil.format("url: {}, method: {}, code: {}, response: {}",url,method,response.getRawStatusCode(),getResponseBody(response)));
            }
        });
        return restTemplate;
    }

    private void modifyDefaultCharset(RestTemplate restTemplate) {
        List<HttpMessageConverter<?>> converterList = restTemplate.getMessageConverters();
        HttpMessageConverter<?> converterTarget = null;
        for (HttpMessageConverter<?> item : converterList) {
            if (StringHttpMessageConverter.class == item.getClass()) {
                converterTarget = item;
                break;
            }
        }
        if (null != converterTarget) {
            converterList.remove(converterTarget);
        }
        Charset defaultCharset = StandardCharsets.UTF_8;
        converterList.add(1, new StringHttpMessageConverter(defaultCharset));
    }

}

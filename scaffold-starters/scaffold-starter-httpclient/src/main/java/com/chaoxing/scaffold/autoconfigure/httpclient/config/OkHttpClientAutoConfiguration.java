package com.chaoxing.scaffold.autoconfigure.httpclient.config;

import com.chaoxing.scaffold.autoconfigure.httpclient.properties.OkHttpClientProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author SK
 * @since 2023/6/8
 */
@AutoConfiguration
@EnableConfigurationProperties(OkHttpClientProperties.class)
public class OkHttpClientAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(OkHttpClient.class)
    public OkHttpClient okHttpClient(OkHttpClientProperties okHttpClientProperties, ConnectionPool connectionPool) {
        return new OkHttpClient(createBuilder(okHttpClientProperties, connectionPool));
    }

    /**
     * 创建HTTP客户端工厂
     */
    @Bean
    @ConditionalOnMissingBean(OkHttp3ClientHttpRequestFactory.class)
    public OkHttp3ClientHttpRequestFactory okHttp3ClientHttpRequestFactory(OkHttpClient httpClient) {
        return new OkHttp3ClientHttpRequestFactory(httpClient);
    }

    @Bean
    @ConditionalOnMissingBean(ConnectionPool.class)
    public ConnectionPool connectionPool(OkHttpClientProperties okHttpClientProperties) {
        int maxIdleConnections = okHttpClientProperties.getMaxIdleConnections();
        long keepAliveDuration = okHttpClientProperties.getKeepAliveDuration();
        return new ConnectionPool(maxIdleConnections, keepAliveDuration, TimeUnit.MILLISECONDS);
    }

    private OkHttpClient.Builder createBuilder(OkHttpClientProperties okHttpClientProperties,
                                               ConnectionPool connectionPool) {
        return new OkHttpClient.Builder()
                .readTimeout(okHttpClientProperties.getReadTimeout(), TimeUnit.MILLISECONDS)
                .connectTimeout(okHttpClientProperties.getConnectTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(okHttpClientProperties.getWriteTimeout(), TimeUnit.MILLISECONDS)
                .connectionPool(connectionPool)
                .followRedirects(okHttpClientProperties.isFollowRedirects())
                .followSslRedirects(okHttpClientProperties.isFollowSslRedirects())
                .retryOnConnectionFailure(okHttpClientProperties.isRetryOnConnectionFailure());
    }
}

package com.chaoxing.scaffold.autoconfigure.httpclient.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SK
 * @since 2023/6/8
 */
@Data
@ConfigurationProperties(prefix = OkHttpClientProperties.PREFIX)
public class OkHttpClientProperties {
    public static final String PREFIX = "scaffold.http";

    private int maxIdleConnections = 5;
    private long keepAliveDuration = 60 * 1000;
    private int connectTimeout = 5 * 1000;
    private int readTimeout = 30 * 1000;
    private int writeTimeout = 30 * 1000;
    private boolean retryOnConnectionFailure = true;
    private boolean followRedirects = true;
    private boolean followSslRedirects = true;
}

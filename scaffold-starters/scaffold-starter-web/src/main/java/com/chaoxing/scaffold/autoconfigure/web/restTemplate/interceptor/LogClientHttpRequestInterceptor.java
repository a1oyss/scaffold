package com.chaoxing.scaffold.autoconfigure.web.restTemplate.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Slf4j(topic = "RestTemplate")
@Component
public class LogClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        ClientHttpResponse response = execution.execute(request, body);
        stopwatch.stop();
        StringBuilder resBody = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))) {
            String line = bufferedReader.readLine();
            while (line != null) {
                resBody.append(line);
                line = bufferedReader.readLine();
            }
        }
        // 省略图片等二进制数据
        if (request.getHeaders().getContentType() != null && request.getHeaders().getContentType().includes(MediaType.MULTIPART_FORM_DATA)) {
            body = new byte[]{};
        }
        if (log.isDebugEnabled()) {
            log.info("\n======================== RestTemplate OUTPUT START =========================\n" +
                       "Method: {}" +
                       "TimeCost: {}" +
                       "Param: {}" +
                       "Response: {}" +
                       "======================== RestTemplate OUTPUT END =========================\n",
                    request.getMethodValue(),
                    stopwatch,
                    JSON.toJSONString(body, SerializerFeature.PrettyFormat),
                    JSON.toJSONString(resBody, SerializerFeature.PrettyFormat));
        }
        return response;
    }
}
package com.chaoxing.scaffold.autoconfigure.log.logImpl;

import cn.hutool.core.util.URLUtil;
import com.chaoxing.common.log.constants.LogConstants;
import com.chaoxing.common.log.entity.AccessLog;
import com.chaoxing.common.log.handler.AccessLogHandler;
import com.chaoxing.scaffold.common.core.utils.IpUtils;
import com.chaoxing.scaffold.common.core.utils.JsonUtils;
import com.chaoxing.scaffold.common.core.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON;

/**
 * @author SK
 * @since 2023/6/13
 */
@Slf4j
public class DefaultAccessLogHandlerImpl implements AccessLogHandler<AccessLog> {
    @Override
    public AccessLog buildLog(HttpServletRequest request, HttpServletResponse response, String executionTime, Throwable throwable) {
        Object matchingPatternAttr = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String matchingPattern = matchingPatternAttr == null ? "" : String.valueOf(matchingPatternAttr);
        // @formatter:off
        String uri = URLUtil.getPath(request.getRequestURI());
        AccessLog accessLog = new AccessLog()
                .setTraceId(MDC.get(LogConstants.TRACE_ID))
                .setCreateTime(LocalDateTime.now())
                .setCost(executionTime)
                .setIp(IpUtils.getIpAddr(request))
                .setMethod(request.getMethod())
                .setUri(uri)
                .setMatchingPattern(matchingPattern)
                .setErrorMsg(Optional.ofNullable(throwable).map(Throwable::getMessage).orElse(""))
                .setHttpStatus(response.getStatus());
        // @formatter:on

        // 参数获取
        String params = getParams(request);
        accessLog.setReqParams(params);

        // 记录请求体
        if (shouldRecordRequestBody(request, uri)) {
            accessLog.setReqBody(LogUtils.getRequestBody(request));
        }

        // 只记录响应体
        if (shouldRecordResponseBody(response, uri)) {
            accessLog.setResult(LogUtils.getResponseBody(request, response));
        }

        return accessLog;
    }

    /**
     * 是否应该记录请求体
     * @param request 请求信息
     * @param uri 当前请求的uri
     * @return 记录返回 true，否则返回 false
     */
    protected boolean shouldRecordRequestBody(HttpServletRequest request, String uri) {
        // TODO 使用注解控制此次请求是否记录body，更方便个性化定制
        // 文件上传请求、用户改密时、验证码请求不记录body
        return !LogUtils.isMultipartContent(request) && !uri.matches("^/system/user/pass/[^/]+/?$")
                && !uri.matches("^/captcha/.*$");
    }

    /**
     * 是否应该记录响应体
     * @param response 响应信息
     * @param uri 当前请求的uri
     * @return 记录返回 true，否则返回 false
     */
    protected boolean shouldRecordResponseBody(HttpServletResponse response, String uri) {
        // 只对 content-type 为 application/json 的响应记录响应体（分页请求除外）
        return !uri.endsWith("/page") && response.getContentType() != null
                && response.getContentType().contains(APPLICATION_JSON.getType());
    }

    /**
     * 获取参数信息
     * @param request 请求信息
     * @return 请求参数
     */
    public String getParams(HttpServletRequest request) {
        String params;
        try {
            Map<String, String[]> parameterMap = new HashMap<>(request.getParameterMap());
            params = JsonUtils.toJson(parameterMap);
        }
        catch (Exception e) {
            params = "记录参数异常";
            log.error("[prodLog]，参数获取序列化异常", e);
        }
        return params;
    }

    @Override
    public void saveLog(AccessLog accessLog) {
        log.info("accessLog: {}", JsonUtils.toJson(accessLog));
    }
}

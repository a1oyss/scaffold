package com.chaoxing.common.log.mdc;

import cn.hutool.core.util.IdUtil;
import com.chaoxing.common.log.constants.LogConstants;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 添加TraceId
 * @author SK
 * @since 2023/6/2
 */
public class TraceIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String traceId = IdUtil.objectId();
        MDC.put(LogConstants.TRACE_ID, traceId);
        try {
            // 响应头中添加 traceId 参数，方便排查问题
            response.setHeader(LogConstants.TRACE_ID, traceId);
            filterChain.doFilter(request, response);
        }
        finally {
            MDC.remove(LogConstants.TRACE_ID);
        }
    }

}

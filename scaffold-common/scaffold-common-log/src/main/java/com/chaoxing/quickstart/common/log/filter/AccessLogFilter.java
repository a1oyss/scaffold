package com.chaoxing.quickstart.common.log.filter;

import com.chaoxing.quickstart.common.log.constants.LogConstants;
import com.chaoxing.quickstart.common.log.handler.AccessLogHandler;
import com.chaoxing.scaffold.common.core.request.RepeatBodyRequestWrapper;
import com.chaoxing.scaffold.common.core.utils.LogUtils;
import com.google.common.base.Stopwatch;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 请求日志过滤器
 * @author SK
 * @since 2023/06/01
 */
public class AccessLogFilter extends OncePerRequestFilter {
	private AccessLogHandler<?> accessLogService;
	private List<String> ignoreUrlPatterns;

	/**
	 * 针对需忽略的Url的规则匹配器
	 */
	private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

	/**
	 * URL 路径匹配的帮助类
	 */
	private static final UrlPathHelper URL_PATH_HELPER = new UrlPathHelper();

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		// 跳过部分忽略 url
		String lookupPathForRequest = URL_PATH_HELPER.getLookupPathForRequest(request);
		for (String ignoreUrlPattern : ignoreUrlPatterns) {
			if (ANT_PATH_MATCHER.match(ignoreUrlPattern, lookupPathForRequest)) {
				filterChain.doFilter(request, response);
				return;
			}
		}

		// 包装request，以保证可以重复读取body 但不对文件上传请求body进行处理
		HttpServletRequest requestWrapper;
		if (LogUtils.isMultipartContent(request)) {
			requestWrapper = request;
		}
		else {
			requestWrapper = new RepeatBodyRequestWrapper(request);
		}
		// 包装 response，便于重复获取 body
		ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

		// 开始时间
		Stopwatch stopwatch = Stopwatch.createStarted();
		Throwable myThrowable = null;
		final String traceId = MDC.get(LogConstants.TRACE_ID);
		try {
			filterChain.doFilter(requestWrapper, responseWrapper);
		}
		catch (Throwable throwable) {
			// 记录外抛异常
			myThrowable = throwable;
			throw throwable;
		}
		finally {
			// 这里抛BusinessException后会丢失traceId，需要重新设置
			if (StringUtils.isBlank(MDC.get(LogConstants.TRACE_ID))) {
				MDC.put(LogConstants.TRACE_ID, traceId);
			}
			// 结束时间
			stopwatch.stop();
			// 记录在doFilter里被程序处理过后的异常，可参考
			// http://www.runoob.com/servlet/servlet-exception-handling.html
			Throwable throwable = (Throwable) requestWrapper.getAttribute("javax.servlet.error.exception");
			if (throwable != null) {
				myThrowable = throwable;
			}
			// 生产一个日志并记录
			try {
				accessLogService.handleLog(requestWrapper, responseWrapper, stopwatch.toString(), myThrowable);
			}
			catch (Exception e) {
				logger.error("logging access_log error!", e);
			}
			// 重新写入数据到响应信息中
			responseWrapper.copyBodyToResponse();
		}
	}

	public void setAccessLogService(AccessLogHandler<?> accessLogService) {
		this.accessLogService = accessLogService;
	}

	public void setIgnoreUrlPatterns(List<String> ignoreUrlPatterns) {
		this.ignoreUrlPatterns = ignoreUrlPatterns;
	}
}
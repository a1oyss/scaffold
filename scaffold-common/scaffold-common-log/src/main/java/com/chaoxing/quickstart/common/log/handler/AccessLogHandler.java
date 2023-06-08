package com.chaoxing.quickstart.common.log.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 日志处理类接口
 * @author SK
 * @since 2023/6/2
 */
public interface AccessLogHandler<T> {
    /**
     * 处理日志
     * @param request 请求信息
     * @param response 响应信息
     * @param executionTime 执行时间
     * @param throwable 异常信息
     */
    default void handleLog(HttpServletRequest request, HttpServletResponse response,String executionTime,Throwable throwable){
        T log = buildLog(request, response, executionTime, throwable);
        saveLog(log);
    }

    /**
     * 构建日志
     * @param request 请求信息
     * @param response 响应信息
     * @param executionTime 执行时间
     * @param throwable 异常信息
     * @return 日志信息
     */
    T buildLog(HttpServletRequest request, HttpServletResponse response,String executionTime,Throwable throwable);

    /**
     * 保存日志
     * @param log 日志信息
     */
    void saveLog(T log);

}

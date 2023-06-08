package com.chaoxing.scaffold.autoconfigure.web.exception.resolver;

import com.chaoxing.scaffold.common.core.constants.Constants;
import com.chaoxing.scaffold.common.core.entity.Result;
import com.chaoxing.scaffold.common.core.enums.ResultEnum;
import com.chaoxing.scaffold.common.core.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常拦截
 *
 * @author SK
 * @since 2023/6/2
 */
@Order
@Slf4j
@RestControllerAdvice
public class GlobalExceptionResolver {
    @Value("${spring.profiles.active}")
    private String profile;

    public static final String PROD_ERR_MSG = "系统异常，请联系管理员";

    public static final String NLP_MSG = "空指针异常!";

    /**
     * 全局异常捕获
     *
     * @param e the e
     * @return Result
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<String> handleGlobalException(Exception e, HttpServletRequest request) {
        log.error("请求地址: {}, 全局异常信息 ex={}", request.getRequestURI(), e.getMessage(), e);
        // 当为生产环境, 不适合把具体的异常信息展示给用户, 比如数据库异常信息.
        String errorMessage = Constants.ENV_PROD.equals(profile) ? PROD_ERR_MSG : e.getLocalizedMessage();
        return Result.failed(ResultEnum.SERVER_ERROR, errorMessage);
    }

    /**
     * 空指针异常捕获
     *
     * @param e the e
     * @return Result
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<String> handleNullPointerException(NullPointerException e, HttpServletRequest request) {
        log.error("请求地址: {}, 空指针异常 ex={}", request.getRequestURI(), e.getMessage(), e);
        // 当为生产环境, 不适合把具体的异常信息展示给用户, 比如数据库异常信息.
        String errorMessage = Constants.ENV_PROD.equals(profile) ? PROD_ERR_MSG : NLP_MSG;
        return Result.failed(ResultEnum.SERVER_ERROR, errorMessage);
    }

    /**
     * MethodArgumentTypeMismatchException 参数类型转换异常
     *
     * @param e the e
     * @return Result
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result<String> handleMethodArgumentTypeMismatchException(Exception e, HttpServletRequest request) {
        log.error("请求地址: {}, 请求入参异常 ex={}", request.getRequestURI(), e.getMessage(), e);
        String errorMessage = Constants.ENV_PROD.equals(profile) ? PROD_ERR_MSG : e.getMessage();
        return Result.failed(ResultEnum.BAD_REQUEST, errorMessage);
    }

    /**
     * 请求方式有问题 - MediaType 异常 - Method 异常
     *
     * @return Result
     */
    @ExceptionHandler({HttpMediaTypeNotSupportedException.class, HttpRequestMethodNotSupportedException.class})
    public Result<String> requestNotSupportedException(Exception e, HttpServletRequest request) {
        log.error("请求地址: {}, 请求方式异常 ex={}", request.getRequestURI(), e.getMessage(), e);
        return Result.failed(ResultEnum.BAD_REQUEST, e.getLocalizedMessage());
    }

    /**
     * IllegalArgumentException 异常捕获，主要用于Assert
     *
     * @param e the e
     * @return Result
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        log.error("请求地址: {}, 非法数据输入 ex={}", request.getRequestURI(), e.getMessage(), e);
        return Result.failed(ResultEnum.BAD_REQUEST, e.getMessage());
    }

    /**
     * validation Exception
     *
     * @param e the e
     * @return Result
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> handleBodyValidException(BindException e, HttpServletRequest request) {
        BindingResult bindingResult = e.getBindingResult();
        String errorMsg = bindingResult.getErrorCount() > 0 ? bindingResult.getAllErrors().get(0).getDefaultMessage()
                : "未获取到错误信息!";

        log.error("请求地址: {}, 参数绑定异常 ex={}", request.getRequestURI(), errorMsg);
        return Result.failed(ResultEnum.BAD_REQUEST, errorMsg);
    }

    /**
     * 自定义业务异常捕获 业务异常响应码推荐使用200 用 result 结构中的code做为业务错误码标识
     *
     * @param e the e
     * @return Result
     */
    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<String> handleBallCatException(ServiceException e, HttpServletRequest request) {
        log.error("请求地址: {}, 业务异常信息 ex={}", request.getRequestURI(), e.getMessage());
        return Result.failed(e.getMessage());
    }
}

package com.chaoxing.scaffold.autoconfigure.web.exception.resolver;

import com.chaoxing.scaffold.common.core.entity.Result;
import com.chaoxing.scaffold.common.core.enums.ResultEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(-1000)
@Slf4j
@RestControllerAdvice
public class SecurityHandlerExceptionResolver {
	/**
	 * AccessDeniedException
	 * @param e the e
	 * @return R
	 */
	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public Result<String> handleAccessDeniedException(AccessDeniedException e) {
		String msg = SpringSecurityMessageSource.getAccessor()
			.getMessage("AbstractAccessDecisionManager.accessDenied", e.getMessage());
		log.error("拒绝授权异常信息 ex={}", msg);
		return Result.failed(ResultEnum.FORBIDDEN, e.getLocalizedMessage());
	}

}

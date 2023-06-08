package com.chaoxing.common.idempotent.exception;

import com.chaoxing.scaffold.common.core.exception.ServiceException;
import lombok.EqualsAndHashCode;

/**
 * @author SK
 * @since 2023/6/7
 */
@EqualsAndHashCode(callSuper = true)
public class IdempotentException extends ServiceException {

    public IdempotentException(String message) {
        super(message);
    }

    public IdempotentException(String message, Object... args) {
        super(message, args);
    }
}

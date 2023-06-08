package com.chaoxing.scaffold.common.core.exception;

import org.slf4j.helpers.MessageFormatter;

/**
 * @author SK
 * @since 2023/6/2
 */
public class ServiceException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public ServiceException(String message){
        super(message);
    }

    public ServiceException(String message,Object...args) {
        super(MessageFormatter.arrayFormat(message,args).getMessage(),
                MessageFormatter.getThrowableCandidate(args));

    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }
}

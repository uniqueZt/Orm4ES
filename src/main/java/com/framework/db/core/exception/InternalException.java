package com.framework.db.core.exception;

/**
 * Created by zhangteng on 2018/8/18.
 */
public class InternalException extends RuntimeException{

    public InternalException() {
        super();
    }

    public InternalException(String message) {
        super(message);
    }

    public InternalException(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalException(Throwable cause) {
        super(cause);
    }
}

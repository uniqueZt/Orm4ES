package com.framework.db.core.exception;

/**
 * Created by zhangteng on 2018/8/18.
 */
public class ExecuteException extends RuntimeException{

    public ExecuteException() {
        super();
    }

    public ExecuteException(String message) {
        super(message);
    }

    public ExecuteException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExecuteException(Throwable cause) {
        super(cause);
    }
}

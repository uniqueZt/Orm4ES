package com.framework.db.core.exception;

/**
 * Created by zhangteng on 2018/8/17.
 */
public class ConfigException extends RuntimeException{

    public ConfigException() {
        super();
    }

    public ConfigException(String message) {
        super(message);
    }

    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigException(Throwable cause) {
        super(cause);
    }
}

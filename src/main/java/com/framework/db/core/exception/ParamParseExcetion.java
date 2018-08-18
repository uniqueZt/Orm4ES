package com.framework.db.core.exception;

/**
 * Created by zhangteng on 2018/8/18.
 */
public class ParamParseExcetion extends RuntimeException{

    public ParamParseExcetion() {
        super();
    }

    public ParamParseExcetion(String message) {
        super(message);
    }

    public ParamParseExcetion(String message, Throwable cause) {
        super(message, cause);
    }

    public ParamParseExcetion(Throwable cause) {
        super(cause);
    }
}

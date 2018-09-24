package com.framework.db.core.filter;

/**
 * Created by zhangteng on 2018/9/24.
 */
public class FilterResult {
    private boolean isPassed;

    private Object result;

    public boolean isPassed() {
        return isPassed;
    }

    public void setPassed(boolean passed) {
        isPassed = passed;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}

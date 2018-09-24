package com.framework.db.core.filter;

/**
 * Created by zhangteng on 2018/9/24.
 */
public class PipeLineResult {

    private boolean isPassed;

    private int currentInvokeIndex;

    private Object filterResult;

    public boolean isPassed() {
        return isPassed;
    }

    public void setPassed(boolean passed) {
        isPassed = passed;
    }

    public int getCurrentInvokeIndex() {
        return currentInvokeIndex;
    }

    public void setCurrentInvokeIndex(int currentInvokeIndex) {
        this.currentInvokeIndex = currentInvokeIndex;
    }

    public Object getFilterResult() {
        return filterResult;
    }

    public void setFilterResult(Object filterResult) {
        this.filterResult = filterResult;
    }
}

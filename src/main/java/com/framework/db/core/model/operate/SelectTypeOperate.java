package com.framework.db.core.model.operate;

/**
 * Created by zhangteng on 2018/8/17.
 */
public class SelectTypeOperate{

    private String index;

    private String type;

    private boolean isScroll;

    private long scrollTime;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isScroll() {
        return isScroll;
    }

    public void setScroll(boolean scroll) {
        isScroll = scroll;
    }

    public long getScrollTime() {
        return scrollTime;
    }

    public void setScrollTime(long scrollTime) {
        this.scrollTime = scrollTime;
    }
}

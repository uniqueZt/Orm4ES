package com.framework.db.core.cache;

import java.io.Serializable;

/**
 * Created by zhangteng on 2018/9/24.
 */
public class CacheResult implements Serializable{

    private Object originResult;

    private long writeTime;

    public Object getOriginResult() {
        return originResult;
    }

    public void setOriginResult(Object originResult) {
        this.originResult = originResult;
    }

    public long getWriteTime() {
        return writeTime;
    }

    public void setWriteTime(long writeTime) {
        this.writeTime = writeTime;
    }
}

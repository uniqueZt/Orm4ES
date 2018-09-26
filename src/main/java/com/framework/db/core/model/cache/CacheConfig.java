package com.framework.db.core.model.cache;

import com.framework.db.core.cache.CacheLevel;
import com.framework.db.core.cache.EvictStrategy;

/**
 * Created by zhangteng on 2018/9/27.
 */
public class CacheConfig {
    private EvictStrategy evictStrategy;

    private CacheLevel cacheLevel;

    private int maxSize;

    private long expireTime;

    private long expireTimeAfterWrite;

    public EvictStrategy getEvictStrategy() {
        return evictStrategy;
    }

    public void setEvictStrategy(EvictStrategy evictStrategy) {
        this.evictStrategy = evictStrategy;
    }

    public CacheLevel getCacheLevel() {
        return cacheLevel;
    }

    public void setCacheLevel(CacheLevel cacheLevel) {
        this.cacheLevel = cacheLevel;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public long getExpireTimeAfterWrite() {
        return expireTimeAfterWrite;
    }

    public void setExpireTimeAfterWrite(long expireTimeAfterWrite) {
        this.expireTimeAfterWrite = expireTimeAfterWrite;
    }
}

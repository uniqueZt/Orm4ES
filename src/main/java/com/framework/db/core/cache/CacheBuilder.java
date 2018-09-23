package com.framework.db.core.cache;

import com.framework.db.core.exception.ExecuteException;

/**
 * Created by zhangteng on 2018/9/24.
 */
public class CacheBuilder {
    //缓存级别
    private CacheLevel cacheLevel;

    //缓存过期时间(定时清理没有具体到某一个key)
    private long expireTime;

    //缓存过期时间，具体到某一key，时间从写入之后算起
    private long expireTimeAfterWrite;

    //缓存最大key
    private int maxSize;

    //缓存驱逐策略
    private EvictStrategy evictStrategy;

    //缓存具体实现类
    private Cache cache = new MemoryCache();

    public CacheBuilder setCacheLevel(CacheLevel cacheLevel){
        this.cacheLevel = cacheLevel;
        return this;
    }

    public CacheBuilder setExpireTime(long expireTime){
        if(expireTime <=0){
            throw new ExecuteException("缓存生存时间不能小于等于0");
        }
        this.expireTime = expireTime;
        return this;
    }

    public CacheBuilder setExpireTimeAfterWrite(long expireTime){
        this.expireTimeAfterWrite = expireTime;
        return this;
    }

    public CacheBuilder setMaxSize(int maxSize){
        if(maxSize <=0){
            throw new ExecuteException("缓存的key的个数不能为0");
        }
        this.maxSize = maxSize;
        return this;
    }

    public CacheBuilder setEvictStrategy(EvictStrategy evictStrategy){
        this.evictStrategy = evictStrategy;
        return this;
    }

    public CacheBuilder setCache(Cache cache){
        this.cache = cache;
        return this;
    }

    public static CacheBuilder newInstance(){
        return new CacheBuilder();
    }

    public CacheManager build(){
        CacheManager cacheManager = null;
        switch (evictStrategy){
            case FIFO:
                cacheManager = new FifoCacheManager();
                break;
            case LRU:
                cacheManager = new LRUCacheManager();
                break;
        }
        cacheManager.setCache(cache);
        cacheManager.setCacheLevel(cacheLevel);
        cacheManager.setExpireTime(expireTime);
        cacheManager.setExpireTimeAfterWrite(expireTimeAfterWrite);
        cacheManager.setMaxSize(maxSize);
        cacheManager.init();
        return cacheManager;
    }
}

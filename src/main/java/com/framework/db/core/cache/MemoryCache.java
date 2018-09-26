package com.framework.db.core.cache;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhangteng on 2018/9/24.
 */
public class MemoryCache implements Cache{

    private ConcurrentHashMap<CacheKey,CacheResult> cacheMap = new ConcurrentHashMap<>();


    @Override
    public CacheResult get(CacheKey cacheKey) {
        return cacheMap.get(cacheKey);
    }

    @Override
    public void remove(CacheKey cacheKey) {
        cacheMap.remove(cacheKey);
    }

    @Override
    public void clear() {
        cacheMap.clear();
    }

    @Override
    public void put(CacheKey cacheKey, CacheResult value) {
        cacheMap.put(cacheKey,value);
    }
}

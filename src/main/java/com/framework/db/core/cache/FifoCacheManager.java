package com.framework.db.core.cache;

import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by zhangteng on 2018/9/23.
 */
public class FifoCacheManager extends CacheManager{

    @Override
    protected void evictData() {
        ConcurrentLinkedDeque<CacheKey> cacheKeys = getCacheKeys();
        CacheKey cacheKey = cacheKeys.poll();
        if(cacheKey != null){
          getCache().remove(cacheKey);
        }
    }
}

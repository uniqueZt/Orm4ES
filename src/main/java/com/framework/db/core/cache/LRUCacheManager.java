package com.framework.db.core.cache;

/**
 * Created by zhangteng on 2018/9/23.
 */
public class LRUCacheManager extends FifoCacheManager{

    @Override
    public Object get(CacheKey cacheKey) {
        Object result =  super.get(cacheKey);
        //删除key
        getCacheKeys().remove(cacheKey);
        //重新入队
        getCacheKeys().offer(cacheKey);
        return result;
    }

}

package com.framework.db.core.cache;

import java.util.List;

/**
 * Created by zhangteng on 2018/9/19.
 */
public interface Cache {

    void put(CacheKey cacheKey, CacheResult value);

    CacheResult get(CacheKey cacheKey);

    void remove(CacheKey cacheKey);

    void clear();
}

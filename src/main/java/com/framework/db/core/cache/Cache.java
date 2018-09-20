package com.framework.db.core.cache;

import java.util.List;

/**
 * Created by zhangteng on 2018/9/19.
 */
public interface Cache {

    void put(CacheKey cacheKey, Object value);

    Object get(CacheKey cacheKey);

    void remove(CacheKey cacheKey);

    void clear();
}

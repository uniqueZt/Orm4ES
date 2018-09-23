package com.framework.db.core.cache;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

/**
 * Created by zhangteng on 2018/9/23.
 */
public class OrmSoftRefrence extends SoftReference{

    private CacheKey cacheKey;

    public OrmSoftRefrence(Object referent) {
        super(referent);
    }

    public OrmSoftRefrence(Object referent, ReferenceQueue q) {
        super(referent, q);
    }

    public CacheKey getCacheKey() {
        return cacheKey;
    }

    public OrmSoftRefrence setCacheKey(CacheKey cacheKey) {
        this.cacheKey = cacheKey;
        return this;
    }
}

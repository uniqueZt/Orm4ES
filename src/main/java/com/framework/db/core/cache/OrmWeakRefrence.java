package com.framework.db.core.cache;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/**
 * Created by zhangteng on 2018/9/23.
 */
public class OrmWeakRefrence extends WeakReference{

    private CacheKey cachekey;

    public OrmWeakRefrence(Object referent) {
        super(referent);
    }

    public OrmWeakRefrence(Object referent, ReferenceQueue q) {
        super(referent, q);
    }

    public CacheKey getCachekey() {
        return cachekey;
    }

    public OrmWeakRefrence setCachekey(CacheKey cachekey) {
        this.cachekey = cachekey;
        return this;
    }
}

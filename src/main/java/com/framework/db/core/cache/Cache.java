package com.framework.db.core.cache;

/**
 * Created by zhangteng on 2018/8/26.
 */
public interface Cache {

    void putObject(Object key,Object value);

    void getObject(Object key);

    void removeObject(Object key);

    void clear();

}

package com.framework.db.core.cache;

import com.framework.db.core.exception.ExecuteException;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by zhangteng on 2018/9/19.
 */
public class Segment extends ReentrantLock{

    private Cache cache;

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    public void put(CacheKey cacheKey,Object value){
        lock();
        try{
            cache.put(cacheKey,value);
        }catch (Exception e){
            throw  new ExecuteException("加入缓存失败",e);
        }finally {
            unlock();
        }
    }

    public void remove(CacheKey cacheKey){
        lock();
        try{
            cache.remove(cacheKey);
        }catch (Exception e){
            throw new ExecuteException("删除数据失败",e);
        }finally {
            unlock();
        }
    }

    public Object get(CacheKey cacheKey){
       return cache.get(cacheKey);
    }

}

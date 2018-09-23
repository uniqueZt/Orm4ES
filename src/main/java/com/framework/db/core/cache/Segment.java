package com.framework.db.core.cache;

import com.framework.db.core.exception.ExecuteException;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by zhangteng on 2018/9/19.
 */
public class Segment extends ReentrantLock{

    private long expireTimeAfterWrite;

    private Cache cache;

    private CacheManager cacheManager;

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    public void put(CacheKey cacheKey,Object value){
        lock();
        try{
            CacheResult cacheResult = new CacheResult();
            cacheResult.setWriteTime(System.currentTimeMillis());
            cacheResult.setOriginResult(value);
            cache.put(cacheKey,cacheResult);
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

    public void removeUnlock(CacheKey cacheKey){
        cache.remove(cacheKey);
    }

    public Object get(CacheKey cacheKey){
       CacheResult cacheResult = cache.get(cacheKey);
       if(null == cacheResult){
           if(expireTimeAfterWrite == -1){
               cacheManager.getCacheKeys().remove(cacheKey);
           }
           return null;
       }

       if(System.currentTimeMillis() - cacheResult.getWriteTime() > expireTimeAfterWrite){
           cacheManager.removeUnlock(cacheKey);
           return null;
       }
       Object result = cacheResult.getOriginResult();
       if(result instanceof WeakReference){
           return ((WeakReference)result).get();
       }
       if(result instanceof SoftReference){
           return ((SoftReference)result).get();
       }
       return result;
    }

    public long getExpireTimeAfterWrite() {
        return expireTimeAfterWrite;
    }

    public void setExpireTimeAfterWrite(long expireTimeAfterWrite) {
        this.expireTimeAfterWrite = expireTimeAfterWrite;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
}

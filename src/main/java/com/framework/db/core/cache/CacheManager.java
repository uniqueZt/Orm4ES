package com.framework.db.core.cache;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhangteng on 2018/9/19.
 */
public abstract class CacheManager {

    private final static int defaultLength = 16;

    private int segmentSize;

    private int maxSize;

    private AtomicInteger currentKeyCount = new AtomicInteger(0);

    private Segment[] segments;

    protected Cache cache;

    private long expireTime;

    private volatile long readyTime;

    public void put(CacheKey cacheKey,Object object){
         if(object == null) {
             return;
         }
         Segment segment = segmentForCacheKey(cacheKey);
         segment.put(cacheKey,object);
         currentKeyCount.incrementAndGet();
         if(currentKeyCount.intValue() > maxSize){
             //驱逐数据
             evictData();
             currentKeyCount.decrementAndGet();
         }
    }

    protected void remove(CacheKey cacheKey){
        Segment segment = segmentForCacheKey(cacheKey);
        segment.remove(cacheKey);
    }

    //是否清除缓存
    private boolean isClearCache(){
        long currentTime = System.currentTimeMillis();
        return currentTime-readyTime > expireTime;
    }

    private void resetReadyTime(){
        long newTime = System.currentTimeMillis();
        if(newTime - readyTime > expireTime){
            readyTime = newTime;
        }
    }

    public void clear(){
        cache.clear();
        resetReadyTime();
    }

    public Object get(CacheKey cacheKey){
        Segment segment = segmentForCacheKey(cacheKey);
        if(isClearCache()){
            clear();
        }
        return segment.get(cacheKey);
    }


    public void init(){
        int realSize = getRealSize();
        segments = new Segment[realSize];
        for(int i=0;i<segments.length;i++){
            segments[i] = new Segment();
            segments[i].setCache(cache);
        }
        readyTime = System.currentTimeMillis();
    }

    protected abstract void evictData();

    private int getRealSize(){
        int baseNumber = 2;
        while(baseNumber >= segmentSize){
            baseNumber = baseNumber << 1;
        }
        return Math.max(baseNumber,defaultLength);
    }

    public Segment segmentForCacheKey(CacheKey cacheKey){
        int hashCode = cacheKey.hashCode();
        return segments[(segments.length -1) & hashCode];
    }
}

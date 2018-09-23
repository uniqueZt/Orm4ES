package com.framework.db.core.cache;

import com.framework.db.core.exception.ExecuteException;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentLinkedDeque;
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

    protected ConcurrentLinkedDeque<CacheKey> cacheKeys = new ConcurrentLinkedDeque<>();

    private long expireTime;

    private long expireTimeAfterWrite;

    private volatile long readyTime;

    private CacheLevel cacheLevel;

    private ReferenceQueue gcQueue;

    public void put(CacheKey cacheKey,Object value){
         if(value == null) {
             return;
         }
         Segment segment = segmentForCacheKey(cacheKey);
         segment.put(cacheKey,getRealObject(cacheKey,value));
         currentKeyCount.incrementAndGet();
         if(currentKeyCount.intValue() > maxSize){
             //驱逐数据
             evictData();
             currentKeyCount.decrementAndGet();
         }
         cacheKeys.offer(cacheKey);
         if(isClearCache()){
             clear();
         }
    }

    protected void remove(CacheKey cacheKey){
        Segment segment = segmentForCacheKey(cacheKey);
        segment.remove(cacheKey);
        cacheKeys.remove(cacheKey);
        currentKeyCount.decrementAndGet();
    }

    public final void removeUnlock(CacheKey cacheKey){
        Segment segment = segmentForCacheKey(cacheKey);
        segment.removeUnlock(cacheKey);
        cacheKeys.remove(cacheKey);
        currentKeyCount.decrementAndGet();
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
        cacheKeys.clear();
        currentKeyCount.set(0);
        resetReadyTime();
    }

    public Object get(CacheKey cacheKey){
        Segment segment = segmentForCacheKey(cacheKey);
        if(isClearCache()){
            clear();
        }
        return segment.get(cacheKey);
    }

    private Object getRealObject(CacheKey cacheKey,Object value){
        switch (cacheLevel){
            case STRONG:
                return value;
            case SOFT:
                return new OrmSoftRefrence(value,gcQueue).setCacheKey(cacheKey);
            case WEAK:
                return new OrmWeakRefrence(value,gcQueue).setCachekey(cacheKey);
            default:
                return null;
        }
    }

    public void init(){
        int realSize = getRealSize();
        segments = new Segment[realSize];
        if(cacheLevel == null){
            cacheLevel = CacheLevel.STRONG;
        }
        if(cacheLevel != CacheLevel.STRONG){
            gcQueue = new ReferenceQueue();
            //开启定时清理，软引用或者是若引用的数据
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true){
                       Object result = gcQueue.poll();
                       if(null != result){
                           CacheKey cacheKey = null;
                           if(result instanceof OrmSoftRefrence){
                               cacheKey = ((OrmSoftRefrence)result).getCacheKey();
                           }else if(result instanceof OrmWeakRefrence){
                               cacheKey = ((OrmWeakRefrence)result).getCachekey();
                           }
                           remove(cacheKey);
                       }
                    }
                }
            }).start();
        }
        for(int i=0;i<segments.length;i++){
            segments[i] = new Segment();
            segments[i].setCache(cache);
            segments[i].setExpireTimeAfterWrite(expireTimeAfterWrite);
            segments[i].setCacheManager(this);

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
        int index = (segments.length -1) & hashCode;
        System.out.println(cacheKey.getSql()+"定位到"+index);
        return segments[index];
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public Cache getCache() {
        return cache;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    protected ConcurrentLinkedDeque<CacheKey> getCacheKeys() {
        return cacheKeys;
    }

    public void setCacheKeys(ConcurrentLinkedDeque<CacheKey> cacheKeys) {
        this.cacheKeys = cacheKeys;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public CacheLevel getCacheLevel() {
        return cacheLevel;
    }

    public void setCacheLevel(CacheLevel cacheLevel) {
        this.cacheLevel = cacheLevel;
    }

    public long getExpireTimeAfterWrite() {
        return expireTimeAfterWrite;
    }

    public void setExpireTimeAfterWrite(long expireTimeAfterWrite) {
        this.expireTimeAfterWrite = expireTimeAfterWrite;
    }
}

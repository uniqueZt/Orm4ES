package com.framework.db.core.cache;

/**
 * Created by zhangteng on 2018/9/24.
 */
public class CacheTest {

    public static void main(String[] args) throws Exception{
         CacheBuilder cacheBuilder = CacheBuilder.newInstance();
         CacheManager cacheManager = cacheBuilder.setEvictStrategy(EvictStrategy.FIFO)
                 .setMaxSize(6)
                 .setExpireTime(5000)
                 .setExpireTimeAfterWrite(2000)
                 .setCacheLevel(CacheLevel.STRONG).build();
         for(int i =0;i<10;i++){
             cacheManager.put(new CacheKey("test"+i),"hello"+i);
         }

         Thread.sleep(7000);

         for(int i=0;i<3;i++){
             cacheManager.put(new CacheKey("test"+i),"hello"+i);
         }


    }
}

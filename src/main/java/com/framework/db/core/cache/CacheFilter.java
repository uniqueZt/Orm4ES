package com.framework.db.core.cache;

import com.framework.db.core.filter.Filter;
import com.framework.db.core.filter.FilterResult;
import com.framework.db.core.filter.OperateEntity;
import com.framework.db.core.model.operate.SqlSelectTypeOperate;
import com.framework.db.core.model.param.SqlSelectTypeParam;

/**
 * Created by zhangteng on 2018/9/26.
 */
public class CacheFilter implements Filter{

    private EvictStrategy evictStrategy;

    private CacheLevel cacheLevel;

    private int maxSize;

    private long expireTime;

    private long expireTimeAfterWrite;

    private Cache cache = new MemoryCache();

    private CacheManager cacheManager;

    @Override
    public void init() {
        cacheManager = CacheBuilder.newInstance()
                .setCache(cache)
                .setCacheLevel(cacheLevel)
                .setEvictStrategy(evictStrategy)
                .setExpireTime(expireTime)
                .setExpireTimeAfterWrite(expireTimeAfterWrite)
                .setMaxSize(maxSize).build();
    }

    @Override
    public FilterResult beforeExecute(OperateEntity entity) {
        FilterResult filterResult = new FilterResult();
        switch (entity.getOperateType()){
            case sqlSelect:
                CacheKey cacheKey = getCacheKeyFromSqlSelectOperate(entity);
                Object data = cacheManager.get(cacheKey);
                if(null == data){
                    filterResult.setPassed(true);
                }else{
                    filterResult.setPassed(false);
                    filterResult.setResult(data);
                }
                return filterResult;
            default:
                filterResult.setPassed(true);
                filterResult.setResult(null);
            return filterResult;
        }
    }

    @Override
    public void afterExecute(OperateEntity entity, Object executeResult) {
        switch (entity.getOperateType()){
            case sqlSelect:
                CacheKey cacheKey = getCacheKeyFromSqlSelectOperate(entity);
                cacheManager.put(cacheKey,executeResult);
                break;
            default:
                break;
        }
    }

    private CacheKey getCacheKeyFromSqlSelectOperate(OperateEntity entity){
        SqlSelectTypeOperate sqlSelectTypeOperate = (SqlSelectTypeOperate) entity.getOperate();
        SqlSelectTypeParam sqlSelectTypeParam = (SqlSelectTypeParam) entity.getOperateParam();
        String sql = sqlSelectTypeOperate.buildSql(sqlSelectTypeParam.getParams());
        CacheKey cacheKey = new CacheKey().setSql(sql);
        return cacheKey;
    }

    public EvictStrategy getEvictStrategy() {
        return evictStrategy;
    }

    public void setEvictStrategy(EvictStrategy evictStrategy) {
        this.evictStrategy = evictStrategy;
    }

    public CacheLevel getCacheLevel() {
        return cacheLevel;
    }

    public void setCacheLevel(CacheLevel cacheLevel) {
        this.cacheLevel = cacheLevel;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public long getExpireTimeAfterWrite() {
        return expireTimeAfterWrite;
    }

    public void setExpireTimeAfterWrite(long expireTimeAfterWrite) {
        this.expireTimeAfterWrite = expireTimeAfterWrite;
    }

    public Cache getCache() {
        return cache;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }
}

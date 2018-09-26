package com.framework.db.core.cache;

import java.io.Serializable;

/**
 * Created by zhangteng on 2018/9/17.
 */
public class CacheKey{
    private String sql;

    public CacheKey() {
        this.sql = sql;
    }

    public CacheKey(String sql) {
        this.sql = sql;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null){
            return false;
        }
        if(obj instanceof CacheKey){
            return this.getSql().equals(((CacheKey) obj).getSql());
        }
        return false;
    }

    public String getSql() {
        return sql;
    }

    public CacheKey setSql(String sql) {
        this.sql = sql;
        return this;
    }

    @Override
    public int hashCode() {
        return sql.hashCode();
    }
}

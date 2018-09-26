package com.framework.db.core.model.namespace;

import com.framework.db.core.model.cache.CacheConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangteng on 2018/8/17.
 */
public class GlobalSettings {

    private final static GlobalSettings instance = new GlobalSettings();

    private GlobalSettings(){

    }

    private CacheConfig cacheConfig;

    private Map<String,Namespace> namespaceMap = new HashMap<String, Namespace>();

    public Map<String, Namespace> getNamespaceMap() {
        return namespaceMap;
    }

    public static GlobalSettings getInstance(){
        return instance;
    }

    public CacheConfig getCacheConfig() {
        return cacheConfig;
    }

    public void setCacheConfig(CacheConfig cacheConfig) {
        this.cacheConfig = cacheConfig;
    }
}

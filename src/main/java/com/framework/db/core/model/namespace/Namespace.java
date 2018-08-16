package com.framework.db.core.model.namespace;

import com.framework.db.core.model.mapper.Mapper;
import com.framework.db.core.model.operate.Operate;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangteng on 2018/8/17.
 */
public class Namespace {

    private Class<?> namespaceClass;

    private Map<String,Operate> operateMap = new HashMap<String, Operate>();

    private Map<String,Mapper> mapperMap = new HashMap<String, Mapper>();

    public Class<?> getNamespaceClass() {
        return namespaceClass;
    }

    public void setNamespaceClass(Class<?> namespaceClass) {
        this.namespaceClass = namespaceClass;
    }

    public Map<String, Operate> getOperateMap() {
        return operateMap;
    }

    public Map<String, Mapper> getMapperMap() {
        return mapperMap;
    }
}

package com.framework.db.core.model.namespace;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangteng on 2018/8/17.
 */
public class NamespaceSettings {
     private Map<String,Namespace> namespaceMap = new HashMap<String, Namespace>();

    public Map<String, Namespace> getNamespaceMap() {
        return namespaceMap;
    }

    public void setNamespaceMap(Map<String, Namespace> namespaceMap) {
        this.namespaceMap = namespaceMap;
    }
}

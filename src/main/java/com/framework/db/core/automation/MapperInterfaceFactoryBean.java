package com.framework.db.core.automation;

import com.framework.db.core.middle.ElasticSearchCallSupport;
import com.framework.db.core.model.namespace.Namespace;
import com.framework.db.core.proxy.MapperInterfaceProxy;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by zhangteng on 2018/8/18.
 */
public class MapperInterfaceFactoryBean<T> implements FactoryBean<T>,InitializingBean{

    @Autowired
    private ElasticSearchCallSupport elasticSearchCallSupport;

    private MapperInterfaceProxy<T> mapperInterfaceProxy;

    @Override
    public T getObject() throws Exception {
        return mapperInterfaceProxy.createProxy();
    }

    @Override
    public Class<?> getObjectType() {
        return mapperInterfaceProxy.getNamespace().getNamespaceClass();
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        mapperInterfaceProxy.setElasticSearchCallSupport(elasticSearchCallSupport);
    }

    public MapperInterfaceProxy<T> getMapperInterfaceProxy() {
        return mapperInterfaceProxy;
    }

    public void setMapperInterfaceProxy(MapperInterfaceProxy<T> mapperInterfaceProxy) {
        this.mapperInterfaceProxy = mapperInterfaceProxy;
    }
}

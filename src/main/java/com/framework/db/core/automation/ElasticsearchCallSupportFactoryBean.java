package com.framework.db.core.automation;

import com.framework.db.core.middle.ElasticSearchCallSupport;
import com.framework.db.core.middle.impl.DefaultElasticSearchCallSupport;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by zhangteng on 2018/8/18.
 */
public class ElasticsearchCallSupportFactoryBean implements FactoryBean<ElasticSearchCallSupport>,InitializingBean {

    private RestHighLevelClient restHighLevelClient;

    private ElasticSearchCallSupport elasticSearchCallSupport;

    @Override
    public ElasticSearchCallSupport getObject() throws Exception {
        return elasticSearchCallSupport;
    }

    @Override
    public Class<?> getObjectType() {
        return ElasticSearchCallSupport.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public RestHighLevelClient getRestHighLevelClient() {
        return restHighLevelClient;
    }

    public void setRestHighLevelClient(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if(restHighLevelClient != null){
            elasticSearchCallSupport = new DefaultElasticSearchCallSupport().setRestHighLevelClient(restHighLevelClient);
        }else{
            elasticSearchCallSupport = new DefaultElasticSearchCallSupport();
        }
    }
}

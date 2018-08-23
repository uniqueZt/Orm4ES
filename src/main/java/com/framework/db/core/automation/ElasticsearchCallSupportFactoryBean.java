package com.framework.db.core.automation;

import com.framework.db.core.middle.ElasticSearchCallSupport;
import com.framework.db.core.middle.impl.DefaultElasticSearchCallSupport;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by zhangteng on 2018/8/18.
 */
public class ElasticsearchCallSupportFactoryBean implements FactoryBean<ElasticSearchCallSupport>,InitializingBean {

    private RestHighLevelClient restHighLevelClient;

    private RestClient restClient;

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
        elasticSearchCallSupport = new DefaultElasticSearchCallSupport();
        if(restHighLevelClient != null){
            ((DefaultElasticSearchCallSupport)elasticSearchCallSupport).setRestHighLevelClient(restHighLevelClient);
        }
        if(restClient != null){
            ((DefaultElasticSearchCallSupport)elasticSearchCallSupport).setRestClient(restClient);
        }
    }

    public RestClient getRestClient() {
        return restClient;
    }

    public void setRestClient(RestClient restClient) {
        this.restClient = restClient;
    }
}

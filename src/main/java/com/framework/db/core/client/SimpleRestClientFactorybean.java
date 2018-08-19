package com.framework.db.core.client;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by zhangteng on 2018/8/19.
 */
public class SimpleRestClientFactorybean implements FactoryBean<RestHighLevelClient>,InitializingBean{

    private String host;

    private int port;

    private String schema;

    private HttpHost httpHost;

    @Override
    public RestHighLevelClient getObject() throws Exception {
        RestClientBuilder builder = RestClient.builder(this.httpHost);
        RestClient restClient = builder.build();
        return new RestHighLevelClient(restClient);
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        httpHost = new HttpHost(host,port,schema);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }
}

package com.framework.db.core.middle.impl;

import com.framework.db.core.middle.ElasticSearchCallSupport;
import com.framework.db.core.model.mapper.CommonTypeMapper;
import com.framework.db.core.model.mapper.Mapper;
import com.framework.db.core.model.operate.*;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;

import java.util.List;
import java.util.Map;

/**
 * Created by zhangteng on 2018/8/18.
 */
public class DefaultElasticSearchCallSupport implements ElasticSearchCallSupport{

    private RestHighLevelClient restHighLevelClient;

    @Override
    public void insert(Object paramter, InsertTypeOperate insertTypeOperate, CommonTypeMapper commonTypeMapper) {

    }

    @Override
    public void insert(String key, Object parameter, InsertTypeOperate insertTypeOperate, CommonTypeMapper commonTypeMapper) {

    }

    @Override
    public void updateByKey(String key, Object parameter, UpdateByKeyTypeOperate updateByKeyTypeOperate, CommonTypeMapper commonTypeMapper) {

    }

    @Override
    public void deleteByKey(String key, DeleteByKeyTypeOperate deleteByKeyTypeOperate) {

    }

    @Override
    public void deleteByQuery(QueryBuilder queryBuilder, DeleteByQueryTypeOperate deleteByQueryTypeOperate) {

    }

    @Override
    public List<?> select(QueryBuilder queryBuilder, SelectTypeOperate selectTypeOperate, Mapper resultMapper) {
        return null;
    }

    @Override
    public List<?> sqlSelect(Map<String, Object> paramter, SqlSelectTypeOperate sqlSelectTypeOperate, Mapper resultMapper) {
        return null;
    }

    public DefaultElasticSearchCallSupport setRestHighLevelClient(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
        return this;
    }
}

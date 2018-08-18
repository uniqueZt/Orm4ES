package com.framework.db.core.middle;

import com.framework.db.core.model.mapper.CommonTypeMapper;
import com.framework.db.core.model.mapper.Mapper;
import com.framework.db.core.model.operate.*;
import org.elasticsearch.common.collect.HppcMaps;
import org.elasticsearch.index.query.QueryBuilder;

import java.util.List;
import java.util.Map;

/**
 * Created by zhangteng on 2018/8/18.
 */
public interface ElasticSearchCallSupport {

    void insert(Object paramter, InsertTypeOperate insertTypeOperate, CommonTypeMapper commonTypeMapper);

    void insert(String key,Object parameter,InsertTypeOperate insertTypeOperate,CommonTypeMapper commonTypeMapper);

    void updateByKey(String key, Object parameter, UpdateByKeyTypeOperate updateByKeyTypeOperate,CommonTypeMapper commonTypeMapper);

    void deleteByKey(String key, DeleteByKeyTypeOperate deleteByKeyTypeOperate);

    void deleteByQuery(QueryBuilder queryBuilder, DeleteByQueryTypeOperate deleteByQueryTypeOperate);

    List<?> select(QueryBuilder queryBuilder, SelectTypeOperate selectTypeOperate, Mapper resultMapper);

    List<?> sqlSelect(Map<String,Object> paramter, SqlSelectTypeOperate sqlSelectTypeOperate, Mapper resultMapper);

}

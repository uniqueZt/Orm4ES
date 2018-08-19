package com.framework.db.core.middle.impl;

import com.framework.db.core.exception.ExecuteException;
import com.framework.db.core.middle.ElasticSearchCallSupport;
import com.framework.db.core.model.mapper.CommonTypeMapper;
import com.framework.db.core.model.mapper.Mapper;
import com.framework.db.core.model.operate.*;
import com.framework.db.core.util.ParamUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by zhangteng on 2018/8/18.
 */
public class DefaultElasticSearchCallSupport implements ElasticSearchCallSupport{

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultElasticSearchCallSupport.class);

    private static final long defaultRequestTimeOut = 2L;

    private RestHighLevelClient restHighLevelClient;

    private Long requestTimeOut;

    @Override
    public String insert(Object paramter, InsertTypeOperate insertTypeOperate, CommonTypeMapper commonTypeMapper) {
         String randomKey = UUID.randomUUID().toString();
         insert(randomKey,paramter,insertTypeOperate,commonTypeMapper);
         return randomKey;
    }

    @Override
    public void insert(String key, Object parameter, InsertTypeOperate insertTypeOperate, CommonTypeMapper commonTypeMapper) {
         String writeParamStr = ParamUtils.getWriteParamStr(commonTypeMapper.getAttributes(),parameter);
         IndexRequest request = new IndexRequest(insertTypeOperate.getIndex(),insertTypeOperate.getType(),key);
         long finalRequestTimeOut = requestTimeOut == null?defaultRequestTimeOut:requestTimeOut;
         request.timeout(TimeValue.timeValueSeconds(finalRequestTimeOut));
         request.setRefreshPolicy(getRefreshPolicy(insertTypeOperate.getRefresh()));
         request.source(writeParamStr, XContentType.JSON);
         try{
             IndexResponse indexResponse = getRestHighLevelClient().index(request);
             RestStatus restStatus = indexResponse.status();
             if(restStatus != RestStatus.CREATED && restStatus != RestStatus.OK){
                 throw new ExecuteException("数据写入失败，返回状态:"+restStatus);
             }
         }catch (Exception e) {
             throw new ExecuteException("数据写入失败", e);
         }
    }

    private WriteRequest.RefreshPolicy getRefreshPolicy(RefreshType refreshType){
        switch (refreshType){
            case NONE:
                return WriteRequest.RefreshPolicy.NONE;
            case IMMEDIATE:
                return WriteRequest.RefreshPolicy.IMMEDIATE;
            case WAITUTIL:
                return WriteRequest.RefreshPolicy.WAIT_UNTIL;
            default:
                return WriteRequest.RefreshPolicy.NONE;
        }
    }

    @Override
    public void updateByKey(String key, Object parameter, UpdateByKeyTypeOperate updateByKeyTypeOperate, CommonTypeMapper commonTypeMapper) {
        String writeParamStr = null;
        UpdateRequest updateRequest = new UpdateRequest(updateByKeyTypeOperate.getIndex(),updateByKeyTypeOperate.getType(),key);
        try{
            XContentBuilder xContentBuilder = XContentFactory.jsonBuilder();
            writeParamStr = ParamUtils.getWriteParamStr(commonTypeMapper.getAttributes(),parameter,xContentBuilder);
            updateRequest.doc(xContentBuilder);
        }catch (Exception e){
            LOGGER.error("内部错误",e);
        }
        long finalRequestTimeOut = requestTimeOut == null?defaultRequestTimeOut:requestTimeOut;
        updateRequest.timeout(TimeValue.timeValueSeconds(finalRequestTimeOut));
        updateRequest.setRefreshPolicy(getRefreshPolicy(updateByKeyTypeOperate.getRefresh()));
        updateRequest.upsert(writeParamStr,XContentType.JSON);
        try{
            UpdateResponse updateResponse = restHighLevelClient.update(updateRequest);
            if(updateResponse.status() != RestStatus.CREATED && updateResponse.status() != RestStatus.OK){
                throw  new ExecuteException("更新数据失败："+updateResponse.status());
            }
        }catch (Exception e){
            throw new ExecuteException("数据更新失败",e);
        }
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

    public RestHighLevelClient getRestHighLevelClient() {
        return restHighLevelClient;
    }
}

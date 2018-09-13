package com.framework.db.core.middle.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.framework.db.core.exception.ExecuteException;
import com.framework.db.core.middle.ElasticSearchCallSupport;
import com.framework.db.core.model.mapper.CommonTypeMapper;
import com.framework.db.core.model.mapper.JsonTypeMapper;
import com.framework.db.core.model.mapper.MapTypeMapper;
import com.framework.db.core.model.mapper.Mapper;
import com.framework.db.core.model.operate.*;
import com.framework.db.core.sql.SqlQueryParser;
import com.framework.db.core.util.ParamUtils;
import com.framework.db.core.util.SqlParserUtils;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.*;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * Created by zhangteng on 2018/8/18.
 */
public class DefaultElasticSearchCallSupport implements ElasticSearchCallSupport{

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultElasticSearchCallSupport.class);

    private static final long defaultRequestTimeOut = 10L;

    private RestHighLevelClient restHighLevelClient;

    private RestClient restClient;

    private Long requestTimeOut;

    @Override
    public String insert(Object paramter, InsertTypeOperate insertTypeOperate, CommonTypeMapper commonTypeMapper) {
         String randomKey = UUID.randomUUID().toString();
         insert(randomKey,paramter,insertTypeOperate,commonTypeMapper);
         return randomKey;
    }

    @Override
    public void insert(String key, Object parameter, InsertTypeOperate insertTypeOperate, CommonTypeMapper commonTypeMapper) {
         String writeParamStr = ParamUtils.getWriteParamStr(false,commonTypeMapper,parameter);
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

    @Override
    public void batchInsert(List paramters, BatchInsertTypeOperate batchInsertTypeOperate, CommonTypeMapper commonTypeMapper) {
        BulkRequest bulkRequest = new BulkRequest();
        for(Object paramter:paramters){
            String writeParamStr = ParamUtils.getWriteParamStr(false,commonTypeMapper,paramter);
            IndexRequest indexRequest = new IndexRequest(batchInsertTypeOperate.getIndex(),batchInsertTypeOperate.getType());
            indexRequest.source(writeParamStr,XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        long finalBuilkRequestTimeOut = requestTimeOut == null?defaultRequestTimeOut:requestTimeOut;
        bulkRequest.timeout(TimeValue.timeValueSeconds(finalBuilkRequestTimeOut));
        bulkRequest.setRefreshPolicy(getRefreshPolicy(batchInsertTypeOperate.getRefresh()));
        try{
            BulkResponse bulkResponse = getRestHighLevelClient().bulk(bulkRequest);
            RestStatus restStatus = bulkResponse.status();
            if(restStatus != RestStatus.CREATED && restStatus != RestStatus.OK){
                throw new ExecuteException("批量数据写入失败，返回状态:"+restStatus);
            }
        }catch (Exception e){
            throw new ExecuteException("批量数据写入失败",e);
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
            writeParamStr = ParamUtils.getWriteParamStr(true,commonTypeMapper,parameter,xContentBuilder);
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
        DeleteRequest deleteRequest = new DeleteRequest(deleteByKeyTypeOperate.getIndex(),deleteByKeyTypeOperate.getType(),key);
        long finalRequestTimeOut = requestTimeOut == null?defaultRequestTimeOut:requestTimeOut;
        deleteRequest.timeout(TimeValue.timeValueSeconds(finalRequestTimeOut));
        deleteRequest.setRefreshPolicy(getRefreshPolicy(deleteByKeyTypeOperate.getRefresh()));
        try{
            DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest);
            if(deleteResponse.status() != RestStatus.OK || deleteResponse.status() != RestStatus.NOT_FOUND ){
                throw new ExecuteException("删除数据出错，错误状态" + deleteResponse.status());
            }
        }catch (Exception e){
            throw new ExecuteException("数据删除失败",e);
        }
    }

    @Override
    public void deleteByQuery(QueryBuilder queryBuilder, DeleteByQueryTypeOperate deleteByQueryTypeOperate) {
        if(restClient == null){
            throw  new ExecuteException("没有配置restClient不支持deleteByQuery操作");
        }
        JSONObject object = new JSONObject();
        object.put("query", JSON.parseObject(queryBuilder.toString()));
        HttpEntity entity = new StringEntity(JSON.toJSONString(object), ContentType.APPLICATION_JSON);
        try {
            Response response = restClient.performRequest("POST", "/" + deleteByQueryTypeOperate.getIndex() + "/" + deleteByQueryTypeOperate.getType() + "/_delete_by_query", new HashMap<String, String>(), entity);
            int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode != 200){
                throw new ExecuteException("删除数据请求失败,状态码"+statusCode);
            }
        }catch (Exception e){
            throw new ExecuteException("删除数据失败",e);
        }
    }

    @Override
    public List select(QueryBuilder queryBuilder, SelectTypeOperate selectTypeOperate, Mapper resultMapper) {
        List result = null;
        if(selectTypeOperate.isScroll()){
            result = selectScroll(queryBuilder,selectTypeOperate,resultMapper);
        }else{
            result = selectCommon(queryBuilder,selectTypeOperate,resultMapper);
        }
        return result;
    }

    private  List selectScroll(QueryBuilder queryBuilder,SelectTypeOperate selectTypeOperate,Mapper resultMapper){
        Scroll scroll = new Scroll(TimeValue.timeValueSeconds(selectTypeOperate.getScrollTime()));
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.scroll(scroll);
        searchRequest.indices(selectTypeOperate.getIndex());
        if(!StringUtils.isEmpty(selectTypeOperate.getType())){
            searchRequest.types(selectTypeOperate.getType());
        }
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder).size((int)selectTypeOperate.getSize());
        String scrollId = null;
        try{
            SearchResponse searchResponse = getRestHighLevelClient().search(searchRequest);
            scrollId = searchResponse.getScrollId();
            SearchHits searchHits = searchResponse.getHits();
            SearchHit[] searchHitArr = searchHits.getHits();
            List result = new LinkedList<>();
            while(null != searchHitArr && searchHitArr.length >0){
                for(SearchHit searchHit:searchHitArr){
                    assembleResult(result,resultMapper,searchHit.getSourceAsMap());
                }
                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                scrollRequest.scroll(scroll);
                searchResponse = getRestHighLevelClient().searchScroll(scrollRequest);
                scrollId = searchResponse.getScrollId();
                searchHitArr = searchResponse.getHits().getHits();
            }
            return result;
        }catch (Exception e){
            throw new ExecuteException("scroll查询失败");
        }finally {
            if(null != scrollId){
                ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
                clearScrollRequest.addScrollId(scrollId);
                try{
                     ClearScrollResponse clearScrollResponse = getRestHighLevelClient().clearScroll(clearScrollRequest);
                }catch (IOException e){
                    throw new ExecuteException("scroll清除状态失败",e);
                }
            }
        }
    }

    private  List selectCommon(QueryBuilder queryBuilder, SelectTypeOperate selectTypeOperate, Mapper resultMapper) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(selectTypeOperate.getIndex());
        if(!StringUtils.isEmpty(selectTypeOperate.getType())){
            searchRequest.types(selectTypeOperate.getType());
        }
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.size((int)selectTypeOperate.getSize());
        searchRequest.source(searchSourceBuilder);
        try{
            SearchResponse searchResponse = getRestHighLevelClient().search(searchRequest);
            List result = new LinkedList<>();
            SearchHits searchHits = searchResponse.getHits();
            if(searchHits.totalHits > 0){
                for(SearchHit searchHit:searchHits.getHits()){
                    Map<String,Object> sourceMap = searchHit.getSourceAsMap();
                    assembleResult(result,resultMapper,sourceMap);
                }
                return result;
            }
        }catch (IOException e){
            throw new ExecuteException("发生IO错误",e);
        }catch (Exception e){
            throw new ExecuteException("框架错误",e);
        }
        return new LinkedList<>();
    }

    private void assembleResult(List result,Mapper resultMapper,Map<String,Object> sourceMap) throws Exception{
        if(resultMapper instanceof MapTypeMapper){
            result.add(sourceMap);
        }else if(resultMapper instanceof JsonTypeMapper){
            result.add(ParamUtils.parseOriginResultToJSON(sourceMap));
        }else if(resultMapper instanceof CommonTypeMapper){
            result.add(ParamUtils.parseOrginResultToBean(sourceMap,resultMapper.getMapperClass(),((CommonTypeMapper)resultMapper).getAttributes()));
        }else{
            throw new ExecuteException("没有明确解析结果模型");
        }
    }

    @Override
    public List sqlSelect(Map<String, Object> paramter, SqlSelectTypeOperate sqlSelectTypeOperate, Mapper resultMapper) {
        String sql = sqlSelectTypeOperate.buildSql(paramter);
        try{
            SqlQueryParser sqlQueryParser =  SqlParserUtils.parseSqlExprParser(sql);
            SearchRequest searchRequest = sqlQueryParser.buildSearchRequest();
            System.out.println(searchRequest.toString());

            SearchResponse searchResponse = getRestHighLevelClient().search(searchRequest);
            return sqlQueryParser.getSqlResult(searchResponse,resultMapper);
        }catch (Exception e){
            throw new ExecuteException("sql查询失败",e);
        }
    }

    public DefaultElasticSearchCallSupport setRestHighLevelClient(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
        return this;
    }

    public RestHighLevelClient getRestHighLevelClient() {
        return restHighLevelClient;
    }

    public RestClient getRestClient() {
        return restClient;
    }

    public void setRestClient(RestClient restClient) {
        this.restClient = restClient;
    }
}

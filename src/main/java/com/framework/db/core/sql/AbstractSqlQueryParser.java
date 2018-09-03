package com.framework.db.core.sql;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.Token;
import com.framework.db.core.exception.ExecuteException;
import com.framework.db.core.model.mapper.CommonTypeMapper;
import com.framework.db.core.model.mapper.JsonTypeMapper;
import com.framework.db.core.model.mapper.MapTypeMapper;
import com.framework.db.core.model.mapper.Mapper;
import com.framework.db.core.util.ParamUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.nlpcn.es4sql.domain.*;
import org.nlpcn.es4sql.parse.ElasticSqlExprParser;
import org.nlpcn.es4sql.query.maker.QueryMaker;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangteng on 2018/8/20.
 */
public abstract class AbstractSqlQueryParser implements SqlQueryParser{

    private Map<String,String> fieldMappingAlias = new HashMap<>();

    private boolean isAllField;

    public SearchRequest initSearchRequest(){
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchRequest.source(searchSourceBuilder);
        return searchRequest;
    }

    public void setFieldMappingAlias(Select query){
        List<Field> fields =  query.getFields();
        if(fields.size() == 0){
            isAllField = true;
            return;
        }
        //doc['action_name'].value
        for(Field field:fields){
            if(field instanceof MethodField){
                MethodField methodField = (MethodField)field;
                List<KVValue> kvValues = methodField.getParams();
                if(methodField.getName().equals("script")){
                    String value = kvValues.get(1).value.toString();
                    fieldMappingAlias.put(dealFieldValue(value),methodField.getAlias());
                }else{
                    String column = kvValues.get(0).value.toString();
                    String key = methodField.getName()+"("+column+")";
                    fieldMappingAlias.put(key,methodField.getAlias()==null?key:methodField.getAlias());
                }
            }else {
                fieldMappingAlias.put(field.getName(),field.getName());
            }
        }
    }

    private String dealFieldValue(String originValue){
        int startIndex = -1;
        int endIndex = -1;
        char[] originValueCharArr = originValue.toCharArray();
        for(int i = 0;i<originValueCharArr.length;i++){
            if('\'' == originValueCharArr[i] && startIndex == -1){
                startIndex = i;
            }

            if('\'' == originValueCharArr[i] && startIndex < i ){
                endIndex = i;
                break;
            }
        }
        return originValue.substring(startIndex+1,endIndex);
    }

    public void setIndicesAndTypes(SearchRequest searchRequest, Select query){
        String[] indexes = query.getIndexArr();
        for(String sqlIndex:indexes){
            String[] indexAndTypeArr = sqlIndex.split("\\.");
            String index = indexAndTypeArr[0];
            String type = indexAndTypeArr[1];
            if(!StringUtils.isEmpty(index)){
                searchRequest.indices(index);
            }
            if(!StringUtils.isEmpty(type)){
                searchRequest.types(type);
            }
        }
    }

    public void setWhere(SearchRequest searchRequest,Where where) throws Exception{
        SearchSourceBuilder searchSourceBuilder = searchRequest.source();
        BoolQueryBuilder queryBuilder = QueryMaker.explan(where,true);
        searchSourceBuilder.query(queryBuilder);
    }

    public static void setLimit(SearchRequest searchRequest,int from,int size){
        searchRequest.source().from(from <= 0 ? 0:from).size(size);
    }

    public void setSort(SearchRequest searchRequest, List<Order> orders){
        if(orders != null){
            for(Order order:orders){
               searchRequest.source().sort(order.getName(), SortOrder.valueOf(order.getType()));
            }
        }
    }

    private Map<String,Object> filterResult(Map<String,Object> sourceMap){
        if(isAllField()){
            return sourceMap;
        }else{
            Map<String,Object> newResult = new HashMap<>();
            for(Map.Entry<String,String> entry:fieldMappingAlias.entrySet()){
                Object t1 = sourceMap.get("log_id");
                Object sourceValue = sourceMap.get(entry.getKey().toString());
                if(null != sourceValue){
                    newResult.put(entry.getValue(),sourceValue);
                }
            }
            return newResult;
        }
    }

    public Object parseSingleResult(Mapper resultMapper,Map<String,Object> sourceMap) throws Exception{
        if(resultMapper instanceof MapTypeMapper){
            return filterResult(sourceMap);
        }else if(resultMapper instanceof JsonTypeMapper){
            return ParamUtils.parseOriginResultToJSON(filterResult(sourceMap));
        }else if(resultMapper instanceof CommonTypeMapper){
            return ParamUtils.parseOrginResultToBean(filterResult(sourceMap),resultMapper.getMapperClass(),((CommonTypeMapper)resultMapper).getAttributes());
        }else{
            throw new ExecuteException("没有明确解析结果模型");
        }
    }

    public Map<String, String> getFieldMappingAlias() {
        return fieldMappingAlias;
    }

    public void setFieldMappingAlias(Map<String, String> fieldMappingAlias) {
        this.fieldMappingAlias = fieldMappingAlias;
    }

    public boolean isAllField() {
        return isAllField;
    }

    public void setAllField(boolean allField) {
        isAllField = allField;
    }
}

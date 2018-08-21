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
import org.nlpcn.es4sql.domain.Order;
import org.nlpcn.es4sql.domain.Select;
import org.nlpcn.es4sql.domain.Where;
import org.nlpcn.es4sql.parse.ElasticSqlExprParser;
import org.nlpcn.es4sql.query.maker.QueryMaker;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by zhangteng on 2018/8/20.
 */
public abstract class AbstractSqlQueryParser implements SqlQueryParser{

    public SearchRequest initSearchRequest(){
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchRequest.source(searchSourceBuilder);
        return searchRequest;
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

    public Object parseSingleResult(Mapper resultMapper,Map<String,Object> sourceMap) throws Exception{
        if(resultMapper instanceof MapTypeMapper){
            return sourceMap;
        }else if(resultMapper instanceof JsonTypeMapper){
            return ParamUtils.parseOriginResultToJSON(sourceMap);
        }else if(resultMapper instanceof CommonTypeMapper){
            return ParamUtils.parseOrginResultToBean(sourceMap,resultMapper.getMapperClass(),((CommonTypeMapper)resultMapper).getAttributes());
        }else{
            throw new ExecuteException("没有明确解析结果模型");
        }
    }
}

package com.framework.db.core.sql;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.framework.db.core.exception.ExecuteException;
import com.framework.db.core.model.mapper.Mapper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.nlpcn.es4sql.domain.Select;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangteng on 2018/8/20.
 */
public class SelectSqlQueryParser extends AbstractSqlQueryParser {

    private Select select;

    public SelectSqlQueryParser(Select select){
        this.select = select;
    }

    @Override
    public SearchRequest buildSearchRequest(){
        SearchRequest searchRequest = initSearchRequest();
        try{
            setFieldMappingAlias(select);
            setWhere(searchRequest,select.getWhere());
            setSort(searchRequest,select.getOrderBys());
            setLimit(searchRequest,select.getOffset(),select.getRowCount());
            setIndicesAndTypes(searchRequest,select);
        }catch (Exception e){
            throw new ExecuteException("sql parse error",e);
        }
        return searchRequest;
    }

    @Override
    public List getSqlResult(SearchResponse searchResponse, Mapper resultMapper) {
        SearchHits searchHits = searchResponse.getHits();
        List result = new LinkedList<>();
        if(searchHits.totalHits > 0){
            try{
                for(SearchHit searchHit : searchHits.getHits()){
                    Map<String,Object> tempResult =  searchHit.getSourceAsMap();
                    result.add(parseSingleResult(resultMapper,tempResult));
                }
            }catch (Exception e){
                throw new ExecuteException("查询结果解析失败",e);
            }
        }
        return result;
    }
}

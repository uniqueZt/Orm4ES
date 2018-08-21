package com.framework.db.core.sql;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.framework.db.core.model.mapper.Mapper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;

import java.util.List;

/**
 * Created by zhangteng on 2018/8/20.
 */
public interface SqlQueryParser {

    public SearchRequest buildSearchRequest();

    public List getSqlResult(SearchResponse searchResponse, Mapper resultMapper);

}

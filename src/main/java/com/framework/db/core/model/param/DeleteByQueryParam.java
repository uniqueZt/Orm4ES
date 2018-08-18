package com.framework.db.core.model.param;

import org.elasticsearch.index.query.QueryBuilder;

/**
 * Created by zhangteng on 2018/8/18.
 */
public class DeleteByQueryParam {

    private QueryBuilder queryBuilder;

    public QueryBuilder getQueryBuilder() {
        return queryBuilder;
    }

    public void setQueryBuilder(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }
}

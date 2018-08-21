package com.framework.db.core.sql.aggregation;

import java.util.List;

/**
 * Created by zhangteng on 2018/8/21.
 */
public class AggregationTree {

    private String field;

    private AggregationTree subAggregations;

    private List<CommonAggregation> commonAggregations;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public AggregationTree getSubAggregations() {
        return subAggregations;
    }

    public void setSubAggregations(AggregationTree subAggregations) {
        this.subAggregations = subAggregations;
    }

    public List<CommonAggregation> getCommonAggregations() {
        return commonAggregations;
    }

    public void setCommonAggregations(List<CommonAggregation> commonAggregations) {
        this.commonAggregations = commonAggregations;
    }
}

package com.framework.db.core.sql.aggregation;

/**
 * Created by zhangteng on 2018/8/21.
 */
public class CommonAggregation {

    public final static String SUM = "SUM";

    public final static String MAX = "MAX";

    public final static String MIN = "MIN";

    public final static String COUNT = "COUNT";

    public final static String AVG = "AVG";

    private String aggsType;

    private String alias;

    public String getAggsType() {
        return aggsType;
    }

    public void setAggsType(String aggsType) {
        this.aggsType = aggsType;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}

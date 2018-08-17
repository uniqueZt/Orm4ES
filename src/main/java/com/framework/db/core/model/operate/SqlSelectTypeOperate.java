package com.framework.db.core.model.operate;

/**
 * Created by zhangteng on 2018/8/17.
 */
public class SqlSelectTypeOperate extends Operate{

    public final static long defaultSqlSelectSize = 200L;

    private String formatSql;

    public String getFormatSql() {
        return formatSql;
    }

    public void setFormatSql(String formatSql) {
        this.formatSql = formatSql;
    }
}

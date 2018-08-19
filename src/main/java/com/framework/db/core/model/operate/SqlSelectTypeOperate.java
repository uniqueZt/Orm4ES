package com.framework.db.core.model.operate;

import com.framework.db.core.model.cache.KeyValuePair;

import java.util.*;

/**
 * Created by zhangteng on 2018/8/17.
 */
public class SqlSelectTypeOperate extends Operate{

    static class SqlSegment{
        private String value;
        private boolean isParamter;

        public SqlSegment(boolean isParamter,String value) {
            this.value = value;
            this.isParamter = isParamter;
        }

        public boolean isParamter(){
            return isParamter;
        }

        public String getValue(){
            return value;
        }
    }

    public static class SqlWithParamterBuilder{

        public static final char OPEN = '{';

        public static final char CLOSE = '}';

        private List<SqlSegment> sqlSegments = new LinkedList<>();

        private Set<String> params = new HashSet<>();

        public void appendSqlSegment(String value){
            sqlSegments.add(new SqlSegment(false,value));
        }

        public void appendParameter(String value){
            params.add(value);
            sqlSegments.add(new SqlSegment(true,value));
        }

        public List<SqlSegment> getSqlSegments() {
            return sqlSegments;
        }

        public int getParameterSize(){
            return params.size();
        }

        public String buildSql(Map<String,Object> parameters){
            StringBuilder sqlBuilder = new StringBuilder();
            for(SqlSegment sqlSegment:sqlSegments){
                if(sqlSegment.isParamter()){
                    sqlBuilder.append(parameters.get(sqlSegment.getValue()));
                }else{
                    sqlBuilder.append(sqlSegment.getValue());
                }
            }
            return sqlBuilder.toString();
        }

    }

    public final static long defaultSqlSelectSize = 200L;

    private String formatSql;

    private SqlWithParamterBuilder sqlWithParamterBuilder;

    public String getFormatSql() {
        return formatSql;
    }

    public void setFormatSql(String formatSql) {
        this.formatSql = formatSql;
    }

    public SqlWithParamterBuilder getSqlWithParamterBuilder() {
        return sqlWithParamterBuilder;
    }

    public void setSqlWithParamterBuilder(SqlWithParamterBuilder sqlWithParamterBuilder) {
        this.sqlWithParamterBuilder = sqlWithParamterBuilder;
    }

    public int getParamSize(){
        return sqlWithParamterBuilder.getParameterSize();
    }

    public String buildSql(Map<String,Object> parameters){
        return sqlWithParamterBuilder.toString();
    }
}

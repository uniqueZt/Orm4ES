package com.framework.db.core.util;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.Token;
import com.framework.db.core.exception.ExecuteException;
import com.framework.db.core.sql.AggregationSqlQueryParser;
import com.framework.db.core.sql.SelectSqlQueryParser;
import com.framework.db.core.sql.SqlQueryParser;
import org.nlpcn.es4sql.domain.Select;
import org.nlpcn.es4sql.parse.ElasticSqlExprParser;
import org.nlpcn.es4sql.parse.SqlParser;

/**
 * Created by zhangteng on 2018/8/21.
 */
public class SqlParserUtils {

    public static SqlQueryParser parseSqlExprParser(String sql) throws Exception {
        SQLExprParser parser = new ElasticSqlExprParser(sql);
        SQLExpr expr = parser.expr();
        if(parser.getLexer().token() != Token.EOF){
            throw new ExecuteException("sql parse error");
        }
        Select select = new SqlParser().parseSelect((SQLQueryExpr) expr);
        if(select.isAgg){
            return new AggregationSqlQueryParser(select);
        }else{
            return new SelectSqlQueryParser(select);
        }
    }
}

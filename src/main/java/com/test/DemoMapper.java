package com.test;

import com.framework.db.core.model.operate.RefreshType;
import com.framework.db.core.parse.annotation.config.Namespace;
import com.framework.db.core.parse.annotation.config.operate.*;
import com.framework.db.core.parse.annotation.parameter.Key;
import com.framework.db.core.parse.annotation.parameter.Parameter;
import com.framework.db.core.parse.annotation.parameter.Query;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by zhangteng on 2018/8/17.
 */

@Namespace
@Repository
public interface DemoMapper {

    @Insert(index = "log_info",type = "log",refresh = RefreshType.NONE,parameter = "demoBean")
    void insertTest(@Key String key, @Parameter DemoBean demoBean);

    //void insertTest2(@Key String key, @Parameter NestedBean nestedBean);

    @UpdateByKey(index = "log_info",type = "log",refresh = RefreshType.NONE,parameter = "demoBean")
    void updateTest(@Key String key, @Parameter DemoBean demoBean);

    @DeleteByKey(index = "log_info",type = "log",refresh = RefreshType.NONE)
    void deleteTest(@Key String key);

    @DeleteByQuery(index = "log_info",type="log")
    void deleteQueryTest(@Query QueryBuilder queryBuilder);

    @Select(index = "log_info",type = "log",result = "demoBean")
    List<DemoBean> selectTest1(@Query QueryBuilder queryBuilder);

    @Select(index = "log_info",type = "log",result = "demoBean",size = 1000,time = 20000,scroll = true)
    List<Map<String,Object>> selectTest2(@Query QueryBuilder queryBuilder);

    @SqlSelect(result = "demoBean",sql="select action_name as acName,log_id as logId,log_content\n" +
            "        from log_info.log\n" +
            "        where action_name = {actionName}")
    List<DemoBean> sqlSelectTest(@Parameter(value = "actionName") String testName);

    //List<Map<String,Object>> sqlSelectTest2();

}

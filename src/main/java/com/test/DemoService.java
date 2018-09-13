package com.test;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * Created by zhangteng on 2018/8/18.
 */
public class DemoService {

    @Autowired
    private DemoMapperTest demoMapper;

    public void test(){
        demoMapper.insertTest("123",new DemoBean());
        demoMapper.updateTest("123",new DemoBean());
        demoMapper.deleteTest("456");
        demoMapper.deleteQueryTest(QueryBuilders.termQuery("1test","123"));
        List<DemoBean> result1 = demoMapper.selectTest1(QueryBuilders.termQuery("2test","456"));
        List<Map<String,Object>> result2 = demoMapper.selectTest2(QueryBuilders.termQuery("3test","678"));
        List<DemoBean> result3 = demoMapper.sqlSelectTest("12345");
    }

    public void testInsert(String key,DemoBean demoBean){
        demoMapper.insertTest(key,demoBean);
    }

    public void insertTest2(NestedBean nestedBean) throws Exception{
        Thread.sleep(10);
        //demoMapper.insertTest2(System.currentTimeMillis()+"",nestedBean);
    }

    public void testUpdate(String key,DemoBean demoBean){
        demoMapper.updateTest(key,demoBean);
    }

    public List<DemoBean> testSelect1(){
        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
        return demoMapper.selectTest1(queryBuilder);
    }

    public List<Map<String,Object>> testSelect2(){
        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
        return demoMapper.selectTest2(queryBuilder);
    }

    public List<DemoBean> testSqlSelect(String actionName){
       return  demoMapper.sqlSelectTest(actionName);
    }

    public List<Map<String,Object>> testSqlSelect2(){
        //return demoMapper.sqlSelectTest2();
        return null;
    }
}

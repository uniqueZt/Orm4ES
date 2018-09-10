package com.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * Created by zhangteng on 2018/8/16.
 */
public class DemoClass {

    public static void main(String[] args) throws Exception{
       test5();
    }

    public static void test5() throws Exception {
        ApplicationContext apx = new ClassPathXmlApplicationContext("classpath:spring/spring_annotation.xml");
        DemoMapperTest demoMapperTest = apx.getBean(DemoMapperTest.class);
        List<DemoBean> result = demoMapperTest.sqlSelectTest("insertTest");
        for(DemoBean demoBean:result){
           System.out.println(demoBean);
        }
    }

    public static void test4() throws Exception {
        ApplicationContext apx = new ClassPathXmlApplicationContext("classpath:spring/spring_annotation.xml");
    }

    public static void test3() throws Exception{
        ApplicationContext apx = new ClassPathXmlApplicationContext("classpath:spring/spring.xml");
        DemoService demoService = apx.getBean(DemoService.class);
        for(int i = 0;i<1;i++){
            NestedBean nestedBean = new NestedBean();
            nestedBean.setId(i);
            nestedBean.setName("test"+i);

            DemoBean demoBean = new DemoBean();
            nestedBean.setBean(demoBean);
           // nestedBean.setAttr(demoBean);
            demoBean.setLogId(i+"");
            demoBean.setLogContent("测试insert");
            demoBean.setActionName("updateTest");
            demoBean.setCreateTime(null);
            //demoService.testInsert("12345",demoBean);
            demoBean.setLogContent("测试update");
            demoService.insertTest2(nestedBean);
            System.out.println("跟新完毕");
        }
    }

    public static void test2() throws  Exception{
       // ApplicationContext apx = new ClassPathXmlApplicationContext("classpath:spring/spring.xml");
        ApplicationContext apx = new ClassPathXmlApplicationContext("classpath:spring/spring_annotation.xml");
        DemoService demoService = apx.getBean(DemoService.class);
        for(int i = 0;i<3;i++){
            DemoBean demoBean = new DemoBean();
            demoBean.setLogId(i+"");
            demoBean.setLogContent("测试insert123");
            demoBean.setActionName("updateTest123");
            demoBean.setCreateTime(null);
            //demoService.testInsert("12345",demoBean);
            demoBean.setLogContent("测试update123");
            Thread.sleep(10);
            demoService.testInsert(System.currentTimeMillis()+"",demoBean);
        }
        System.out.println("新增完毕");
    }

    public static void test(){
        ApplicationContext apx = new ClassPathXmlApplicationContext("classpath:spring/spring.xml");
        DemoService demoService = apx.getBean(DemoService.class);
        //demoService.test();
        DemoBean demoBean = new DemoBean();
        demoBean.setLogId("123");
        demoBean.setLogContent("测试insert");
        demoBean.setActionName("updateTest");
        demoBean.setCreateTime(null);
        //demoService.testInsert("12345",demoBean);
        demoBean.setLogContent("测试update");
        //demoService.testUpdate("786",demoBean);
//        List<DemoBean> result1 = demoService.testSelect1();
//        for(DemoBean demoBean1:result1){
//            System.out.println(demoBean1.getLogId()+","+demoBean1.getActionName()+","+demoBean1.getCreateTime()+","+demoBean1.getLogContent());
//        }
//        List<Map<String,Object>> result2 = demoService.testSelect2();
        //System.out.println(result2);

        System.out.println(demoService.testSqlSelect("insertTest"));
        System.out.println(123);
    }
}

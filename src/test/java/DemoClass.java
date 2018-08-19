import com.framework.db.core.model.namespace.NamespaceSettings;
import com.framework.db.core.parse.xml.XmlConfigParser;
import com.framework.db.core.parse.xml.XmlConfigSettingsParser;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;
import java.util.Map;

/**
 * Created by zhangteng on 2018/8/16.
 */
public class DemoClass {

    public static void main(String[] args) throws Exception{
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
        List<DemoBean> result1 = demoService.testSelect1();
        for(DemoBean demoBean1:result1){
            System.out.println(demoBean1.getLogId()+","+demoBean1.getActionName()+","+demoBean1.getCreateTime()+","+demoBean1.getLogContent());
        }
        List<Map<String,Object>> result2 = demoService.testSelect2();
        System.out.println(result2);
        System.out.println(123);
    }
}

import com.framework.db.core.model.namespace.NamespaceSettings;
import com.framework.db.core.parse.xml.XmlConfigParser;
import com.framework.db.core.parse.xml.XmlConfigSettingsParser;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by zhangteng on 2018/8/16.
 */
public class DemoClass {

    public static void main(String[] args) throws Exception{
        ApplicationContext apx = new ClassPathXmlApplicationContext("classpath:spring/spring.xml");
        DemoService demoService = apx.getBean(DemoService.class);
        demoService.test();
        System.out.println(123);
    }
}

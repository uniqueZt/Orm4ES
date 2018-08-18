import com.framework.db.core.model.namespace.NamespaceSettings;
import com.framework.db.core.parse.xml.XmlConfigParser;
import com.framework.db.core.parse.xml.XmlConfigSettingsParser;

/**
 * Created by zhangteng on 2018/8/16.
 */
public class DemoClass {

    public static void main(String[] args) throws Exception{
        XmlConfigParser xmlConfigParser = new XmlConfigParser();
        xmlConfigParser.setSettingPath("classpath:test/es_setting.xml");
        xmlConfigParser.parse();
        NamespaceSettings namespaceSettings = NamespaceSettings.getInstance();
        System.out.println(namespaceSettings);
    }
}

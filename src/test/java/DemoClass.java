import com.framework.db.core.parse.xml.XmlConfigSettingsParser;

/**
 * Created by zhangteng on 2018/8/16.
 */
public class DemoClass {

    public static void main(String[] args) throws Exception{
        XmlConfigSettingsParser xmlConfigSettingsParser = new XmlConfigSettingsParser();
        xmlConfigSettingsParser.parseXmlFile("classpath:test/es_setting.xml");
        System.out.println(xmlConfigSettingsParser);
    }
}

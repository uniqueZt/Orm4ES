import java.util.Map;

/**
 * Created by zhangteng on 2018/8/25.
 */
public class NestedBean {

    private Integer id;

    private String name;

    private DemoBean bean;

    private Object attr;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DemoBean getBean() {
        return bean;
    }

    public void setBean(DemoBean bean) {
        this.bean = bean;
    }

    public Object getAttr() {
        return attr;
    }

    public void setAttr(Object attr) {
        this.attr = attr;
    }
}

package com.framework.db.core.model.mapper;

/**
 * Created by zhangteng on 2018/8/17.
 * 主要用来映射bean和db属性和字段
 */
public class Attributes{

    public final static String PROPERTY = "property";

    public final static String COLUMN = "column";

    public final static String JSON = "json";

    public final static String NESTED = "nested";

    //bean property
    private String property;

    //db column
    private String column;

    //标识字段是否以json的格式存储在数据库中
    private boolean json;

    //标识字段是否是嵌套字段
    private boolean nested;

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public boolean isJson() {
        return json;
    }

    public void setJson(boolean json) {
        this.json = json;
    }

    public boolean isNested() {
        return nested;
    }

    public void setNested(boolean nested) {
        this.nested = nested;
    }
}

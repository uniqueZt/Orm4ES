package com.test;

import com.framework.db.core.parse.annotation.config.mapper.Attribute;
import com.framework.db.core.parse.annotation.config.mapper.Mapper;

/**
 * Created by zhangteng on 2018/8/25.
 */
@Mapper(namespace = DemoMapperTest.class ,name = "nestedTest")
public class NestedBean {

    @Attribute(column = "id")
    private Integer id;

    @Attribute
    private String name;

    @Attribute(isNested = true)
    private DemoBean bean;

    @Attribute(isJson = true)
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

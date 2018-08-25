package com.framework.db.core.model.mapper;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhangteng on 2018/8/17.
 * 主要用来存放多个字段的对应关系，以及真实的类
 */
public abstract class Mapper {

    public final static String ATTRIBUTE = "attribute";

    public final static String CLASS = "class";

    public final static String NAME = "name";

    public boolean haveNestedProperty;

    private Class<?> mapperClass;//标识mapper映射的类

    public Class<?> getMapperClass() {
        return mapperClass;
    }

    public void setMapperClass(Class<?> mapperClass) {
        this.mapperClass = mapperClass;
    }

    public boolean isHaveNestedProperty() {
        return haveNestedProperty;
    }

    public void setHaveNestedProperty(boolean haveNestedProperty) {
        this.haveNestedProperty = haveNestedProperty;
    }
}

package com.framework.db.core.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhangteng on 2018/8/17.
 * 自定义类型的mapper
 */
public class CommonTypeMapper extends Mapper{

    //存放多个映射属性
    private List<Attributes> attributes = new LinkedList<Attributes>();

    public List<Attributes> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attributes> attributes) {
        this.attributes = attributes;
    }
}

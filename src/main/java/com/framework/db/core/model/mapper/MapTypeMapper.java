package com.framework.db.core.model.mapper;

import com.framework.db.core.model.mapper.Attributes;
import com.framework.db.core.model.mapper.Mapper;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhangteng on 2018/8/17.
 * 内置类型（map）mapper，暂无任何属性
 */
public class MapTypeMapper extends Mapper {

    //存放多个映射属性
    private List<Attributes> attributes = new LinkedList<Attributes>();

    public List<Attributes> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attributes> attributes) {
        this.attributes = attributes;
    }
}

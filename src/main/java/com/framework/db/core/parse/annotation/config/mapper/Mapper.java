package com.framework.db.core.parse.annotation.config.mapper;

import java.lang.annotation.*;

/**
 * Created by zhangteng on 2018/8/31.
 */
@Documented
@Target(ElementType.TYPE)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Mapper {
    Class namespace();
    String name();
}

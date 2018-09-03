package com.framework.db.core.parse.annotation.config.mapper;

import java.lang.annotation.*;

/**
 * Created by zhangteng on 2018/8/31.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Attribute {
    String column();
    boolean isJson() default false;
    boolean isNested() default  false;
}

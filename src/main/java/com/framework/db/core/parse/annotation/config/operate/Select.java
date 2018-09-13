package com.framework.db.core.parse.annotation.config.operate;

import java.lang.annotation.*;

/**
 * Created by zhangteng on 2018/9/2.
 */
@Documented
@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Select {
    String index();
    String type();
    String result();
    long size() default 0L;
    long time() default 0L;
    boolean scroll() default false;
}

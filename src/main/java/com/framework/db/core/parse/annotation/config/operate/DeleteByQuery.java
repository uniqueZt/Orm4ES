package com.framework.db.core.parse.annotation.config.operate;

import com.framework.db.core.model.operate.RefreshType;

import java.lang.annotation.*;

/**
 * Created by zhangteng on 2018/9/2.
 */
@Documented
@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface DeleteByQuery {
    String index();
    String type();
}

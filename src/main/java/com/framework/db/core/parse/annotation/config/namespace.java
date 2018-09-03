package com.framework.db.core.parse.annotation.config;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Created by zhangteng on 2018/8/31.
 */
@Documented
@Target({ElementType.TYPE,ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Namespace {

}

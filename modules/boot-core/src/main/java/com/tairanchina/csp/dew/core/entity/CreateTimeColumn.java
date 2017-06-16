package com.tairanchina.csp.dew.core.entity;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CreateTimeColumn {

    // 默认为类名（驼峰转下划线）
    String columnName() default "";

}

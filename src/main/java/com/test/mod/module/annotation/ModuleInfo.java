package com.test.mod.module.annotation;

import com.test.mod.language.Text;
import com.test.mod.module.Category;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ModuleInfo {
    Text[] name();

    Category category();

    int key() default 0;

    boolean enable() default false;
}

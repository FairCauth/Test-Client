package com.test.mod.transformer.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface At {
    String value();
    String target() default "";
    int ordinal() default -1;
    Shift shift() default Shift.BEFORE;

    enum Shift {
        BEFORE,
        AFTER,
        NONE
    }
}
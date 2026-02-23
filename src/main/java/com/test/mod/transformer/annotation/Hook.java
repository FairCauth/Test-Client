package com.test.mod.transformer.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@TransformerMeta(priority = 0)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Hook {
    String[] methodName();
    String desc();
    At at();
    int[] locals() default {};
}

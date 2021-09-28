package com.vaxtomis.yaml4test.Annotation;

import java.lang.annotation.*;

/**
 * @description Annotation Yaml4test.
 * @author vaxtomis
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Yaml4test {
    String Path() default "";
}

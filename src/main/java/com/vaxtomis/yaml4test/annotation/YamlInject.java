package com.vaxtomis.yaml4test.Annotation;

import java.lang.annotation.*;

/**
 * @description Annotation YamlInject.
 * @author vaxtomis
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface YamlInject {
    String Name() default "";
    enum Scope {Singleton, Prototype};
    Scope Scope() default Scope.Singleton;
}
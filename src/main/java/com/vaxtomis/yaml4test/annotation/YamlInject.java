package com.vaxtomis.yaml4test.annotation;

import java.lang.annotation.*;

import static com.vaxtomis.yaml4test.common.Define.EMPTY;

/**
 * <p>
 * Annotation YamlInject.
 * </p>
 * @author vaxtomis
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface YamlInject {
    String Name() default EMPTY;
    enum Scope {Singleton, Prototype};
    Scope Scope() default Scope.Singleton;
}
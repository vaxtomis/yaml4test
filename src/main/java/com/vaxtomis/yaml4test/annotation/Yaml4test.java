package com.vaxtomis.yaml4test.annotation;

import java.lang.annotation.*;

import static com.vaxtomis.yaml4test.common.Define.EMPTY;

/**
 * <p>
 * Annotation Yaml4test.
 * </p>
 * @author vaxtomis
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Yaml4test {
    String Path() default EMPTY;
    enum Pack {CrossPack, InPack}
    Pack Pack() default Pack.InPack;
}

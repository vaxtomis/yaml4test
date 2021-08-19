package Annotation;

import java.lang.annotation.*;

/**
 * @description 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Yaml4test {
    String Path() default "";
}

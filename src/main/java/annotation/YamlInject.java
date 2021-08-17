package annotation;

import java.lang.annotation.*;

/**
 * @description
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface YamlInject {
}

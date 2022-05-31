package com.vaxtomis.yaml4test;

import com.vaxtomis.yaml4test.Annotation.Yaml4test;
import com.vaxtomis.yaml4test.Annotation.YamlInject;
import com.vaxtomis.yaml4test.Parser.Parser;
import com.vaxtomis.yaml4test.Producer.Producer;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @description
 * 调用服务的主要入口，包含多个静态方法。
 *
 * @author vaxtomis
 */
public class YamlFactory {
    private Parser parser;
    private Producer producer;
    private Map storageMap;
    private String path = "";
    private Class<?> clazz = null;
    private static YamlFactory yamlFactory;

    public YamlFactory() {
        parser =  new Parser();
        producer = new Producer();
        storageMap = new HashMap();
        producer.setInnerMap(storageMap);
    }

    /**
     * Get the address of the yaml file in the annotation,
     * create instances and store them in the storageMap,
     * inject the corresponding instances into the annotated field.
     *
     * 获取注解中的 yaml 文件地址，创建实例并存储在 storageMap 中，将对应的实例注入注解字段。
     *
     * @param context
     * @param <T>
     */
    public static <T> void refreshFactory(T context) {
        if (yamlFactory == null) {
            // init YamlFactory
            yamlFactory = new YamlFactory();
            
            // clear storageMap
            yamlFactory.storageMap.clear();
            
            // init Parser
            yamlFactory.parser = new Parser();
            
            // import the Class of context
            yamlFactory.setClazz(context.getClass());
            
            // import the Path of yaml file by annotation
            yamlFactory.setPathByAnnotation();

            //System.out.println("<=== parser.events ===>");
            //System.out.println(yamlFactory.parser.getEventList());
            
            // inject the event list to created parser
            yamlFactory.producer.setEvents(yamlFactory.parser.getEventList());
            
            // start building
            yamlFactory.producer.build();
        }
        // autowiring parameters to context
        yamlFactory.autowiring(context);
    }

    /**
     * Get object in storageMap by name.
     *
     * 通过名称获取 storageMap 中的实例。
     * @param name
     * @return Object
     */
    public static Object getBean(String name) {
        try {
            return BeanOperator.deepCopy(yamlFactory.storageMap.get(name));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        throw new YamlFactoryException("The return of getBean is null.");
    }

    private void setPathByAnnotation() {
        if (!clazz.isAnnotationPresent(Yaml4test.class)) {
            throw new YamlFactoryException("Do not find com.vaxtomis.yaml4test.Annotation 'Yaml4test'.");
        }
        Yaml4test annotation = clazz.getAnnotation(Yaml4test.class);
        path = annotation.Path();
        if (path.equals("")) {
            throw new YamlFactoryException("The file path is not set.");
        }
        if (annotation.Pack().equals(Yaml4test.Pack.CrossPack)) {
            producer.setClassPath("");
        } else {
            if (clazz.getPackage() != null) {
                producer.setClassPath(clazz.getPackage().getName() + ".");
            } else {
                producer.setClassPath("");
            }
        }
        parser.setPath(Objects.requireNonNull(clazz.getClassLoader().getResource("")).getPath() + path);
        //System.out.println(clazz.getClassLoader().getResource("").getPath() + path);
    }

    private <T> void autowiring(T context) {
        Field[] fields = clazz.getDeclaredFields();
        if (fields.length == 0) {
            throw new YamlFactoryException("Fields is empty.");
        }
        for (Field field : fields) {
            if (!field.isAnnotationPresent(YamlInject.class)) {
                continue;
            }
            if (field.getType().isArray() && field.getType().getComponentType().isPrimitive()) {
                throw new YamlFactoryException("Can not create primitive array.");
            }
            field.setAccessible(true);
            fieldAssignment(field, context);
        }
    }

    private <T> void fieldAssignment(Field field, T context) {
        String name = field.getAnnotation(YamlInject.class).Name();
        YamlInject.Scope scope = field.getAnnotation(YamlInject.class).Scope();
        name = name.equals("")?field.getName():name;
        try {
            if (scope.equals(YamlInject.Scope.Prototype)) {
                field.set(context, BeanOperator.deepCopy(storageMap.get(name)));
            } else {
                field.set(context, storageMap.get(name));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public static class YamlFactoryException extends RuntimeException {
        public YamlFactoryException (String msg, Throwable cause) {
            super(msg, cause);
        }
        public YamlFactoryException (String msg) {
            super(msg, null);
        }
    }
}

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
     * @param context
     * @param <T>
     */
    public static <T> void refreshFactory(T context) {
        if (yamlFactory == null) {
            yamlFactory = new YamlFactory();
        }
        yamlFactory.storageMap.clear();
        yamlFactory.parser = new Parser();
        yamlFactory.setClazz(context.getClass());
        yamlFactory.setPathByAnnotation();
        yamlFactory.producer.setEvents(yamlFactory.parser.getEventList());
        yamlFactory.producer.build();
        yamlFactory.autowiring(context);
    }

    /**
     * Get object in storageMap by name.
     * @param name
     * @return Object
     */
    public static Object getBean(String name) {
        return yamlFactory.storageMap.get(name);
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
        if (clazz.getPackage() != null) {
            producer.setClassPath(clazz.getPackage().getName() + ".");
        } else {
            producer.setClassPath("");
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
            field.setAccessible(true);
            String name = field.getAnnotation(YamlInject.class).Name();
            name = name.equals("")?field.getName():name;
            try {
                field.set(context, storageMap.get(name));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public class YamlFactoryException extends RuntimeException {
        public YamlFactoryException (String msg, Throwable cause) {
            super(msg, cause);
        }
        public YamlFactoryException (String msg) {
            super(msg, null);
        }
    }
}

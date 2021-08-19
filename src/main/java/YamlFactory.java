import Parser.Parser;
import Producer.Producer;
import annotation.Yaml4test;
import annotation.YamlInject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

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

    private void setPathByAnnotation() {
        if (!clazz.isAnnotationPresent(Yaml4test.class)) {
            throw new YamlFactoryException("Do not find Annotation 'Yaml4test'.");
        }
        Yaml4test annotation = clazz.getAnnotation(Yaml4test.class);
        path = annotation.Path();
        if (path.equals("")) {
            throw new YamlFactoryException("The file path is not set.");
        }
        parser.setPath(path);
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
            try {
                field.set(context, storageMap.get(field.getName()));
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
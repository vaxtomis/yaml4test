package com.vaxtomis.yaml4test;

import com.vaxtomis.yaml4test.parser.DeProducer;
import com.vaxtomis.yaml4test.parser.Event;
import com.vaxtomis.yaml4test.parser.EventOperator;
import com.vaxtomis.yaml4test.parser.ModifyCollector;
import com.vaxtomis.yaml4test.producer.Producer;
import com.vaxtomis.yaml4test.common.Define;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.vaxtomis.yaml4test.common.Define.*;

/**
 * <p>
 * Util for deep copy a instance, and change Event in EventList
 * for create a new object that is similar but not identical.
 * <br>
 * 把类还原成 EventList 然后通过 {@link Producer} 再生成 Class。<br>
 * {@link EventOperator} 可以读取并修改 EventList，配合 deepCopy 方法，
 * 用于创建一个相似但不完全相同且独立的类。
 * </p>
 * @author vaxtomis
 */
public class BeanOperator {
    private static Class clazz;
    private static DeProducer deProducer;
    private static Producer producer;

    /**
     * 深拷贝方法，基于给定 source 重新构建一个相同的类实例。
     * @param source Object be copied
     * @param <T> Class
     * @throws IllegalAccessException
     */
    public static <T> T deepCopy(T source) throws IllegalAccessException {
        init(source);
        //System.out.println("<=== deProducer.getEventList ===>");
        //System.out.println(deProducer.getEventList());
        buildInstance(deProducer.getEventList());
        return (T) producer.getCopyInstance();
    }

    public static <T> T modifyCopy(T source, String propName, String value) throws IllegalAccessException {
        init(source);
        EventOperator operator = new EventOperator(deProducer.getEventList(), formatName(propName), value);
        buildInstance(operator.rebuild());
        return (T) producer.getCopyInstance();
    }

    public static <T> T modifyCopy(T source, ModifyCollector collector) throws IllegalAccessException {
        init(source);
        HashMap<String, String> modifyMap = new HashMap<>(16);
        String[] names = collector.getNames();
        for (String name : names) {
            modifyMap.put(formatName(name), collector.getValue(name));
        }
        EventOperator operator = new EventOperator(deProducer.getEventList(), modifyMap);
        buildInstance(operator.rebuild());
        return (T) producer.getCopyInstance();
    }


    /**
     * 创建满足全排列批量修改的实例组。<br>
     * 首先用 Map 来存储是不行的， 因为 Map 的 Key 不重复。<br>
     * 先对改动组进行排列，然后再根据改动组排列去修改模板，避免相互引用出错的可能。
     *
     * @param source Object
     * @param collector {@link ModifyCollector}
     * @param <T> Class
     * @return List
     * @throws IllegalAccessException
     */
    public static <T> List<T> createModifiedGroup(T source, ModifyCollector collector) throws IllegalAccessException {
        LinkedList<T> modifyGroup = new LinkedList<>();
        init(source);
        EventOperator operator = new EventOperator(deProducer.getEventList());
        HashMap<String, String> modifyMap = new HashMap<>(16);
        String[] names = collector.getNames();
        String[][] matrix = collector.generateModifyMatrix();
        List<List<ModifyCollector.Position>> group = collector.generateGroup();
        for (List<ModifyCollector.Position> set : group) {
            modifyMap.clear();
            for (ModifyCollector.Position pos : set) {
                modifyMap.put(names[pos.getX()], matrix[pos.getX()][pos.getY()]);
            }
            operator.setModify(formatName(modifyMap));
            buildInstance(operator.rebuild());
            modifyGroup.add((T) producer.getCopyInstance());
        }
        return modifyGroup;
    }

    /**
     * 初始化工作
     */
    private static <T> void init(T source) throws IllegalAccessException {
        clazz = source.getClass();
        deProducer = new DeProducer();
        deProducer.parseToEvents(source, source.getClass());
        producer = new Producer();
        /*if (clazz.getPackage() != null) {
            classPath = clazz.getPackage().getName() + ".";
        }
        // 多层嵌套时也能找到路径
        while (clazz.isArray()) {
            clazz = clazz.getComponentType();
            classPath = clazz.getPackage().getName() + ".";
        }*/
    }

    /**
     * 传入 EventList 到 producer 中并进行赋值。
     */
    private static void buildInstance(LinkedList<Event> events) {
        producer.setClassPath(EMPTY);
        producer.setEvents(events);
        producer.setInnerMap(new HashMap(16));
        producer.build();
    }

    /**
     * Java 无法获取运行中的实例名，所以添加默认实例名 "CopyInstance"<br>
     * 处理 Array 和 Class 实例名需要变化，例如:<br>
     * <pre>
     * "classes[0].property1"
     * "class.property1"
     * </pre>
     * @param modifyMap HashMap
     * @return HashMap
     */
    private static HashMap<String, String> formatName(HashMap<String, String> modifyMap) {
        HashMap<String, String> newModifyMap = new HashMap<>(16);
        for (Map.Entry entry : modifyMap.entrySet()) {
            switch (checkModifiedName(entry.getKey().toString())) {
                case 0:
                    throw new InvalidFormatException("Invalid property name format.");
                case 1:
                    newModifyMap.put(COPY_INSTANCE_DOT + entry.getKey(), entry.getValue().toString());
                    break;
                case 2:
                    newModifyMap.put(COPY_INSTANCE + entry.getKey(), entry.getValue().toString());
                    break;
                default:

            }
        }
        return newModifyMap;
    }

    private static String formatName(String propertyName) {
        switch (checkModifiedName(propertyName)) {
            case 0:
                throw new InvalidFormatException("Invalid property name format.");
            case 1:
                return COPY_INSTANCE_DOT + propertyName;
            case 2:
                return COPY_INSTANCE + propertyName;
            default:
                return null;

        }
    }

    /**
     * TODO Need to optimize.
     */
    private static int checkModifiedName(String name) {
        String[] subNames = name.split("\\.");
        if (Define.PROPERTY_NAME_VARIABLE.matcher(subNames[0]).matches()) {
            return 1;
        }
        if (Define.BRACKETS_NUMBER.matcher(subNames[0]).matches()) {
            return 2;
        }
        return 0;
    }


    public static class InvalidFormatException extends RuntimeException {
        public InvalidFormatException(String msg) {
            super(msg, null);
        }
    }
}

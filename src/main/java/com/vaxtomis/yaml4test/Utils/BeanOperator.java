package com.vaxtomis.yaml4test.Utils;

import com.sun.istack.internal.NotNull;
import com.vaxtomis.yaml4test.Parser.DeParser;
import com.vaxtomis.yaml4test.Parser.Event;
import com.vaxtomis.yaml4test.Parser.EventOperator;
import com.vaxtomis.yaml4test.Producer.Producer;
import com.vaxtomis.yaml4test.Tokenizer.Define;

import java.util.*;

/**
 * @description Util for deep copy a instance, and change Event in EventList
 * for create a new object that is similar but not identical.
 *
 * 构想 把类还原成 EventList 然后通过 Producer 再生成 Class。
 *
 * EventOperator 可以读取并修改 EventList，配合 deepCopy 方法，
 * 用于创建一个相似但不完全相同且独立的类。
 * @author vaxtomis
 */
public class BeanOperator {
    private static Class clazz;
    private static DeParser deParser;
    private static Producer producer;
    private static String classPath;

    /**
     * 深拷贝方法，基于给定 T source 重新构建一个相同的类实例。
     * @param source
     * @param <T>
     * @throws IllegalAccessException
     */
    public static <T> T deepCopy(@NotNull T source) throws IllegalAccessException {
        createPrepare(source);
        instanceBuild(deParser.getEventList());
        return (T) producer.getCopyInstance();
    }

    public static <T> T modifyCopy(@NotNull T source, @NotNull String propertyName, @NotNull String value) throws IllegalAccessException {
        createPrepare(source);
        EventOperator operator = new EventOperator(deParser.getEventList(), formatName(propertyName), value);
        instanceBuild(operator.rebuild());
        return (T) producer.getCopyInstance();
    }

    public static <T> T modifyCopy(@NotNull T source, @NotNull HashMap<String, String> modifyMap) throws IllegalAccessException {
        createPrepare(source);
        EventOperator operator = new EventOperator(deParser.getEventList(), formatName(modifyMap));
        instanceBuild(operator.rebuild());
        return (T) producer.getCopyInstance();
    }


    /**
     * 创建满足树形分支批量修改的实例组。
     * 首先用 Map 来存储是不行的， 因为 Map 的 Key 不重复
     * 先对改动组进行排列，然后再根据改动组排列去修改模板
     * @param source
     * @param <T>
     * @return List
     * @throws IllegalAccessException
     */
    public static <T> List<T> createModifyGroup(@NotNull T source, @NotNull HashMap<String,String> modifyMap) throws IllegalAccessException {
        LinkedList<T> modifyGroup = new LinkedList<>();
        createPrepare(source);
        EventOperator operator = new EventOperator(deParser.getEventList());

        return modifyGroup;
    }

    private static <T> void createPrepare(@NotNull T source) throws IllegalAccessException {
        clazz = source.getClass();
        deParser = new DeParser();
        deParser.parseToEvents(source, source.getClass());
        producer = new Producer();
        classPath = "";
        if (clazz.getPackage() != null) {
            classPath = clazz.getPackage().getName() + ".";
        }
        // 多层嵌套时也能找到路径
        while (clazz.isArray()) {
            clazz = clazz.getComponentType();
            classPath = clazz.getPackage().getName() + ".";
        }
    }

    // 传入 EventList 到 producer 中并进行赋值。
    private static void instanceBuild(LinkedList<Event> events) {
        producer.setClassPath(classPath);
        producer.setEvents(events);
        producer.setInnerMap(new HashMap());
        producer.build();
    }

    /**
     * Java 无法获取运行中的实例名，所以添加默认实例名 “CopyInstance”
     * 处理 Array 和 Class 实例名需要变化，例如
     * "classes[0].property1"
     * "class.property1"
     *
     * @param modifyMap
     * @return
     */
    private static HashMap<String, String> formatName(HashMap<String, String> modifyMap) {
        HashMap<String, String> newModifyMap = new HashMap<>();
        for (Map.Entry entry : modifyMap.entrySet()) {
            switch (modifiedNameCheck(entry.getKey().toString())) {
                case 0:
                    throw new BeanOperatorException("Invalid property name format.");
                case 1:
                    newModifyMap.put("CopyInstance." + entry.getKey(), entry.getValue().toString());
                case 2:
                    newModifyMap.put("CopyInstance" + entry.getKey(), entry.getValue().toString());
            }
        }
        return newModifyMap;
    }

    private static String formatName(String propertyName) {
        switch (modifiedNameCheck(propertyName)) {
            case 0:
                throw new BeanOperatorException("Invalid property name format.");
            case 1:
                return "CopyInstance." + propertyName;
            case 2:
                return "CopyInstance" + propertyName;
        }
        return null;
    }

    /**
     * @attention Need to optimize.
     */
    private static int modifiedNameCheck(String name) {
        String[] subNames = name.split("\\.");
        if (Define.JAVA_VARIABLE.matcher(subNames[0]).matches()) {
            return 1;
        }
        if (Define.BRACKETS_NUMBER.matcher(subNames[0]).matches()) {
            return 2;
        }
        return 0;
    }

    public static class BeanOperatorException extends RuntimeException {
        public BeanOperatorException (String msg, Throwable cause) {
            super(msg, cause);
        }
        public BeanOperatorException (String msg) {
            super(msg, null);
        }
    }
}

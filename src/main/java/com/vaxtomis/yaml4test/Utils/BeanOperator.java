package com.vaxtomis.yaml4test.Utils;

import com.sun.istack.internal.NotNull;
import com.vaxtomis.yaml4test.Parser.DeParser;
import com.vaxtomis.yaml4test.Parser.Event;
import com.vaxtomis.yaml4test.Parser.EventOperator;
import com.vaxtomis.yaml4test.Producer.Producer;

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
        instanceBuild();
        return (T) producer.getCopyInstance();
    }

    public static <T> T modifyCopy(@NotNull T source, String key, String value) throws IllegalAccessException {
        createPrepare(source);
        modifyEvent(deParser.getEventList(), key, value);
        instanceBuild();
        return (T) producer.getCopyInstance();
    }

    public static <T> T modifyCopy(@NotNull T source, HashMap<String, String> modifyMap) throws IllegalAccessException {
        createPrepare(source);
        modifyEvents(deParser.getEventList(), modifyMap);
        instanceBuild();
        return (T) producer.getCopyInstance();
    }

    // 创建满足树形分支批量修改的实例组
    public static <T> T[] createModifyGroup(@NotNull T source, HashMap<String,String> modifyMap) throws IllegalAccessException {
        LinkedList<T> modifyGroup = new LinkedList<>();
        LinkedList<LinkedList<Event>> eventsQueue = new LinkedList<>();
        createPrepare(source);
        EventOperator operator = new EventOperator(deParser.getEventList(), modifyMap);
        operator.cutEvents();

        return null;
    }

    // 传入 Map 对 EventList 进行修改操作
    private static void modifyEvents(@NotNull LinkedList<Event> events, @NotNull HashMap modifyMap) {
        EventOperator operator = new EventOperator(events, modifyMap);
        operator.rebuild();
    }

    // 传入 K-V 修改 EventList 中的单个 Event
    private static void modifyEvent(@NotNull LinkedList<Event> events, String key, String value) {
        EventOperator operator = new EventOperator(events, key, value);
        operator.rebuild();
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

    private static void instanceBuild() {
        producer.setClassPath(classPath);
        producer.setEvents(deParser.getEventList());
        producer.setInnerMap(new HashMap());
        producer.build();
    }
}

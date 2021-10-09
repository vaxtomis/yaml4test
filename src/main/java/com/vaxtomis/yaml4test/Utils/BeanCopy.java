package com.vaxtomis.yaml4test.Utils;

import com.sun.istack.internal.NotNull;
import com.vaxtomis.yaml4test.Parser.DeParser;
import com.vaxtomis.yaml4test.Producer.Producer;

import java.util.HashMap;

/**
 * @description Util for deep copy a instance.
 * 构想 把类还原成 EventList 然后通过 Producer 再生成 Class
 * @author vaxtomis
 */
public class BeanCopy {
    public static <T> T deepCopy(@NotNull T source) throws IllegalAccessException {
        Class clazz = source.getClass();
        DeParser deParser = new DeParser();
        deParser.parseToEvents(source, source.getClass());
        Producer producer = new Producer();
        String classPath = "";
        if (clazz.getPackage() != null) {
            classPath = clazz.getPackage().getName() + ".";
        }
        // 多层嵌套时也能找到路径
        while (clazz.isArray()) {
            clazz = clazz.getComponentType();
            classPath = clazz.getPackage().getName() + ".";
        }
        producer.setClassPath(classPath);
        producer.setEvents(deParser.getEventList());
        producer.setInnerMap(new HashMap());
        producer.build();
        return (T) producer.getCopyInstance();
    }
}

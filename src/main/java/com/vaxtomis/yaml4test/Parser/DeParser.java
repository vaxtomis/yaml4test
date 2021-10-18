package com.vaxtomis.yaml4test.Parser;

import com.vaxtomis.yaml4test.Producer.Converter;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.LinkedList;

/**
 * @description
 * 使用反射把类实例转换为 EventList。
 * @author vaxtomis
 */
public class DeParser {
    private LinkedList<Event> events = new LinkedList<>();

    public void parseToEvents(Object source, Class<?> clazz) throws IllegalAccessException {
        events.add(Event.MAPPING_START);
        events.add(new NameEvent("CopyInstance"));
        if (clazz.isArray()) {
            sequenceStrategy(source);
        } else {
            events.add(new ClassNameEvent(path(clazz)));
            parse(source, clazz);
        }
        events.add(Event.MAPPING_END);
    }

    public LinkedList<Event> getEventList() {
        return events;
    }

    private void parse(Object parent, Class<?> pClazz) throws IllegalAccessException {
        events.add(Event.MAPPING_START);
        Field[] sFields = pClazz.getDeclaredFields();
        for (Field sField : sFields) {
            sField.setAccessible(true);
            switch (fieldType(sField)) {
                case "MAPPING":
                    parseMapping(parent, sField);
                    break;
                case "SEQUENCE":
                    parseSequence(parent, sField);
                    break;
                case "PRIMITIVE":
                    parsePrimitive(parent, sField);
                    break;
            }
        }
        events.add(Event.MAPPING_END);
    }
    private void parsePrimitive(Object parent, Field sField) throws IllegalAccessException {
        Object son = sField.get(parent);
        if(son == null) return;
        events.add(new NameEvent(sField.getName()));
        events.add(new ValueEvent((char)0, String.valueOf(son)));
    }

    // 解析 Mapping
    private void parseMapping(Object parent, Field sField) throws IllegalAccessException {
        String name = sField.getName();
        Object son = sField.get(parent);
        if (son == null) return;
        Class<?> sClazz = sField.getType();
        events.add(new NameEvent(name));
        events.add(new ClassNameEvent(path(sClazz)));
        parse(son, sClazz);
    }

    // 解析 Sequence
    private void parseSequence(Object parent, Field sField) throws IllegalAccessException {
        String cName = sField.getName();
        Object son = sField.get(parent);
        if (son == null) return;
        events.add(new NameEvent(cName));
        sequenceStrategy(son);
    }

    // 处理 array 中的每一个实例
    private void sequenceStrategy(Object obj) throws IllegalAccessException {
        events.add(Event.SEQUENCE_START);
        if (!Object.class.isAssignableFrom(obj.getClass().getComponentType())) {
            int length = Array.getLength(obj);
            for(int i = 0; i < length; i++) {
                String str = String.valueOf(Array.get(obj,i));
                parseEntry(str);
            }
        } else {
            Object[] parameters = (Object[]) obj;
            for(Object parameter : parameters) {
                parseEntry(parameter);
            }
        }
        events.add(Event.SEQUENCE_END);
    }

    private void parseEntry(Object parameter) throws IllegalAccessException {
        if (Converter.isPrimitive(parameter.getClass())) {
            events.add(new EntryEvent("value", String.valueOf(parameter)));
        } else {
            events.add(new EntryEvent("class", path(parameter.getClass())));
            parse(parameter, parameter.getClass());
        }
    }

    private void parseEntry(String str) {
        events.add(new EntryEvent("value", str));
    }

    /**
     * @description
     * 判断 Field 的类型，根据类型去筛选 parse 处理函数。
     * Collection 对应为 Sequence
     * Primitive 对应为 Key-Value
     * 其余为 Class 实例，对应为 Mapping
     * @param field
     * @return String
     */
    private String fieldType(Field field) {
        if (field.getType().isArray()) {
            return "SEQUENCE";
        } else if (Converter.isPrimitive(field.getType())) {
            return "PRIMITIVE";
        }
        return "MAPPING";
    }

    private String path(Class<?> clazz) {
        /*String packagePath = YamlFactory.class.getPackage().getName() + ".";
        return clazz.getName().substring(packagePath.length());*/
        return clazz.getSimpleName();
    }
}

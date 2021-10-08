package com.vaxtomis.yaml4test.Parser;

import com.vaxtomis.yaml4test.Producer.Converter;
import com.vaxtomis.yaml4test.YamlFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.LinkedList;

/**
 * @description
 * @author vaxtomis
 */
public class DeParser {
    private LinkedList<Event> events = new LinkedList<>();

    public void parseToEvents(Object source, Class<?> clazz) throws IllegalAccessException {
        events.add(Event.MAPPING_START);
        events.add(new NameEvent("CopyInstance"));
        events.add(new ClassNameEvent(path(clazz)));
        events.add(Event.MAPPING_START);
        parse(source, clazz);
        events.add(Event.MAPPING_END);
        events.add(Event.MAPPING_END);
    }

    public LinkedList<Event> getEventList() {
        return events;
    }

    private void parse(Object parent, Class<?> pClazz) throws IllegalAccessException {
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
    }
    private void parsePrimitive(Object parent, Field sField) throws IllegalAccessException {
        Object son = sField.get(parent);
        events.add(new NameEvent(sField.getName()));
        events.add(new ValueEvent((char)0, String.valueOf(son)));
    }

    private void parseMapping(Object parent, Field sField) throws IllegalAccessException {
        String name = sField.getName();
        Object son = sField.get(parent);
        Class<?> sClazz = sField.getType();
        events.add(new NameEvent(name));
        events.add(new ClassNameEvent(path(sClazz)));
        events.add(Event.MAPPING_START);
        parse(son, sClazz);
        events.add(Event.MAPPING_END);
    }

    // 需要细化
    private void parseSequence(Object parent, Field sField) throws IllegalAccessException {
        String cName = sField.getName();
        Object son = sField.get(parent);
        events.add(new NameEvent(cName));
        events.add(Event.SEQUENCE_START);
        if (!Object.class.isAssignableFrom(son.getClass().getComponentType())) {
            int length = Array.getLength(son);
            for(int i = 0; i < length; i++) {
                String str = String.valueOf(Array.get(son,i));
                parseEntry(str);
            }
        } else {
            Object[] parameters = (Object[]) son;
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
            events.add(Event.MAPPING_START);
            parse(parameter, parameter.getClass());
            events.add(Event.MAPPING_END);
        }
    }

    private void parseEntry(String str) {
        events.add(new EntryEvent("value", str));
    }

    /**
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

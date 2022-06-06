package com.vaxtomis.yaml4test.producer;

import com.vaxtomis.yaml4test.parser.*;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.Map;

import static com.vaxtomis.yaml4test.converter.ConverterRegister.injectObj;
import static com.vaxtomis.yaml4test.converter.ConverterRegister.injectObjs;
import static com.vaxtomis.yaml4test.tokenizer.Define.*;

/**
 * @description
 * Perform the corresponding function execution according to Events,
 * and finally store the mapping in the innerMap.
 *
 * 根据 Events 执行相应的方法，最后生成的对象存储到 InnerMap 中。
 *
 * @author vaxtomis
 */
public class Producer {
    private LinkedList<RawPair<?>> objectStack;
    private LinkedList<PairContainer> layerStack;
    private LinkedList<Event> events;
    private PairContainer curContainer;
    private Map<String,?> innerMap;
    private String classPath;

    public Producer() {
        objectStack = new LinkedList<>();
        layerStack = new LinkedList<>();
        events = null;
        curContainer = null;
        innerMap = null;
    }

    public void build() {
        if (events == null) {
            throw new ProducerException("Events are not set.");
        }
        if (events.isEmpty()) {
            throw new ProducerException("Events is empty.");
        }
        if (innerMap == null) {
            throw new ProducerException("InnerMap are not set.");
        }
        while (!events.isEmpty()) {
            Event event = events.getFirst();
            processEvent(event);
            events.removeFirst();
        }
        //System.gc();
    }

    /**
     * After a BLOCK is closed,
     * a set of cached RawPairs are used to inject
     * the created class instance in the stack.
     *
     * 在一组区块闭合后，将缓存在栈中的生键值对信息注入进创建好的类实例中。
     */
    public void injectProperty() {
        RawPair<?> rawPair = objectStack.pollLast();
        curContainer = layerStack.pollLast();

        //System.out.println(rawPair.getName() + " - " + rawPair.getValue());
        Object obj = rawPair.getValue();
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            // Skip the serialVersionUID
            if (field.getModifiers() == (Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL)) {
                continue;
            }
            field.setAccessible(true);
            boolean setterSuccess = false;
            Class<?> fClazz = field.getType();
            String fName = field.getName();
            Object rawPairValue = curContainer.getRawPairValue(fName);
            if (rawPairValue == null) {
                continue;
            }
            PropertyDescriptor descriptor = null;
            try {
                descriptor = new PropertyDescriptor(fName, clazz);
            } catch (IntrospectionException e) {
                e.printStackTrace();
            }
            assert descriptor != null;
            Method method = descriptor.getWriteMethod();
            // try setter inject
            setterSuccess = injectObj(method, fClazz, obj, rawPairValue);
            // If the injection fails (using the setter method),
            // try to assign the value directly.
            if (!setterSuccess) {
                // 判断是 Array 的情况下
                if (fClazz.isArray() && rawPairValue.getClass().getComponentType() == String.class) {
                    // Convert string array.
                    Class<?> componentType = fClazz.getComponentType();
                    injectObjs(componentType, field, obj, rawPairValue);
                } else {
                    try {
                        field.set(obj, rawPairValue);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        curContainer.clear();
        curContainer = layerStack.peekLast();
    }

    /**
     * @attention Need to optimize.
     * TODO
     */
    private void processEvent(Event event) {
        switch (event.getType()) {
            case MAPPING_START:
                prepareMapping();
                break;
            case SEQUENCE_START:
                prepareSequence();
                break;
            case GET_NAME:
                createRawPair(((NameEvent)event).getName());
                break;
            case GET_VALUE:
                if (event instanceof ValueEvent) {
                    putValue(((ValueEvent)event).getStyle(),((ValueEvent)event).getValue());
                }
                break;
            case GET_CLASSNAME:
                if (event instanceof ClassNameEvent) {
                    try {
                        //System.out.println(((ClassNameEvent)event).getName());
                        putClass(((ClassNameEvent)event).getName());
                    } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case GET_ENTRY:
                if (event instanceof EntryEvent) {
                    String entryType = ((EntryEvent)event).getEntryType();
                    String className = ((EntryEvent)event).getValue();
                    if (CLASS.equals(entryType)) {
                        createRawPair();
                        try {
                            putClass(className);
                        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                            e.printStackTrace();
                        }
                        objectStack.getLast().setValue(className);
                    }
                    else if (VALUE.equals(entryType)) {
                        createRawPair();
                        // <=== Need to optimize. ===>
                        putValue("String", ((EntryEvent)event).getValue());
                        objectStack.getLast().setValue("java.lang.String");
                    }
                }
                break;
            case MAPPING_END:
                if (1 == layerStack.size()) {
                    injectMap();
                } else {
                    injectProperty();
                }
                break;
            case SEQUENCE_END:
                addElement();
                break;
            default:

        }
    }

    private void injectMap() {
        curContainer = layerStack.pollLast();
        while (curContainer.size() > 0) {
            RawPair<?> rawPair = curContainer.pollLast();
            innerMap.put(rawPair.getName(), curContainer.getRawPairValue(rawPair.getName()));
        }
    }

    /**
     * 创建类映射的准备工作
     */
    private void prepareMapping() {
        if (0 == layerStack.size()) {
            layerStack.add(new PairContainer());
            curContainer = layerStack.getLast();
            return;
        }
        if (curContainer != null && curContainer.size() > 0) {
            objectStack.add(curContainer.getLast());
        }
        layerStack.add(new PairContainer());
        curContainer = layerStack.getLast();
    }

    /**
     * 创建队列的准备工作
     */
    private void prepareSequence() {
        if (curContainer != null && curContainer.size() > 0) {
            objectStack.add(curContainer.getLast());
        }
        layerStack.add(new PairContainer());
        curContainer = layerStack.getLast();
    }

    /**
     * 创建生键值对（即表示键值对的数据结构）
     * @param name
     */
    private void createRawPair(String name) {
        RawPair<?> pair = new RawPair<>(name);
        curContainer.add(pair);
    }

    private void createRawPair() {
        RawPair<?> pair = new RawPair<>(String.valueOf(curContainer.getPairNo()));
        curContainer.add(pair);
    }

    /**
     * @attention Need to optimize.
     * TODO
     *
     * Put the value(primitive type and their encapsulation class)
     * into RawPair
     *
     * 将 Value（基本类型和其衍生类型）放入生键值对。
     */
    private void putValue(String style, String value) {
        if (curContainer != null && curContainer.size() > 0) {
            RawPair rawPair = curContainer.getLast();
            rawPair.setValue(value);
        } else {
            throw new ProducerException("Need a RawPair to set value but the Container is empty.");
        }
    }

    /**
     * Obtain the {Class} through class name,
     * create new instance and put it into RawPair.
     *
     * 通过全限定名获取 Class，并且创建新实例放入生键值对中。
     */
    private void putClass(String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        //System.out.println(classPath + className);
        Class<?> clazz = Class.forName(classPath + className);
        if (curContainer != null && curContainer.size() > 0) {
            RawPair rawPair = curContainer.getLast();
            rawPair.setValue(clazz.newInstance());
        } else {
            throw new ProducerException("Need a RawPair to set class but the Container is empty.");
        }
    }

    private void addElement() {
        RawPair<?> rawPair = objectStack.pollLast();
        curContainer = layerStack.pollLast();
        String className = (String) rawPair.getValue();
        Class<?> clazz = null;
        try {
            if (STRING.equals(className)) {
                clazz = String.class;
            } else {
                clazz = Class.forName(classPath + className);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Object array =  Array.newInstance(clazz, curContainer.size());
        for (int i = 0; i < curContainer.size(); i++) {
            Array.set(array, i, curContainer.getRawPairValue(String.valueOf(i)));
        }
        rawPair.setValue(array);
        curContainer.clear();
        curContainer = layerStack.peekLast();
    }

    public void setEvents(LinkedList<Event> events) {
        this.events = events;
    }

    public void setInnerMap(Map innerMap) {
        this.innerMap = innerMap;
    }

    public void setClassPath(String path) {
        this.classPath = path;
    }

    public Object getCopyInstance() {
        return innerMap.getOrDefault("CopyInstance", null);
    }

    public class ProducerException extends RuntimeException {
        public ProducerException (String msg, Throwable cause) {
            super(msg, cause);
        }
        public ProducerException (String msg) {
            super(msg, null);
        }
    }
}

package Producer;

import Parser.*;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @description Perform the corresponding function execution according to Events,
 * and finally store the mapping in the innerMap.
 * @author vaxtomis
 */
public class Producer {
    private LinkedList<RawPair<?>> objectStack;
    private LinkedList<PairContainer> layerStack;
    private LinkedList<Event> events;
    private PairContainer curContainer;
    private Map<String,?> innerMap;

    public Producer() {
        objectStack = new LinkedList<>();
        layerStack = new LinkedList<PairContainer>();
        events = null;
        curContainer = null;
        innerMap = null;
    }

    public void build() {
        if (events == null) {
            throw new ProducerException("Events are not set.");
        }
        if (innerMap == null) {
            throw new ProducerException("InnerMap are not set.");
        }
        while (!events.isEmpty()) {
            Event event = events.getFirst();
            processEvent(event);
            events.removeFirst();
        }
        System.gc();
    }

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
                    if (entryType.equals("class")) {
                        createRawPair();
                        try {
                            putClass(className);
                        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                            e.printStackTrace();
                        }
                        objectStack.getLast().setValue(className);
                    }
                    else if (entryType.equals("value")) {
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
        }
    }


    private void injectMap() {
        curContainer = layerStack.pollLast();
        while (curContainer.size() > 0) {
            RawPair<?> rawPair = curContainer.pollLast();
            innerMap.put(rawPair.getName(), curContainer.getRawPairValue(rawPair.getName()));
        }
    }

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

    private void prepareSequence() {
        if (curContainer != null && curContainer.size() > 0) {
            objectStack.add(curContainer.getLast());
        }
        layerStack.add(new PairContainer());
        curContainer = layerStack.getLast();
    }

    private void createRawPair(String name) {
        RawPair<?> pair = new RawPair<>(name);
        curContainer.add(pair);
    }

    private void createRawPair() {
        RawPair<?> pair = new RawPair<>(String.valueOf(curContainer.getPairNo()));
        curContainer.add(pair);
    }

    /**
     * <=== Mark, need to optimize. ===>
     *
     * Put the value(primitive type and their encapsulation class)
     * into RawPair
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
     */
    private void putClass(String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        System.out.println(className);
        Class<?> clazz = Class.forName(className);
        if (curContainer != null && curContainer.size() > 0) {
            RawPair rawPair = curContainer.getLast();
            rawPair.setValue(clazz.newInstance());
        } else {
            throw new ProducerException("Need a RawPair to set value but the Container is empty.");
        }
    }

    /**
     * After a BLOCK is closed,
     * a set of cached RawPairs are used to inject
     * the created class instance in the stack.
     */
    public void injectProperty() {
        RawPair<?> rawPair = objectStack.pollLast();
        curContainer = layerStack.pollLast();

        //System.out.println(rawPair.getName() + " - " + rawPair.getValue());
        Object obj = rawPair.getValue();
        Class<?> clazz = obj.getClass();

        HashMap<String, Method> methodMap = new HashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            methodMap.put(method.getName(), method);
        }
        for (Field field : fields) {
            //Skip the serialVersionUID
            if (field.getModifiers() == (Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL)) {
                continue;
            }
            field.setAccessible(true);
            boolean setterSuccess = false;
            Type fType = field.getGenericType();
            String fName = field.getName();
            try {
                if (fType.equals(String.class)) {
                    String temp = curContainer.getRawPairValue(fName);
                    if (temp == null) temp = "";
                    setterSuccess = setterInject(methodMap, field, obj, temp);
                }
                else if (fType.equals(char.class) || fType.equals(Character.class)) {
                    String getV = curContainer.getRawPairValue(fName);
                    char temp = (getV != null)?getV.charAt(0):'\u0000';
                    setterSuccess = setterInject(methodMap, field, obj, temp);
                }
                else if (fType.equals(int.class) || fType.equals(Integer.class)) {
                    String getV = curContainer.getRawPairValue(fName);
                    int temp = (canParse(getV, "int"))?Integer.parseInt(getV):0;
                    setterSuccess = setterInject(methodMap, field, obj, temp);
                }
                else if (fType.equals(long.class) || fType.equals(Long.class)) {
                    String getV = curContainer.getRawPairValue(fName);
                    long temp = (canParse(getV, "long"))?Long.parseLong(getV):0L;
                    setterSuccess = setterInject(methodMap, field, obj, temp);
                }
                else if (fType.equals(double.class) || fType.equals(Double.class)) {
                    String getV = curContainer.getRawPairValue(fName);
                    double temp = (canParse(getV, "double"))?Double.parseDouble(getV):0.0d;
                    setterSuccess = setterInject(methodMap, field, obj, temp);
                }
                else if (fType.equals(float.class) || fType.equals(Float.class)) {
                    String getV = curContainer.getRawPairValue(fName);
                    float temp = (canParse(getV, "float"))?Float.parseFloat(getV):0.0f;
                    setterSuccess = setterInject(methodMap, field, obj, temp);
                }
                else if (fType.equals(short.class) || fType.equals(Short.class)) {
                    String getV = curContainer.getRawPairValue(fName);
                    short temp = (canParse(getV, "short"))?Short.parseShort(getV):0;
                    setterSuccess = setterInject(methodMap, field, obj, temp);
                }
                else if (fType.equals(boolean.class) || fType.equals(Boolean.class)) {
                    String getV = curContainer.getRawPairValue(fName);
                    boolean temp = (canParse(getV, "boolean")) && Boolean.parseBoolean(getV);
                    setterSuccess = setterInject(methodMap, field, obj, temp);
                }
                else if (fType.equals(BigInteger.class)) {
                    String getV = curContainer.getRawPairValue(fName);
                    BigInteger temp = (canParse(getV, "bigInteger"))?new BigInteger(getV):new BigInteger("0");
                    setterSuccess = setterInject(methodMap, field, obj, temp);
                }
                else if (fType.equals(BigDecimal.class)) {
                    String getV = curContainer.getRawPairValue(fName);
                    BigDecimal temp = (canParse(getV, "bigDecimal"))?new BigDecimal(getV):new BigDecimal("0.00");
                    setterSuccess = setterInject(methodMap, field, obj, temp);
                }
                else if (fType.equals(Byte.class)) {
                    String getV = curContainer.getRawPairValue(fName);
                    Byte temp = (canParse(getV, "byte"))?Byte.parseByte(getV, 16):0;
                    setterSuccess = setterInject(methodMap, field, obj, temp);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            //If the injection fails (using the setter method),
            //try to assign the value directly.
            if (!setterSuccess) {
                try {
                    if (field.getType().isArray()) {
                        //String 更新成自定义类型
                    } else {
                        field.set(obj, curContainer.getRawPairValue(fName));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        curContainer.clear();
        curContainer = layerStack.peekLast();
    }

    private void addElement() {
        RawPair<?> rawPair = objectStack.pollLast();
        curContainer = layerStack.pollLast();
        String className = (String) rawPair.getValue();
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
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

    private boolean setterInject(HashMap<String, Method> methodMap, Field field, Object obj, Object...temp) {
        String setMethodName = "set" + field.getName().substring(0,1).toUpperCase() + field.getName().substring(1);
        Method setMethod = methodMap.get(setMethodName);
        if (setMethod == null || obj == null) {
            return false;
        }
        try {
            setMethod.invoke(obj, temp);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * <=== Mark, Need to optimize. ===>
     *
     * Determine whether this type can be parsed.
     */
    private boolean canParse(String getV, String type) {
        if (getV == null) return false;
        switch (type) {
            case "int":
            case "short":
            case "long":
            case "bigInteger":
                return getV.matches("^(-|\\+)?\\d$");
            case "double":
            case "float":
            case "bigDecimal":
                return getV.matches("^(-?\\d+)(\\.\\d+)?$");
            case "byte":
                if (!getV.matches("^[0-9a-fA-F]+$")) {
                    return false;
                }
                int i = Integer.parseInt(getV, 16);
                return  i >= -128 && i <= 127;
            case "boolean":
                return true;
        }
        return false;
    }

    public void setEvents(LinkedList<Event> events) {
        this.events = events;
    }

    public void setInnerMap(Map innerMap) {
        this.innerMap = innerMap;
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

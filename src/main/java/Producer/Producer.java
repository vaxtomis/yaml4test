package Producer;

import Parser.ClassNameEvent;
import Parser.Event;
import Parser.NameEvent;
import Parser.ValueEvent;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @description
 * @author vaxtomis
 */
public class Producer {
    private LinkedList<RawPair<?>> objectStack;
    private LinkedList<PairContainer> layerStack;
    private LinkedList<Event> events;
    private PairContainer curContainer;
    private Map<String,?> innerMap;

    public Producer(LinkedList<Event> events) {
        objectStack = new LinkedList<>();
        layerStack = new LinkedList<PairContainer>();
        this.events = events;
        curContainer = null;
        innerMap = new HashMap<>();
    }

    public void build() {
        while (!events.isEmpty()) {
            Event event = events.getFirst();
            processEvent(event);
            events.removeFirst();
        }
    }

    private void processEvent(Event event) {
        switch (event.getType()) {
            case MAPPING_START:
                prepareEnvironment();
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
            case MAPPING_END:
                if (1 == layerStack.size()) {
                    injectMap();
                } else {
                    injectProperty();
                }
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

    private void prepareEnvironment() {
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

    private void createRawPair(String name) {
        RawPair<?> pair = new RawPair<>(name);
        curContainer.add(pair);
    }

    /**
     * Mark, need to be optimized.
     */
    private void putValue(String style, String value) {
        if (curContainer != null && curContainer.size() > 0) {
            RawPair rawPair = curContainer.getLast();
            rawPair.setValue(value);
        }
    }

    private void putClass(String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class<?> clazz = Class.forName(className);
        if (curContainer != null && curContainer.size() > 0) {
            RawPair rawPair = curContainer.getLast();
            rawPair.setValue(clazz.newInstance());
        }
    }

    public void injectProperty() {
        RawPair<?> rawPair = objectStack.pollLast();
        curContainer = layerStack.pollLast();
        Class<?> clazz = rawPair.getValue().getClass();
        Object obj = rawPair.getValue();
        HashMap<String, Method> methodMap = new HashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        Method[] methods = clazz.getDeclaredMethods();
        for(Method method : methods) {
            methodMap.put(method.getName(), method);
        }
        for (Field field : fields) {
            if (field.getModifiers() == (Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL)) {
                continue;
            }
            field.setAccessible(true);
            boolean setterSuccess = false;
            try {
                if (field.getGenericType().equals(String.class)) {
                    String temp = curContainer.getRawPairValue(field.getName());
                    setterSuccess = setterInject(methodMap, field, obj, temp);
                }
                else if (field.getGenericType().equals(Integer.class)) {
                    Integer temp = (Integer)curContainer.getRawPairValue(field.getName());
                    setterSuccess = setterInject(methodMap, field, obj, temp);
                }
                else if (field.getGenericType().equals(Long.class)) {
                    Long temp = Long.parseLong(curContainer.getRawPairValue(field.getName()));
                    setterSuccess = setterInject(methodMap, field, obj, temp);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            if (!setterSuccess) {
                try {
                    System.out.println(field.getGenericType());
                    field.set(obj, curContainer.getRawPairValue(field.getName()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
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

    public Map<String,?> getInnerMap() {
        return innerMap;
    }
}

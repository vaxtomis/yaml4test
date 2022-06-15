package com.vaxtomis.yaml4test.parser;

import com.vaxtomis.yaml4test.tokenizer.Define;

import java.util.*;

import static com.vaxtomis.yaml4test.tokenizer.Define.EMPTY;

/**
 * <p>
 * 遍历 EventList，并对符合条件的 Event 进行修改。<br>
 * 需要传入需要修改的属性名和值 (HashMap)。<br>
 * 添加了 EventList 切分，用于降低修改 EventList 的开销。
 * </p>
 * @author vaxtomis
 */
public class EventOperator {
    private Event curEvent;
    private ArrayList<String> pathName = new ArrayList<>();
    private LinkedList<Event> events;
    private LinkedList<Integer> seqIndexStack = new LinkedList<>();
    private HashMap<String,String> modifyMap;
    private int seqIndex = -1;
    private int pathNameIndex = 0;
    private boolean needToModify = false;
    private String modifiedEventName = EMPTY;
    private String value;

    public EventOperator(LinkedList<Event> events) {
        this.events = events;
    }

    public EventOperator(LinkedList<Event> events, HashMap modifyMap) {
        this.events = events;
        this.modifyMap = modifyMap;
    }

    public EventOperator(LinkedList<Event> events, String modifiedEventName, String value) {
        this.events = events;
        this.modifiedEventName = modifiedEventName;
        this.value = value;
    }

    /**
     * 根据 HashMap 创建修改 EventList。
     * 返回一个新的 EventList。
     */
    public LinkedList<Event> rebuild() {
        init();
        LinkedList<Event> modifiedEvents = new LinkedList<>();
        if (modifyMap != null) {
            for(Event event : events) {
                batchModify(modifiedEvents, event);
            }
        } else {
            for (int i = 0; i < events.size(); i++) {
                Event event = events.get(i);
                if (singleModify(modifiedEvents, event)) {
                    modifiedEvents.addAll(events.subList(i + 1, events.size()));
                    break;
                }
            }
        }
        return modifiedEvents;
    }

    /**
     *
     * @return LinkedList events
     */
    public LinkedList<Event> cutEvents() {
        if (modifyMap != null) {
            events = getCutEvents(modifyMap.keySet());
            return events;
        } else if (!EMPTY.equals(modifiedEventName)) {
            Set<String> set = new HashSet<>();
            set.add(modifiedEventName);
            events = getCutEvents(set);
            return events;
        }
        throw new EventOperatorException("Both modifyMap and kay-value pair is not defined.");
    }

    /**
     * 切分 Events，只取参与修改的部分，降低运行时开销。<br>
     * 切分完成的 EventList 依然通过 Producer 创建对应的 Map，
     * 但是会缺失未修改的类。
     * <br>
     * start end 用来标记 BLOCK，deep 保证 BLOCK 粒度最大。<br>
     * 检测到当前 BLOCK 中存在需要修改的 Event 名，将整个 BLOCK 的 Events
     * 放到 cutEvents 中。
     * @param modifiedEventNames Name of event which be modified
     */
    private LinkedList<Event> getCutEvents(Set<String> modifiedEventNames) {
        LinkedList<Event> cutEvents = new LinkedList<>();
        cutEvents.add(Event.MAPPING_START);
        boolean isTargetEvents = false;
        int start = 0;
        int end = 0;
        int deep = 0;
        for(Event event : events) {
            String fullName;
            switch (event.getType()) {
                case GET_NAME:
                    curEvent = event;
                    fullName = getFullName();
                    if (deep == 1) {
                        start = end;
                    }
                    if (modifiedEventNames.contains(fullName)) {
                        isTargetEvents = true;
                    }
                    break;
                case MAPPING_START:
                    mappingStart();
                    deep++;
                    break;
                case MAPPING_END:
                    mappingEnd();
                    deep--;
                    if (isTargetEvents && deep == 1) {
                        cutEvents.addAll(events.subList(start, end + 1));
                        isTargetEvents = false;
                    }
                    break;
                case SEQUENCE_END:
                    sequenceEnd();
                    deep--;
                    if (isTargetEvents && deep == 1) {
                        cutEvents.addAll(events.subList(start, end + 1));
                        isTargetEvents = false;
                    }
                    break;
                case SEQUENCE_START:
                    sequenceStart();
                    deep++;
                    break;
                case GET_ENTRY:
                    seqIndex++;
                    curEvent = event;
                    fullName = getFullName();
                    if (modifiedEventNames.contains(fullName)) {
                        isTargetEvents = true;
                    }
                    break;
                default:

            }
            end++;
        }
        cutEvents.add(Event.MAPPING_END);
        return cutEvents;
    }

    /**
     * 根据 HashMap 对对应的 Event 进行修改。
     * @param event
     */
    private void batchModify(LinkedList<Event> modifiedEvents, Event event) {
        String fullName;
        modifiedEvents.add(event);
        switch (event.getType()) {
            case GET_NAME:
                curEvent = event;
                fullName = getFullName();
                //System.out.println(fullName);
                if (modifyMap.containsKey(fullName)) {
                    needToModify = true;
                    modifiedEventName = fullName;
                }
                break;
            case MAPPING_START:
                mappingStart();
                break;
            case MAPPING_END:
                mappingEnd();
                break;
            case SEQUENCE_END:
                sequenceEnd();
                break;
            case SEQUENCE_START:
                sequenceStart();
                break;
            case GET_ENTRY:
                seqIndex++;
                curEvent = event;
                fullName = getFullName();
                //System.out.println(fullName);
                if (modifyMap.containsKey(fullName)) {
                    EntryEvent modifiedEvent = new EntryEvent("value", modifyMap.get(fullName));
                    modifiedEvents.removeLast();
                    modifiedEvents.add(modifiedEvent);
                }
                break;
            case GET_VALUE:
                if (needToModify) {
                    //((ValueEvent)event).setValue(modifyMap.get(modifiedEventName));
                    ValueEvent modifiedEvent = new ValueEvent('\'', modifyMap.get(modifiedEventName));
                    modifiedEvents.removeLast();
                    modifiedEvents.add(modifiedEvent);
                    needToModify = false;
                }
                break;
            default:

        }
    }

    /**
     * 根据 Key-Value 对对应 Event 进行修改。
     * @param event
     * @return
     */
    private boolean singleModify(LinkedList<Event> modifiedEvents, Event event) {
        String fullName;
        modifiedEvents.add(event);
        switch (event.getType()) {
            case GET_NAME:
                curEvent = event;
                fullName = getFullName();
                //System.out.println(fullName);
                if (fullName.equals(modifiedEventName)) {
                    needToModify = true;
                }
                break;
            case MAPPING_START:
                mappingStart();
                break;
            case MAPPING_END:
                mappingEnd();
                break;
            case SEQUENCE_END:
                sequenceEnd();
                break;
            case SEQUENCE_START:
                sequenceStart();
                break;
            case GET_ENTRY:
                seqIndex++;
                curEvent = event;
                fullName = getFullName();
                //System.out.println(fullName);
                if (fullName.equals(modifiedEventName)) {
                    EntryEvent modifiedEvent = new EntryEvent("value", value);
                    modifiedEvents.removeLast();
                    modifiedEvents.add(modifiedEvent);
                    return true;
                }
                break;
            case GET_VALUE:
                if (needToModify) {
                    //((ValueEvent)event).setValue(value);
                    ValueEvent modifiedEvent = new ValueEvent('\'', value);
                    modifiedEvents.removeLast();
                    modifiedEvents.add(modifiedEvent);
                    return true;
                }
                break;
            default:

        }
        return false;
    }

    /**
     * MAPPING_START 公用处理方法。
     */
    private void mappingStart() {
        if (curEvent != null) {
            handleNameAndEntry();
            pathNameIndex++;
        }
    }

    /**
     * MAPPING_END 公用处理方法。
     */
    private void mappingEnd() {
        if (pathNameIndex > 0) {
            pathName.remove(--pathNameIndex);
        }
    }

    /**
     * SEQUENCE_START 公用处理方法。
     */
    private void sequenceStart() {
        handleNameAndEntry();
        pathNameIndex++;
        if (seqIndex > -1) {
            seqIndexStack.push(seqIndex);
        }
        seqIndex = -1;
    }

    /**
     * SEQUENCE_END 公用处理方法。
     */
    private void sequenceEnd() {
        if (pathNameIndex > 0) {
            pathName.remove(--pathNameIndex);
        }
        if (!seqIndexStack.isEmpty()) {
            seqIndex = seqIndexStack.pop();
        }
    }

    /**
     * 获取包含从最高级父类到当前属性的路径的属性名 如{a.b[0].c}。
     * @return String
     */
    private String getFullName() {
        EventType type = curEvent.getType();
        if (!(type == EventType.GET_ENTRY || type == EventType.GET_NAME)) {
            return null;
        }
        StringBuilder fullName = new StringBuilder();
        for (String str : pathName) {
            if (Define.BRACKETS_NUMBER.matcher(str).matches()) {
                fullName.deleteCharAt(fullName.length() - 1);
            }
            fullName.append(str).append(".");
        }
        if (type == EventType.GET_NAME) {
            fullName.append(((NameEvent)curEvent).getName());
        } else {
            fullName.deleteCharAt(fullName.length() - 1);
            fullName.append("[").append(seqIndex).append("]");
        }
        return fullName.toString();
    }

    /**
     * 处理节点 GET_NAME 和 GET_ENTRY，将名称或下标添加至路径中。
     */
    private void handleNameAndEntry() {
        if (curEvent.getType() == EventType.GET_NAME) {
            pathName.add(((NameEvent)curEvent).getName());
        }
        if (curEvent.getType() == EventType.GET_ENTRY) {
            pathName.add("[" + seqIndex + "]");
        }
    }

    public void setModify(HashMap modifyMap) {
        this.modifyMap = modifyMap;
        this.modifiedEventName = EMPTY;
        this.value = null;
    }

    public void setModify(String modifiedEventName, String value) {
        this.modifyMap = null;
        this.modifiedEventName = modifiedEventName;
        this.value = value;
    }

    private void init() {
        seqIndex = -1;
        pathNameIndex = 0;
        pathName.clear();
        seqIndexStack.clear();
        curEvent = null;
        needToModify = false;
    }

    public class EventOperatorException extends RuntimeException {
        public EventOperatorException (String msg, Throwable cause) {
            super(msg, cause);
        }
        public EventOperatorException (String msg) {
            super(msg, null);
        }
    }
}


package com.vaxtomis.yaml4test.Parser;

import com.vaxtomis.yaml4test.Tokenizer.Define;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * @description
 * 遍历 EventList，并对符合条件的 Event 进行修改。
 * 需要传入需要修改的属性名和值 (HashMap)。
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
    private String modifiedEventName = "";
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
     */
    public void rebuild() {
        if (modifyMap != null) {
            for(Event event : events) {
                batchModify(event);
            }
        } else {
            for(Event event : events) {
                if (singleModify(event)) {
                    return;
                }
            }
        }
    }

    /**
     * 切分 Events，只取参与修改的部分，降低运行时开销。
     *
     * @param modifiedEventNames
     */
    public LinkedList<Event> getCutEvents(ArrayList<String> modifiedEventNames) {
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
                        cutEvents.addAll(events.subList(start, end));
                        isTargetEvents = false;
                    }
                    break;
                case SEQUENCE_END:
                    sequenceEnd();
                    deep--;
                    if (isTargetEvents && deep == 1) {
                        cutEvents.addAll(events.subList(start, end));
                        isTargetEvents = false;
                    }
                    break;
                case SEQUENCE_START:
                    sequenceStart();
                    if (deep == 1) {
                        start = end;
                    }
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
            }
            end++;
        }
        cutEvents.add(Event.MAPPING_END);
        return cutEvents;
    }

    private void batchModify(Event event) {
        String fullName;
        switch (event.getType()) {
            case GET_NAME:
                curEvent = event;
                fullName = getFullName();
                System.out.println(fullName);
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
                System.out.println(fullName);
                if (modifyMap.containsKey(fullName)) {
                    needToModify = true;
                    modifiedEventName = fullName;
                }
                break;
            case GET_VALUE:
                if (needToModify) {
                    ((ValueEvent)event).setValue(modifyMap.get(modifiedEventName));
                    needToModify = false;
                }
        }
    }

    private boolean singleModify(Event event) {
        String fullName;
        switch (event.getType()) {
            case GET_NAME:
                curEvent = event;
                fullName = getFullName();
                //System.out.println(fullName);
                if (modifiedEventName.equals(fullName)) {
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
                if (modifiedEventName.equals(fullName)) {
                    needToModify = true;
                }
                break;
            case GET_VALUE:
                if (needToModify) {
                    ((ValueEvent)event).setValue(value);
                    return true;
                }
        }
        return false;
    }

    private void mappingStart() {
        if (curEvent != null) {
            handleNameAndEntry();
            pathNameIndex++;
        }
    }

    private void mappingEnd() {
        if (pathNameIndex > 0) {
            pathName.remove(--pathNameIndex);
        }
    }

    private void sequenceStart() {
        handleNameAndEntry();
        pathNameIndex++;
        if (seqIndex > -1) {
            seqIndexStack.push(seqIndex);
        }
        seqIndex = -1;
    }

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
            if (Define.BRACKETS.matcher(str).matches()) {
                fullName.deleteCharAt(fullName.length()-1);
            }
            fullName.append(str).append(".");
        }
        if (type == EventType.GET_NAME) {
            fullName.append(((NameEvent)curEvent).getName());
        } else {
            fullName.deleteCharAt(fullName.length()-1);
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
        } else if (curEvent.getType() == EventType.GET_ENTRY) {
            pathName.add("[" + seqIndex + "]");
        }
    }
}

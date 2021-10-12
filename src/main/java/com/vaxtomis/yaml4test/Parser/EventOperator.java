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
                if (curEvent != null) {
                    handleNameAndEntry();
                    pathNameIndex++;
                }
                break;
            case MAPPING_END:
                if (pathNameIndex > 0) {
                    pathName.remove(--pathNameIndex);
                }
                break;
            case SEQUENCE_END:
                if (pathNameIndex > 0) {
                    pathName.remove(--pathNameIndex);
                }
                if (!seqIndexStack.isEmpty()) {
                    seqIndex = seqIndexStack.pop();
                }
                break;
            case SEQUENCE_START:
                handleNameAndEntry();
                pathNameIndex++;
                if (seqIndex > -1) {
                    seqIndexStack.push(seqIndex);
                }
                seqIndex = -1;
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
                if (curEvent != null) {
                    handleNameAndEntry();
                    pathNameIndex++;
                }
                break;
            case MAPPING_END:
                if (pathNameIndex > 0) {
                    pathName.remove(--pathNameIndex);
                }
                break;
            case SEQUENCE_END:
                if (pathNameIndex > 0) {
                    pathName.remove(--pathNameIndex);
                }
                if (!seqIndexStack.isEmpty()) {
                    seqIndex = seqIndexStack.pop();
                }
                break;
            case SEQUENCE_START:
                handleNameAndEntry();
                pathNameIndex++;
                if (seqIndex > -1) {
                    seqIndexStack.push(seqIndex);
                }
                seqIndex = -1;
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

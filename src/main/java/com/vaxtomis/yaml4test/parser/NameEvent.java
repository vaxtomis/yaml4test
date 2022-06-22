package com.vaxtomis.yaml4test.parser;

/**
 * <p>
 * Indicator for get name event.<br>
 * 获取键值对中的键的事件标志。
 * </p>
 */
public class NameEvent extends Event {
    private String name;
    public NameEvent(String name) {
        super(EventType.GET_NAME);
        this.name = name;
    }

    @Override
    public String toString() {
        return super.toString() + "{Name: " + name + "}" ;
    }

    public String getName() {
        return name;
    }
}
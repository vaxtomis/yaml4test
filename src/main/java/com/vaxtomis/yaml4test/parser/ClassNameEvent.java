package com.vaxtomis.yaml4test.parser;

/**
 * <p>
 * Indicator for get class name event.<br>
 * 获取到类名的标志，用于触发对应的事件。
 * </p>
 */
public class ClassNameEvent extends Event {
    private String name;
    public ClassNameEvent(String name) {
        super(EventType.GET_CLASSNAME);
        this.name = name;
    }

    @Override
    public String toString() {
        return super.toString() + "{className: " + name + "}" ;
    }

    public String getName() {
        return name;
    }
}

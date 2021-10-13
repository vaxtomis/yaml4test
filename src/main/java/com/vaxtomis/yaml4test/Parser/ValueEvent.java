package com.vaxtomis.yaml4test.Parser;

/**
 * @description Indicator for get value event.
 * 获取键值对中的值的事件标志。
 */
public class ValueEvent extends Event {
    private String style;
    private String value;

    public ValueEvent(char style, String value) {
        super(EventType.GET_VALUE);
        if (style == '"') {
            this.style = "String";
        } else {
            this.style = "char";
        }
        this.value = value;
    }
    @Override
    public String toString() {
        return super.toString() + "{Value: " + value + "} {Style: " + style + "}" ;
    }

    public String getValue() {
        return value;
    }

    public String getStyle() {
        return style;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

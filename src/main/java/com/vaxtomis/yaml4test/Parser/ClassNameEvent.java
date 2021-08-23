package com.vaxtomis.yaml4test.Parser;

/**
 * @description Indicator for get class name event.
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

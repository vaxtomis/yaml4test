package com.vaxtomis.yaml4test.parser;

/**
 * @description Indicator for get Entry, include class and value.
 * 条目标志，说明要进入集合，类和值都可以放入集合。
 */
public class EntryEvent extends Event {
    private String entryType;
    private String value;
    public EntryEvent(String entryType, String value) {
        super(EventType.GET_ENTRY);
        this.entryType = entryType;
        this.value = value;
    }

    @Override
    public String toString() {
        return super.toString() + "{EntryType: " + entryType + "} {Value: " + value + "}" ;
    }

    public String getEntryType() {
        return entryType;
    }

    public String getValue() {
        return value;
    }
}

package com.vaxtomis.yaml4test.Parser;

/**
 * @description Event.
 */
public class Event {
    private EventType type;
    final static Event GET_ENTRY = new Event(EventType.GET_ENTRY);
    final static Event SEQUENCE_START = new Event(EventType.SEQUENCE_START);
    final static Event SEQUENCE_END = new Event(EventType.SEQUENCE_END);
    final static Event MAPPING_START = new Event(EventType.MAPPING_START);
    final static Event MAPPING_END = new Event(EventType.MAPPING_END);

    public Event(EventType type) {
        this.type = type;
    }

    public EventType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "<" + type.toString() + ">";
    }
}

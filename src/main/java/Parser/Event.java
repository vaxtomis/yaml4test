package Parser;

/**
 * @description
 *
 */
public class Event {
    private EventType type;
    final static Event CREATE_OBJECT = new Event(EventType.CREATE_OBJECT);
    final static Event SEQUENCE_START = new Event(EventType.SEQUENCE_START);
    final static Event SEQUENCE_END = new Event(EventType.SEQUENCE_END);
    final static Event MAPPING_START = new Event(EventType.MAPPING_START);
    final static Event MAPPING_END = new Event(EventType.MAPPING_END);

    public Event(EventType type) {
        this.type = type;
    }

}
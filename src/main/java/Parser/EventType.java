package Parser;

/**
 * @description Event Type num.
 */
public enum EventType {
    GET_NAME,
    GET_CLASSNAME,
    GET_VALUE,
    GET_ENTRY,
    SEQUENCE_START,
    SEQUENCE_END,
    MAPPING_START,
    MAPPING_END;

    @Override
    public String toString() {
        return name().replace('_',' ');
    }
}

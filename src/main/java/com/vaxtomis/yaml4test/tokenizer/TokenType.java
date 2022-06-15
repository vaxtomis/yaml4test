package com.vaxtomis.yaml4test.tokenizer;

/**
 * <p>
 * Token Type Enum.
 * </p>
 */
public enum TokenType {
    KEY,
    VALUE,
    SCALAR,
    CLASSNAME,
    STREAM_START,
    STREAM_END,
    BLOCK_MAPPING_START,
    BLOCK_SEQUENCE_START,
    BLOCK_ENTRY,
    BLOCK_END;

    @Override
    public String toString() {
        return name().replace('_',' ');
    }
}

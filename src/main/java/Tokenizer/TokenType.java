package Tokenizer;

/**
 * @description: Tokenizer.Token Type Enum.
 */
public enum TokenType {
    KEY,
    VALUE,
    SCALAR,
    STREAM_START,
    STREAM_END,
    BLOCK_MAPPING_START,
    BLOCK_SEQUENCE_START,
    BLOCK_ENTRY,
    BLOCK_END;

    @Override
    public String toString() {
        return name().replace("_"," ");
    }
}

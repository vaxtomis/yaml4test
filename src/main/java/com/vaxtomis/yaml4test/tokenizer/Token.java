package com.vaxtomis.yaml4test.tokenizer;

/**
 * <p>
 * Token.
 * </p>
 */
public class Token {
    private TokenType type;
    final static Token KEY = new Token(TokenType.KEY);
    final static Token VALUE = new Token(TokenType.VALUE);
    final static Token STREAM_START = new Token(TokenType.STREAM_START);
    final static Token STREAM_END = new Token(TokenType.STREAM_END);
    final static Token BLOCK_MAPPING_START = new Token(TokenType.BLOCK_MAPPING_START);
    final static Token BLOCK_SEQUENCE_START = new Token(TokenType.BLOCK_SEQUENCE_START);
    final static Token BLOCK_ENTRY = new Token(TokenType.BLOCK_ENTRY);
    final static Token BLOCK_END = new Token(TokenType.BLOCK_END);

    public Token(TokenType type) {
        this.type = type;
    }

    public TokenType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "<" + type.toString() + ">";
    }
}

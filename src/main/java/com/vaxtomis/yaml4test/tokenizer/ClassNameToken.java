package com.vaxtomis.yaml4test.tokenizer;

/**
 * @description Class name token.
 */
public class ClassNameToken extends Token {
    private String name;
    public ClassNameToken(String name) {
        super(TokenType.CLASSNAME);
        this.name = name;
    }

    @Override
    public String toString() {
        return super.toString() + "{className= " + name + "}";
    }

    public String getName() {
        return name;
    }

}

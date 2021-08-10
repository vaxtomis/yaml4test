package Tokenizer;

/**
 * @description Scalar Token.
 */
public class ScalarToken extends Token {
    private final char style;
    private final boolean plain;
    private final String value;

    public ScalarToken(char style, String value, boolean plain) {
        super(TokenType.SCALAR);
        this.style = style;
        this.value = value;
        this.plain = plain;
    }

    public ScalarToken(String value) {
        this((char)0, value,true);
    }

    public ScalarToken(String value,boolean plain) {
        this((char)0, value, plain);
    }

    @Override
    public String toString() {
        return super.toString() + "{value= " + value
                + "} {style= " + style
                + "} {plain= " + plain + "}";
    }
    public char getStyle() {
        return style;
    }

    public String getValue() {
        return value;
    }
}

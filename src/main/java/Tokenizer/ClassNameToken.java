package Tokenizer;

/**
 * @description:
 * @author: vaxtomis
 * @date: 2021/7/9
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

}

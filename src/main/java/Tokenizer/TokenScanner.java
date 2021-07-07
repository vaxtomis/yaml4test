package Tokenizer;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description:
 */
public class TokenScanner {
    private StreamBuffer bf;
    private int tkn;
    private int indent;
    private int depth;
    private boolean tkGetAble = true;
    private Stack<Integer> indents;


    public TokenScanner(String yaml) {
        bf = StreamBuffer.getInstance(yaml);
        tkn = 1;
        indent = 0;
        depth = 0;
    }

    /**
     * Find and forward to next token.
     * @return null
     */
    public void scanNextToken(){
        for(;;){
            while(bf.peek() == ' ')
                bf.forward();
            //Skip the annotation.
            if(bf.peek() == '#')
                while (Define.NULL_OR_LINEBREAK.indexOf(bf.peek()) == -1)
                    bf.forward();
            if(isLineBreak())
                if(depth == 0) tkGetAble = true;
                else break;
        }
    }

    /**
     * Scan the Plain.
     * @return ScalarToken
     */
    public Token scanPlain(){
        StringBuilder chunks = new StringBuilder();
        String spaces = "";
        boolean depthNotZero = true;
        //[\0 \t\r\n\u0085\\[\\]{},:?]
        Pattern rule = Define.R_FLOW_NOT_ZERO;
        if(depth == 0){
            depthNotZero = false;
            rule = Define.R_FLOW_ZERO;
        }
        while(bf.peek() != '#'){
            int length = 0;
            int chunkSize = 32;
            Matcher m = null;
            while (!(m = rule.matcher(bf.preSub(chunkSize))).find()) {
                chunkSize += 32;
            }
            //Get the index of the matched substring
            length = m.start();
            char ch = bf.peek(length);
             //If it is confirmed that it is a block,
             //find':' and the next character is not \0 \t\r\n\u0028[]{}
             //throws an error
            if (depthNotZero && ch == ':' && Define.S4.indexOf(bf.peek(length + 1)) == -1) {
                bf.forward(length);
                throw new TokenScanningException("Scanning a plain scalar, found unexpected ':'");
            }
            if(length == 0) break;
            tkGetAble = false;
            chunks.append(spaces);
            chunks.append(bf.preForward(length));
            spaces = scanPlainSpaces();
            if(spaces.length() == 0 || depth == 0 && bf.getCno() < indent + 1) break;
        }
        return new ScalarToken(chunks.toString(),true);
    }

    /**
     * Scan the spaces before plain.
     */
    public String scanPlainSpaces(){
        StringBuilder chunks = new StringBuilder();
        String spaces = bf.preForward(bf.spacesCount());
        char ch = bf.peek();
        if(Define.FULL_LINEBREAK.indexOf(ch) != -1){
            tkGetAble = true;
            //Determine if it is start/end or not.
            if (Define.END_OR_START.matcher(bf.preSub(4)).matches()) return "";
            StringBuilder breaks = new StringBuilder();

            while (Define.BLANK_OR_LINEBREAK.indexOf(bf.peek()) != -1)
                if (' ' == bf.peek())
                    bf.forward();
                else {
                    if (isLineBreak())  breaks.append("\n");
                    if (Define.END_OR_START.matcher(bf.preSub(4)).matches()) return "";
                }
            if (breaks.length() == 0) chunks.append(" ");
            chunks.append(breaks);
        } else
            chunks.append(spaces);
        return chunks.toString();
    }

    /**
     * Scan the linebreak.
     * @return boolean
     */
    public boolean isLineBreak(){
        char ch = bf.peek();
        if (Define.FULL_LINEBREAK.indexOf(ch) != -1) {
            if (Define.RN.equals(bf.preSub(2)))
                bf.forward(2);
            else
                bf.forward();
            return true;
        }
        return false;
    }

    /**
     * Throw exception while scanning tokens.
     */
    public class TokenScanningException extends RuntimeException {
        public TokenScanningException (String msg, Throwable cause){
            super("Line " + bf.getLno() + ",Column "+ bf.getCno()
            + ": "+ msg,cause);
        }
        public TokenScanningException (String msg){
            super(msg,null);
        }
    }
}

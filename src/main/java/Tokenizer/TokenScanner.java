package Tokenizer;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description:
 */
public class TokenScanner {
    private StreamBuffer bf;
    private int indent = -1;
    private int depth = 0;
    private boolean tkGetAble = true;
    private final Stack<Integer> indents = new Stack<>();


    public TokenScanner(String yaml) {
        bf = StreamBuffer.getInstance(yaml);
    }

    /**
     * Find and forward to next token.
     * @return null
     */
    public void scanNextToken() {
        for(;;){
            while(bf.peek() == ' ')
                bf.forward();
            //Skip the annotation.
            if(bf.peek() == '#')
                while (Define.NULL_OR_LINEBREAK.indexOf(bf.peek()) == -1)
                    bf.forward();
            if(isLineBreak()){
                if(depth == 0) tkGetAble = true;
            } else break;
        }
    }

    /**
     * Scan the Plain.
     * @return ScalarToken
     */
    public Token scanPlain() {
        StringBuilder chunks = new StringBuilder();
        String spaces = "";
        boolean isDepthZero = false;
        //[\0 \t\r\n\u0085\\[\\]{},:?]
        Pattern rule = Define.R_FLOW_NOT_ZERO;
        if(depth == 0){
            isDepthZero = true;
            //[\0 \t\r\n\u0085]|(:[\0 \t\r\n\u0085])
            rule = Define.R_FLOW_ZERO;
        }
        while(bf.peek() != '#'){
            int length = 0;
            int chunkSize = 32;
            Matcher m = null;
            while (!(m = rule.matcher(bf.preSub(chunkSize))).find()) {
                chunkSize += 32;
            }
            //Get the start index of the matched substring
            length = m.start();
            char ch = bf.peek(length);
            //If it is confirmed that it is a block,
            //find ':' and the next character is not \0 \t\r\n\u0028[]{}
            //throws an error
            if (!isDepthZero && ch == ':' && Define.S4.indexOf(bf.peek(length + 1)) == -1) {
                bf.forward(length);
                throw new TokenScanningException("Scanning a plain scalar," +
                        " found unexpected ':'");
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
     * Scan for spaces, including possible line breaks.
     * @return String
     * @Examples:
     * "ab" return " "
     * "  ab" return "  "
     * "
     *      ab" return "\n"
     * "
     * ab" return "\n"
     */
    public String scanPlainSpaces() {
        StringBuilder chunks = new StringBuilder();
        String spaces = bf.preForward(bf.spacesCount());
        char ch = bf.peek();
        if(Define.FULL_LINEBREAK.indexOf(ch) != -1){
            tkGetAble = true;
            //Determine if it is start/end or not.
            if(Define.END_OR_START.matcher(bf.preSub(4)).matches()) return "";
            StringBuilder breaks = new StringBuilder();
            //Count the number of linebreaks.
            while(Define.BLANK_OR_LINEBREAK.indexOf(bf.peek()) != -1)
                if(' ' == bf.peek())
                    bf.forward();
                else {
                    if(isLineBreak())  breaks.append("\n");
                    if(Define.END_OR_START.matcher(bf.preSub(4)).matches())
                        return "";
                }
            if(breaks.length() == 0) chunks.append(" ");
            chunks.append(breaks);
        } else
            chunks.append(spaces);
        return chunks.toString();
    }

    /**
     * @param style
     * @return Token
     */
    public Token scanFlowScalar(char style) {
        boolean dbl = style == '"';
        StringBuilder chunks = new StringBuilder();
        char quote = bf.peek();
        bf.forward();
        chunks.append(scanFlowScalarNonSpaces(dbl));
        while (bf.peek() != quote) {
            chunks.append(scanFlowScalarSpaces());
            chunks.append(scanFlowScalarNonSpaces(dbl));
        }
        bf.forward();
        return new ScalarToken(style, chunks.toString(), false);
    }

    /**
     * @param dbl
     * @return String
     */
    private String scanFlowScalarNonSpaces(boolean dbl) {
        StringBuilder chunks = new StringBuilder();
        for (;;) {
            int length = 0;
            //Not '\"\\\0 \t\r\n\u0085
            while(Define.SPACES_AND_STUFF.indexOf(bf.peek(length)) == -1)
                length++;
            if(length != 0) chunks.append(bf.preForward(length));
            char ch = bf.peek();
            //Like 'abc''
            if(!dbl && ch == '\'' && bf.peek(1) == '\'') {
                chunks.append("'");
                bf.forward(2);
                //Like "abc' or 'abc" or 'abc\
            } else if(dbl && ch == '\'' || !dbl && Define.DOUBLE_ESC.indexOf(ch) != -1) {
                chunks.append(ch);
                bf.forward();
                //Like "abc\
            } else if(dbl && ch == '\\') {
                bf.forward();
                //Get next char
                ch = bf.peek();
                if(Define.ESCAPE_REPLACEMENTS.containsKey(ch)) {
                    // 'ch' to "ch"
                    chunks.append(Define.ESCAPE_REPLACEMENTS.get(ch));
                    bf.forward();
                    /**
                     * 'x' -> 2   Hex, followed by 2 hex digits
                     * 'u' -> 4   Hex, followed by 4 hex digits
                     * 'U' -> 8
                     */
                } else if(Define.ESCAPE_CODES.containsKey(ch)) {
                    length = Define.ESCAPE_CODES.get(ch);
                    bf.forward();
                    String val = bf.preSub(length);
                    if(Define.NOT_HEX.matcher(val).find()){
                        throw new TokenScanningException("Scanning a double quoted scalar" +
                                ", expected an escape sequence of" + length +
                                " hexadecimal numbers but found: " + bf.ch(bf.peek()));
                    }
                    chunks.append(Character.toChars(Integer.parseInt(val, 16)));
                    bf.forward(length);
                } else if(Define.FULL_LINEBREAK.indexOf(ch) != -1) {
                    isLineBreak();
                    chunks.append(scanFlowScalarBreaks());
                }
            } else
                return chunks.toString();
        }
    }

    /**
     * 1.Scan the '\0' (End of stream).
     * 2.Find line breaks.
     * @return String
     */
    private String scanFlowScalarSpaces() {
        StringBuilder chunks = new StringBuilder();
        String spaces = bf.preForward(bf.spacesCount());
        // forward(length);
        char ch = bf.peek();
        if (ch == '\0'){
            throw new TokenScanningException("Scanning a quoted scalar," +
                    " found unexpected end of stream.");
        }
        else if (Define.FULL_LINEBREAK.indexOf(ch) != -1) {
            String breaks = scanFlowScalarBreaks();
            isLineBreak();
            if (breaks.length() == 0) chunks.append(" ");
            chunks.append(breaks);
        } else
            chunks.append(spaces);
        return chunks.toString();
    }

    /**
     * 1.Scan the document separator.
     * 2.Skip blank and tab.
     * 3.Find linebreak and append "\n".
     * @return String
     */
    private String scanFlowScalarBreaks() {
        StringBuilder chunks = new StringBuilder();
        String pre = null;
        for (;;) {
            pre = bf.preSub(3);
            if ((pre.equals("---") || pre.equals("...")) &&
                    Define.NULL_BLANK_T_LINEBREAK.indexOf(bf.peek(3)) != -1) {
                throw new TokenScanningException("Scanning a quoted scalar," +
                        " found unexpected document separator.");
            }
            while(Define.BLANK_T.indexOf(bf.peek()) != -1)
                bf.forward();
            if (Define.FULL_LINEBREAK.indexOf(bf.peek()) != -1)
                if(isLineBreak()) chunks.append("\n");
            else
                return chunks.toString();
        }
    }

    /**
     * Scan the linebreak.
     * @return boolean
     * */
    public boolean isLineBreak() {
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


    public boolean addIndent() {
        return addIndent(bf.getCno());
    }

    /**
     * Increase the indentation.
     * If the indentation is less than the current position,
     * it means that the new block is opened, record the indentation position indents header,
     * and then set curMaxIndent to the current line number as the current maximum indentation
     * @param col
     * @return
     */
    public boolean addIndent(int col){
        if(indent < col) {
            indents.push(indent);
            indent = col;
            return true;
        }
        return false;
    }

    public int countCloseBlock() {
        return countCloseBlock(bf.getCno());
    }

    /**
     * Pass in {col} to count BLOCKs
     * whose indentation is greater than or equal to {col}
     * @param col
     * @return int
     */
    public int countCloseBlock(int col) {
        int count = 0;
        if(!isDepthZero()) return count;
        while(indent > col){
            indent = indents.pop();
            count++;
        }
        return count;
    }

    public char getChar() {
        return getChar(0);
    }

    public char getChar(int length) {
        return bf.peek(length);
    }

    public String getString(int length) {
        return bf.preSub(length);
    }

    public boolean isColZero() {
        return bf.getCno() == 0;
    }

    public boolean isDepthZero() {
        return depth == 0;
    }

    public boolean canGetKV(){
        return depth != 0 || Define.NULL_OR_OTHER.indexOf(bf.peek(1)) != -1;
    }

    public void setTkGetAble(boolean tkGetAble) {
        this.tkGetAble = tkGetAble;
    }

    public boolean isTkGetAble() {
        return tkGetAble;
    }

    public int getDepth() {
        return depth;
    }

    public void toNext() {
        bf.forward();
    }
    public int getCno() {
        return bf.getCno();
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

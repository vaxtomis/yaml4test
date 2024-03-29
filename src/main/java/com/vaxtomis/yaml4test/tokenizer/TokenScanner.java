package com.vaxtomis.yaml4test.tokenizer;

import com.vaxtomis.yaml4test.common.Define;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.vaxtomis.yaml4test.common.Define.EMPTY;

/**
 * <p>
 * TokenScanner<br>
 * Used to scan the stream and split out tokens.<br><br>
 * Reference Project: com.esotericsoftware.yamlbeans
 * (Copyright (c) 2008 Nathan Sweet, Copyright (c) 2006 Ola Bini)
 * </p>
 */
class TokenScanner implements TokenScan {
    private static TokenScanner tokenScanner = null;
    private StreamBuffer streamBuffer;
    private int indent;
    private int depth;
    private boolean ableToGetToken;
    private final Stack<Integer> indents;


    public TokenScanner(String yaml) {
        indent = -1;
        depth = 0;
        ableToGetToken = true;
        indents = new Stack<>();
        streamBuffer = StreamBuffer.getInstance(yaml);
    }

    public static TokenScanner getInstance(String yaml) {
        if (tokenScanner == null) {
            tokenScanner = new TokenScanner(yaml);
            return tokenScanner;
        }
        tokenScanner.reset(yaml);
        return tokenScanner;
    }

    private void reset(String yaml) {
        indent = -1;
        depth = 0;
        ableToGetToken = true;
        indents.clear();
        streamBuffer = StreamBuffer.getInstance(yaml);
    }

    /**
     * Find and forward to next token.
     * @return null
     */
    public void scanNextToken() {
        for (;;) {
            while (peekChar() == ' ') {
                toNext();
            }
            //Skip the annotation.
            if (peekChar() == '#') {
                while (Define.NULL_OR_LINEBREAK.indexOf(peekChar()) == -1) {
                    toNext();
                }
            }
            if (isLineBreak()) {
                if (depth == 0) {
                    ableToGetToken = true;
                }
            } else {
                break;
            }
        }
    }

    /**
     * Scan the Plain.
     * @return ScalarToken
     */
    public Token scanPlain() {
        StringBuilder chunks = new StringBuilder();
        String spaces = EMPTY;
        boolean isDepthZero = false;
        //[\0 \t\r\n\u0085\\[\\]{},:?]
        Pattern rule = Define.R_FLOW_NOT_ZERO;
        if (depth == 0) {
            isDepthZero = true;
            //[\0 \t\r\n\u0085]|(:[\0 \t\r\n\u0085])
            rule = Define.R_FLOW_ZERO;
        }
        while (peekChar() != '#') {
            int length = 0;
            int chunkSize = 32;
            Matcher m = null;
            while (!(m = rule.matcher(peekString(chunkSize))).find()) {
                chunkSize += 32;
            }
            //Get the start index of the matched substring
            length = m.start();
            char ch = peekChar(length);
            //If it is confirmed that it is a block,
            //find ':' and the next character is not \0 \t\r\n\u0028[]{}
            //throws an error
            if (!isDepthZero && ch == ':' && Define.S4.indexOf(peekChar(length + 1)) == -1) {
                toNext(length);
                throw new TokenScanningException("Scanning a plain scalar," +
                        " found unexpected ':'");
            }
            if (length == 0) {
                break;
            }
            ableToGetToken = false;
            chunks.append(spaces);
            chunks.append(getString(length));
            spaces = scanPlainSpaces();
            if (spaces.length() == 0 || depth == 0 && getCno() < indent + 1) {
                break;
            }
        }
        return new ScalarToken(chunks.toString(),true);
    }

    /**
     * Scan for spaces, including possible line breaks.
     * @return String
     * @Examples
     * "ab" return " "
     * "  ab" return "  "
     * "
     *      ab" return "\n"
     * "
     * ab" return "\n"
     */
    public String scanPlainSpaces() {
        StringBuilder chunks = new StringBuilder();
        String spaces = getString(streamBuffer.countSpaces());
        char ch = peekChar();
        if (Define.FULL_LINEBREAK.indexOf(ch) != -1) {
            ableToGetToken = true;
            //Determine if it is start/end or not.
            if (Define.END_OR_START.matcher(peekString(4)).matches()) {
                return EMPTY;
            }
            StringBuilder breaks = new StringBuilder();
            //Count the number of linebreaks.
            while (Define.BLANK_OR_LINEBREAK.indexOf(peekChar()) != -1) {
                if (' ' == peekChar()) {
                    toNext();
                } else {
                    if (isLineBreak()) {
                        breaks.append("\n");
                    }
                    if (Define.END_OR_START.matcher(peekString(4)).matches()) {
                        return EMPTY;
                    }
                }
            }
            if (breaks.length() == 0) {
                chunks.append(" ");
            }
            chunks.append(breaks);
        } else {
            chunks.append(spaces);
        }
        return chunks.toString();
    }

    /**
     * Scan The Scalar.
     * @param style Token style
     * @return Token
     */
    public Token scanFlowScalar(char style) {
        boolean dbl = style == '"';
        StringBuilder chunks = new StringBuilder();
        char quote = peekChar();
        toNext();
        chunks.append(scanFlowScalarNonSpaces(dbl));
        while (peekChar() != quote) {
            chunks.append(scanFlowScalarSpaces());
            chunks.append(scanFlowScalarNonSpaces(dbl));
        }
        toNext();
        return new ScalarToken(style, chunks.toString(), false);
    }

    /**
     * Scan scalar which does not contain spaces.
     * @param dbl
     * @return String
     */
    private String scanFlowScalarNonSpaces(boolean dbl) {
        StringBuilder chunks = new StringBuilder();
        for (;;) {
            int length = 0;
            //Not '\"\\\0 \t\r\n\u0085
            while (Define.SPACES_AND_STUFF.indexOf(peekChar(length)) == -1)
                length++;
            if (length != 0) chunks.append(getString(length));
            char ch = peekChar();
            //Like 'abc''
            if (!dbl && ch == '\'' && peekChar(1) == '\'') {
                chunks.append("'");
                toNext(2);
                //Like "abc' or 'abc" or 'abc\
            } else if (dbl && ch == '\'' || !dbl && Define.DOUBLE_ESC.indexOf(ch) != -1) {
                chunks.append(ch);
                toNext();
                //Like "abc\
            } else if (dbl && ch == '\\') {
                toNext();
                //Get next char
                ch = peekChar();
                if (Define.ESCAPE_REPLACEMENTS.containsKey(ch)) {
                    // 'ch' to "ch"
                    chunks.append(Define.ESCAPE_REPLACEMENTS.get(ch));
                    toNext();
                    /**
                     * 'x' -> 2   Hex, followed by 2 hex digits
                     * 'u' -> 4   Hex, followed by 4 hex digits
                     * 'U' -> 8
                     */
                } else if (Define.ESCAPE_CODES.containsKey(ch)) {
                    length = Define.ESCAPE_CODES.get(ch);
                    toNext();
                    String val = peekString(length);
                    if (Define.NOT_HEX.matcher(val).find()) {
                        throw new TokenScanningException("Scanning a double quoted scalar" +
                                ", expected an escape sequence of" + length +
                                " hexadecimal numbers but found: " + streamBuffer.ch(peekChar()));
                    }
                    chunks.append(Character.toChars(Integer.parseInt(val, 16)));
                    toNext(length);
                } else if (Define.FULL_LINEBREAK.indexOf(ch) != -1) {
                    isLineBreak();
                    chunks.append(scanFlowScalarBreaks());
                }
            } else
                return chunks.toString();
        }
    }

    /**
     * 1.Scan the '\0' (End of stream).<br>
     * 2.Find line breaks.
     * @return String
     */
    private String scanFlowScalarSpaces() {
        StringBuilder chunks = new StringBuilder();
        String spaces = getString(streamBuffer.countSpaces());
        // forward(length);
        char ch = peekChar();
        if (ch == '\0') {
            throw new TokenScanningException("Scanning a quoted scalar," +
                    " found unexpected end of stream.");
        }
        else if (Define.FULL_LINEBREAK.indexOf(ch) != -1) {
            String breaks = scanFlowScalarBreaks();
            isLineBreak();
            if (breaks.length() == 0) {
                chunks.append(" ");
            }
            chunks.append(breaks);
        } else {
            chunks.append(spaces);
        }
        return chunks.toString();
    }

    /**
     * 1.Scan the document separator.<br>
     * 2.Skip blank and tab.<br>
     * 3.Find linebreak and append "\n".
     * @return String
     */
    private String scanFlowScalarBreaks() {
        StringBuilder chunks = new StringBuilder();
        String pre = null;
        for (;;) {
            pre = peekString(3);
            if (("---".equals(pre) || "...".equals(pre)) &&
                    Define.NULL_BLANK_T_LINEBREAK.indexOf(peekChar(3)) != -1) {
                throw new TokenScanningException("Scanning a quoted scalar," +
                        " found unexpected document separator.");
            }
            while (Define.BLANK_T.indexOf(peekChar()) != -1) {
                toNext();
            }
            if (Define.FULL_LINEBREAK.indexOf(peekChar()) != -1) {
                if (isLineBreak()) {
                    chunks.append("\n");
                }
            } else {
                return chunks.toString();
            }
        }
    }

    /**
     * Scan the Class Name like {xxx.xxx.xxx}
     * @return Token
     */
    public Token scanClassName() {
        int length = 0;
        char ch = peekChar(length++);
        if (Define.FULL_LINEBREAK.indexOf(ch) != -1 || ch == '.') {
            throw new TokenScanningException("Scanning class name but find linebreaks and '.' .");
        }
        StringBuilder chunks = new StringBuilder();
        boolean doubleDot = false;
        while (Define.ALPHA.indexOf(ch) != -1 || ch == '.') {
            if (ch == '.' && doubleDot) {
                throw new TokenScanningException("Scanning class name but find wrong format.");
            }
            doubleDot = ch == '.';
            chunks.append(ch);
            ch = peekChar(length++);
        }
        toNext(length);
        return new ClassNameToken(chunks.toString());
    }

    /**
     * Scan the linebreak.
     * @return boolean
     */
    public boolean isLineBreak() {
        char ch = peekChar();
        if (Define.FULL_LINEBREAK.indexOf(ch) != -1) {
            if (Define.RN.equals(peekString(2))) {
                toNext(2);
            } else {
                toNext();
            }
            return true;
        }
        return false;
    }


    public boolean addIndent() {
        return addIndent(getCno());
    }

    /**
     * Increase the indentation.<br>
     * If the indentation is less than the current position
     * it means that the new block is opened, record the indentation position indents header
     * then set curMaxIndent to the current line number as the current maximum indentation.
     * <br><br>
     * 增加缩进。<br>
     * 如果缩进小于当前位置表示新区块开启，
     * 记录缩进位置 indents header，
     * 然后设置 curMaxIndent 为当前行号作为当前最大缩进。
     *
     * @param col col
     * @return boolean
     */
    public boolean addIndent(int col){
        if (indent < col) {
            indents.push(indent);
            indent = col;
            return true;
        }
        return false;
    }

    public int countCloseBlock() {
        return countCloseBlock(getCno());
    }

    /**
     * Pass in {col} to count BLOCKs
     * whose indentation is greater than or equal to {col}.
     * <br>
     * 传入 {col} 以计算缩进大于或等于 {col} 的区块。
     * @param col col
     * @return int
     */
    public int countCloseBlock(int col) {
        int count = 0;
        if (!isDepthZero()) return count;
        while (indent > col) {
            indent = indents.pop();
            count++;
        }
        return count;
    }

    public char peekChar() {
        return streamBuffer.peek(0);
    }

    public char peekChar(int length) {
        return streamBuffer.peek(length);
    }

    public String peekString(int length) {
        return streamBuffer.preSubstring(length);
    }

    public boolean isColZero() {
        return streamBuffer.getColumnNumber() == 0;
    }

    public boolean isDepthZero() {
        return depth == 0;
    }

    public boolean ableToGetKeyValue() {
        return depth != 0 || Define.NULL_OR_OTHER.indexOf(peekChar(1)) != -1;
    }

    public void setAbleToGetToken(boolean ableToGetToken) {
        this.ableToGetToken = ableToGetToken;
    }

    public boolean isAbleToGetToken() {
        return ableToGetToken;
    }

    public int getDepth() {
        return depth;
    }

    public void toNext(int length) {
        streamBuffer.forward(length);
    }

    public void toNext() {
        streamBuffer.forward(1);
    }

    public String getString(int length) {
        return streamBuffer.preForward(length);
    }

    public int getCno() {
        return streamBuffer.getColumnNumber();
    }

    public int getLno() {
        return streamBuffer.getLineNumber();
    }

    /**
     * Throw exception while scanning tokens.
     */
    public class TokenScanningException extends RuntimeException {
        public TokenScanningException (String msg, Throwable cause){
            super("Line " + getLno() + ",Column "+ getCno()
                    + ": "+ msg,cause);
        }
        public TokenScanningException (String msg) {
            super(msg,null);
        }
    }

}

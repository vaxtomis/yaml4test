package com.vaxtomis.yaml4test.parser;

import com.vaxtomis.yaml4test.common.Define;
import com.vaxtomis.yaml4test.tokenizer.*;
import com.vaxtomis.yaml4test.YamlFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedList;

import static com.vaxtomis.yaml4test.common.Define.*;
import static com.vaxtomis.yaml4test.tokenizer.TokenType.*;

/**
 * <p>
 * Parser.
 * <p/>
 * @author vaxtomis
 */
public class Parser {
    private Tokenizer tokenizer;
    private String path;
    private final Parser.SlidingWindow window;
    private final LinkedList<Event> events;
    private final LinkedList<Token> blockStack;
    private boolean flagTokensEnd = false;

    public Parser() {
        tokenizer = null;
        path = null;
        window = new SlidingWindow();
        events = new LinkedList<>();
        blockStack = new LinkedList<Token>();
    }

    public void reset() {
        tokenizer = null;
        path = null;
        window.cursor = -1;
        events.clear();
        blockStack.clear();
        flagTokensEnd = false;
    }

    /**
     * Sliding Window. Used to store recently used tokens.<br>
     * 滑动窗口，用来缓存最近经过的 Token。
     */
    class SlidingWindow {
        private int cursor = -1;
        private Token[] tks = new Token[3];
        public void forward() {
            if (flagTokensEnd) {
                return;
            }
            Token tk = tokenizer.getNextToken();
            if (tk != null) {
                if (cursor < 2) {
                    tks[++cursor] = tk;
                } else {
                    tks[0] = tks[1];
                    tks[1] = tks[2];
                    tks[2] = tk;
                }
            } else {
                flagTokensEnd = true;
            }
        }

        public Token getCurrent() {
            if (cursor == -1) {
                return null;
            }
            return tks[cursor];
        }

        public Token getPrevious() {
            if (cursor + PREVIOUS < 0) {
                return null;
            }
            return tks[cursor + PREVIOUS];
        }

        public Token getDoublePrevious() {
            if (cursor + DOUBLE_PREVIOUS < 0) {
                return null;
            }
            return tks[cursor + DOUBLE_PREVIOUS];
        }

        /**
         * Get the [cur, pre, pre-pre] type of Token in sliding window.<br>
         * 从滑动窗口中获取当前，上一个，上上个 Token。
         */
        public TokenType getTokenType(int type) {
            Token token = null;
            switch (type) {
                case DOUBLE_PREVIOUS:
                    token = getDoublePrevious();
                    break;
                case PREVIOUS:
                    token = getPrevious();
                    break;
                case CURRENT:
                    token = getCurrent();
                    break;
                default:

            }
            if (token == null) {
                return null;
            }
            return token.getType();
        }
    }

    /**
     * Traverse the Token to determine the correctness of the syntax,
     * and obtain a set of Events.<br>
     * 遍历 Token 判断语法的正确性，得到一组 Events。
     */
    private void loopProcessing() {
        if (path == null) {
            throw new ParserException("The path of file cannot be null.");
        }
        try {
            this.path = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        tokenizer = new Tokenizer(path);
        while (!flagTokensEnd) {
            window.forward();
            fetchEvent();
        }
    }

    /**
     * ConstraintMap defines Token grammar rules<br>
     * Generates corresponding Event arrangement according to the grammar rules.<br>
     * 限制路径图定义了 Token 的语法规则，根据语法规则生成对应的 Event 序列。<br>
     */
    private void fetchEvent() {
        if (!ConstraintsMap.isAllowed(window)) {
            throw new ParserException("Found a Token where it is not allowed."
                    + " Token: " + window.getCurrent().toString());
        }
        Token tk = window.getCurrent();
        switch (tk.getType()) {
            case SCALAR:
                handleScalar((ScalarToken) tk);
                break;
            case CLASSNAME:
                handleClassName((ClassNameToken) tk);
                break;
            case BLOCK_END:
                handleBlockEnd();
                break;
            case BLOCK_MAPPING_START:
                handleMappingStart();
                break;
            case BLOCK_SEQUENCE_START:
                handleSequenceStart();
                break;
            default:

        }
    }
    private void handleScalar(ScalarToken tk) {
        Token pre = window.getPrevious();
        switch (pre.getType()) {
            case KEY:
                events.add(new NameEvent(tk.getValue()));
                break;
            case VALUE:
                events.add(new ValueEvent(tk.getStyle(), tk.getValue()));
                break;
            case BLOCK_ENTRY:
                events.add(new EntryEvent(Define.VALUE, tk.getValue()));
                break;
            default:

        }
    }

    private void handleClassName(ClassNameToken tk) {
        Token pre = window.getPrevious();
        if (pre.getType() == BLOCK_ENTRY) {
            events.add(new EntryEvent(CLASS, tk.getName()));
        } else {
            events.add(new ClassNameEvent(tk.getName()));
        }
    }

    private void handleBlockEnd() {
        if (blockStack.pop().getType() == BLOCK_MAPPING_START) {
            events.add(Event.MAPPING_END);
        } else {
            events.add(Event.SEQUENCE_END);
        }
    }

    private void handleMappingStart() {
        events.add(Event.MAPPING_START);
        blockStack.push(window.getCurrent());
    }

    private void handleSequenceStart() {
        events.add(Event.SEQUENCE_START);
        blockStack.push(window.getCurrent());
    }

    public LinkedList<Event> getEventList() {
        loopProcessing();
        return events;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public class ParserException extends RuntimeException {
        public ParserException (String msg, Throwable cause) {
            super(msg, cause);
        }
        public ParserException (String msg) {
            super(msg, null);
        }
    }

    public static void main(String[] args) {
        Parser parser = new Parser();
        String path = YamlFactory.class.getClassLoader().getResource("").getPath()+"test3.yml";
        //System.out.println(path);
        parser.setPath(path);
        for(Event e : parser.getEventList()) {
            System.out.println(e.toString());
        }
    }
}

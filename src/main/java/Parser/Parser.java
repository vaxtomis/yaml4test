package Parser;

import Tokenizer.*;
import java.util.LinkedList;
import static Tokenizer.TokenType.*;

/**
 * @description Parser.
 * @author vaxtomis
 */
public class Parser {
    private Tokenizer tokenizer;
    private Parser.SlidingWindow sw = new SlidingWindow();
    private LinkedList<Event> events = new LinkedList<>();
    private LinkedList<Token> blockStack = new LinkedList<Token>();
    private boolean flagTokensEnd = false;

    public Parser(String yaml) {
        this.tokenizer = new Tokenizer(yaml);
    }

    /**
     * @description Sliding Window. Used to store recently used tokens.
     */
    class SlidingWindow {
        private int cursor = -1;
        private Token[] tks = new Token[3];
        public void forward() {
            if (flagTokensEnd) return;
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

        public Token getCur() {
            if (cursor == -1) return null;
            return tks[cursor];
        }

        public Token getPre() {
            if (cursor-1 < 0) return null;
            return tks[cursor-1];
        }

        public Token getDoublePre() {
            if (cursor-2 < 0) return null;
            return tks[cursor-2];
        }

        public int getCursor() {
            return cursor;
        }

        public TokenType getTokenType(String type) {
            Token tk = null;
            switch (type) {
                case "DPre":
                    tk = getDoublePre();
                    break;
                case "Pre":
                    tk = getPre();
                    break;
                case "Cur":
                    tk = getCur();
                    break;
            }
            if (tk == null) {
                return null;
            } else {
                return tk.getType();
            }
        }
    }

    public void loopProcessing() {
        while (!flagTokensEnd) {
            sw.forward();
            fetchEvent();
        }
    }

    private void fetchEvent() {
        if (!ConstraintsMap.isAllowed(sw)) {
            throw new ParserException("Found a Token where it is not allowed."
                    + " Token: " + sw.getCur().toString());
        }
        Token tk = sw.getCur();
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
        }
    }
    private void handleScalar(ScalarToken tk) {
        Token pre = sw.getPre();
        if (pre.getType() == KEY) {
            events.add(new NameEvent(tk.getValue()));
        }
        else if (pre.getType() == VALUE) {
            events.add(new ValueEvent(tk.getStyle(), tk.getValue()));
        }
        else if (pre.getType() == BLOCK_ENTRY) {
            events.add(Event.GET_ENTRY);
        }
    }

    private void handleClassName(ClassNameToken tk) {
        events.add(new ClassNameEvent(tk.getName()));
        events.add(Event.CREATE_OBJECT);
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
        blockStack.push(sw.getCur());
    }

    private void handleSequenceStart() {
        events.add(Event.SEQUENCE_START);
        blockStack.push(sw.getCur());
    }

    public LinkedList<Event> getEventQueue() {
        return events;
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
        Parser parser = new Parser("test/test.yml");
        parser.loopProcessing();
        for(Event e : parser.getEventQueue()) {
            System.out.println(e.toString());
        }
    }
}

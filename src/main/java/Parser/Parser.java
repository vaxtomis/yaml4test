package Parser;


import Tokenizer.Tokenizer;
import Tokenizer.Token;

/**
 * @description
 * @author vaxtomis
 */
public class Parser {
    private Tokenizer tokenizer;
    private Parser.SlidingWindow slidingWindow;
    private EventQueue queue;
    private boolean flagTokensEnd = false;

    public Parser(String yaml) {
        this.tokenizer = new Tokenizer(yaml);
        this.slidingWindow = new SlidingWindow();
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
    }


    public static void main(String[] args) {

    }
}

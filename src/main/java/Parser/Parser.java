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
     * @description: Sliding Window. Used to store recently used tokens.
     */
    class SlidingWindow {
        private int count = 0;
        private Token[] tks = new Token[3];
        public void forward() {
            if (flagTokensEnd) return;
            Token tk = tokenizer.getNextToken();
            if (tk != null) {
                if (count < 3) {
                    tks[count++] = tk;
                } else {
                    tks[0] = tks[1];
                    tks[1] = tks[2];
                    tks[2] = tk;
                }
            } else {
                flagTokensEnd = true;
            }
        }

        public boolean fillTheWindow() {
            while (count < 3) {
                forward();
            }
            return !flagTokensEnd;
        }

        public Token getCurToken() {
            if (count == 0) return null;
            if (count < 3) {
                return tks[tks.length-1];
            } else {
                return tks[2];
            }
        }

        public Token getFirstToken() {
            if (count == 0) return null;
            return tks[0];
        }

        public Token getSecondToken() {
            if (count < 2) return null;
            return tks[1];
        }
    }


    public static void main(String[] args) {

    }
}

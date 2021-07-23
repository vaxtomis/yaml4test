package Parser;


import Tokenizer.Tokenizer;
import Tokenizer.Token;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;

/**
 * @description:
 * @author: vaxtomis
 * @date: 2021/7/23
 */
public class Parser {
    private Tokenizer tokenizer;
    private SlidingWin slidingWin;
    private boolean flagTokensEnd = false;

    public Parser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    /**
     *
     */
    class SlidingWin {
        private int count = 0;
        private Token[] tks = new Token[3];

        public void forward() {
            if(flagTokensEnd) return;
            Token tk = tokenizer.getNextToken();
            if(tk != null) {
                if(count < 3) {
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
    }

    public static void main(String[] args) {
    }
}

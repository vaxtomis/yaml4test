package com.vaxtomis.yaml4test.Tokenizer;

import com.vaxtomis.yaml4test.YamlFactory;

import java.util.*;

/**
 * @description Tokenizer.
 * @note -> Reference Project: com.esotericsoftware.yamlbeans
 * (Copyright (c) 2008 Nathan Sweet, Copyright (c) 2006 Ola Bini)
 */
public class Tokenizer {
    private TokenScanner ts;
    private int taken = 0;
    private boolean flagStreamStart = false;
    private boolean flagStreamEnd = false;
    private final List<Token> tokens = new LinkedList<Token>();
    private final Map<Integer,TokenKey> tokenKeyMap = new HashMap<>();

    static class TokenKey {
        public final int tkn;
        public final int cno;
        TokenKey(int tkn, int cno) {
            this.tkn = tkn;
            this.cno = cno;
        }
    }

    public Tokenizer(TokenScanner ts) {
        this.ts = ts;
        fetchStreamStart();
    }

    public Tokenizer(String yaml) {
        this(new TokenScanner(yaml));
    }


    public Iterator iterator () {
        return new Iterator() {
            public boolean hasNext() {
                return null != peekNextToken();
            }

            public Object next() {
                return getNextToken();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public Token getNextToken() {
        while (isNeedMoreTokens())
            fetchToken();
        if (!tokens.isEmpty()) {
                taken++;
                return tokens.remove(0);
        }
        return null;
    }

    public Token peekNextToken() {
        while (isNeedMoreTokens()) {
            fetchToken();
        }
        return tokens.isEmpty() ? null : tokens.get(0);
    }

    private boolean isNeedMoreTokens() {
        if (flagStreamEnd) return false;
        return tokens.isEmpty() || nextPossibleTokenNo() == taken;
    }

    private void fetchToken() {
        ts.scanNextToken();
        for (int i = ts.countCloseBlock(); i > 0; i--) {
            tokens.add(Token.BLOCK_END);
        }
        char ch = ts.peekChar();
        switch (ch) {
            case '\0' :
                fetchStreamEnd();
                return;
            case '\'' :
                fetchFlowScalar('\'');
                return;
            case '"'  :
                fetchFlowScalar('"');
                return;
            case ':'  :
                if (ts.canGetKV()) {
                    fetchValue();
                    return;
                }
                break;
            case '-' :
                if (Define.NULL_OR_OTHER.indexOf(ts.peekChar(1)) != -1) {
                    fetchBlockEntry();
                    return;
                }
                break;
            case '!' :
                fetchClassName();
                return;
            default:

        }
        if (Define.BEG.matcher(ts.peekString(2)).find())
            fetchPlain();
    }

    private void fetchStreamStart() {
        tokens.add(Token.STREAM_START);
        flagStreamStart = true;
    }

    private void fetchStreamEnd() {
        for (int i = ts.countCloseBlock(-1); i > 0; i--) {
            tokens.add(Token.BLOCK_END);
        }
        ts.setTkGetAble(false);
        tokenKeyMap.clear();
        tokens.add(Token.STREAM_END);
        flagStreamEnd = true;
    }

    private void fetchFlowScalar(char style) {
        savePossibleTokenKey();
        ts.setTkGetAble(false);
        tokens.add(ts.scanFlowScalar(style));
    }

    private void fetchKey() {
        if (ts.isDepthZero()) {
            if (!ts.isTkGetAble()) throw new TokenizerException("Found a mapping key where it is not allowed.");
            if (ts.addIndent(ts.getCno())) tokens.add(Token.BLOCK_MAPPING_START);
        }
        ts.setTkGetAble(ts.isDepthZero());
        ts.toNext();
        tokens.add(Token.KEY);
    }

    private void fetchValue() {
        TokenKey tk = tokenKeyMap.get(ts.getDepth());
        if (tk == null) {
            if (ts.isDepthZero() && !ts.isTkGetAble()) {
                throw new TokenizerException("Found a mapping value where it is not allowed.");
            }
        } else {
            tokenKeyMap.remove(ts.getDepth());
            tokens.add(tk.tkn - taken, Token.KEY);
            if (ts.isDepthZero() && ts.addIndent(tk.cno)) {
                tokens.add(tk.tkn - taken, Token.BLOCK_MAPPING_START);
            }
            ts.setTkGetAble(false);
        }
        ts.toNext();
        tokens.add(Token.VALUE);
    }

    private void fetchClassName() {
        savePossibleTokenKey();
        ts.setTkGetAble(false);
        ts.toNext();
        tokens.add(ts.scanClassName());
    }

    private void fetchPlain() {
        savePossibleTokenKey();
        ts.setTkGetAble(false);
        tokens.add(ts.scanPlain());
    }

    private void fetchBlockEntry() {
        if (ts.getDepth() == 0) {
            if (!ts.isTkGetAble()) throw new TokenizerException("Found a sequence entry where it is not allowed.");
            if (ts.addIndent(ts.getCno())) tokens.add(Token.BLOCK_SEQUENCE_START);
        }
        ts.setTkGetAble(true);
        ts.toNext();
        tokens.add(Token.BLOCK_ENTRY);
    }

    private void savePossibleTokenKey() {
        if (ts.isTkGetAble()) {
            tokenKeyMap.put(ts.getDepth(),
                    new TokenKey(tokens.size() + taken, ts.getCno()));
        }
    }

    private int nextPossibleTokenNo() {
        for (TokenKey tk : tokenKeyMap.values()) {
            if (tk.tkn > 0) return tk.tkn;
        }
        return -1;
    }

    public class TokenizerException extends RuntimeException {
        public TokenizerException (String msg, Throwable cause) {
            super(msg, cause);
        }
        public TokenizerException (String msg) {
            super(msg, null);
        }
    }


    public static void main(String[] args) {
        String path = YamlFactory.class.getClassLoader().getResource("").getPath()+"test.yml";
        Tokenizer tokenizer = new Tokenizer(path);
        Iterator iterator = tokenizer.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }
}

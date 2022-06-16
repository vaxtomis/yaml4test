package com.vaxtomis.yaml4test.tokenizer;

import com.vaxtomis.yaml4test.YamlFactory;
import com.vaxtomis.yaml4test.common.Define;

import java.util.*;

/**
 * <p>
 * Tokenizer.<br>
 * Reference Project: com.esotericsoftware.yamlbeans
 * (Copyright (c) 2008 Nathan Sweet, Copyright (c) 2006 Ola Bini)
 * </p>
 */
public class Tokenizer {
    private TokenScanner tokenScanner;
    private int taken = 0;
    private boolean flagStreamStart = false;
    private boolean flagStreamEnd = false;
    private final List<Token> tokens = new LinkedList<Token>();
    private final Map<Integer,TokenKey> tokenKeyMap = new HashMap<>();

    static class TokenKey {
        public final int tokenNumber;
        public final int columnNumber;
        TokenKey(int tokenNumber, int columnNumber) {
            this.tokenNumber = tokenNumber;
            this.columnNumber = columnNumber;
        }
    }

    public Tokenizer(TokenScanner tokenScanner) {
        this.tokenScanner = tokenScanner;
        fetchStreamStart();
    }

    public Tokenizer(String yaml) {
        this(TokenScanner.getInstance(yaml));
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
        while (isNeedMoreTokens()) {
            fetchToken();
        }
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
        if (flagStreamEnd) {
            return false;
        }
        return tokens.isEmpty() || nextPossibleTokenNo() == taken;
    }

    private void fetchToken() {
        tokenScanner.scanNextToken();
        for (int i = tokenScanner.countCloseBlock(); i > 0; i--) {
            tokens.add(Token.BLOCK_END);
        }
        char ch = tokenScanner.peekChar();
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
                if (tokenScanner.ableToGetKeyValue()) {
                    fetchValue();
                    return;
                }
                break;
            case '-' :
                if (Define.NULL_OR_OTHER.indexOf(tokenScanner.peekChar(1)) != -1) {
                    fetchBlockEntry();
                    return;
                }
                break;
            case '!' :
                fetchClassName();
                return;
            default:

        }
        if (Define.BEG.matcher(tokenScanner.peekString(2)).find())
            fetchPlain();
    }

    private void fetchStreamStart() {
        tokens.add(Token.STREAM_START);
        flagStreamStart = true;
    }

    private void fetchStreamEnd() {
        for (int i = tokenScanner.countCloseBlock(-1); i > 0; i--) {
            tokens.add(Token.BLOCK_END);
        }
        tokenScanner.setAbleToGetToken(false);
        tokenKeyMap.clear();
        tokens.add(Token.STREAM_END);
        flagStreamEnd = true;
    }

    private void fetchFlowScalar(char style) {
        savePossibleTokenKey();
        tokenScanner.setAbleToGetToken(false);
        tokens.add(tokenScanner.scanFlowScalar(style));
    }

    private void fetchKey() {
        if (tokenScanner.isDepthZero()) {
            if (!tokenScanner.isAbleToGetToken()) {
                throw new TokenizerException("Found a mapping key where it is not allowed.");
            }
            if (tokenScanner.addIndent(tokenScanner.getCno())) {
                tokens.add(Token.BLOCK_MAPPING_START);
            }
        }
        tokenScanner.setAbleToGetToken(tokenScanner.isDepthZero());
        tokenScanner.toNext();
        tokens.add(Token.KEY);
    }

    private void fetchValue() {
        TokenKey tk = tokenKeyMap.get(tokenScanner.getDepth());
        if (tk == null) {
            if (tokenScanner.isDepthZero() && !tokenScanner.isAbleToGetToken()) {
                throw new TokenizerException("Found a mapping value where it is not allowed.");
            }
        } else {
            tokenKeyMap.remove(tokenScanner.getDepth());
            tokens.add(tk.tokenNumber - taken, Token.KEY);
            if (tokenScanner.isDepthZero() && tokenScanner.addIndent(tk.columnNumber)) {
                tokens.add(tk.tokenNumber - taken, Token.BLOCK_MAPPING_START);
            }
            tokenScanner.setAbleToGetToken(false);
        }
        tokenScanner.toNext();
        tokens.add(Token.VALUE);
    }

    private void fetchClassName() {
        savePossibleTokenKey();
        tokenScanner.setAbleToGetToken(false);
        tokenScanner.toNext();
        tokens.add(tokenScanner.scanClassName());
    }

    private void fetchPlain() {
        savePossibleTokenKey();
        tokenScanner.setAbleToGetToken(false);
        tokens.add(tokenScanner.scanPlain());
    }

    private void fetchBlockEntry() {
        if (tokenScanner.getDepth() == 0) {
            if (!tokenScanner.isAbleToGetToken()) {
                throw new TokenizerException("Found a sequence entry where it is not allowed.");
            }
            if (tokenScanner.addIndent(tokenScanner.getCno())) {
                tokens.add(Token.BLOCK_SEQUENCE_START);
            }
        }
        tokenScanner.setAbleToGetToken(true);
        tokenScanner.toNext();
        tokens.add(Token.BLOCK_ENTRY);
    }

    private void savePossibleTokenKey() {
        if (tokenScanner.isAbleToGetToken()) {
            tokenKeyMap.put(tokenScanner.getDepth(),
                    new TokenKey(tokens.size() + taken, tokenScanner.getCno()));
        }
    }

    private int nextPossibleTokenNo() {
        for (TokenKey tk : tokenKeyMap.values()) {
            if (tk.tokenNumber > 0) {
                return tk.tokenNumber;
            }
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

package com.vaxtomis.yaml4test.Tokenizer;

import com.vaxtomis.yaml4test.YamlFactory;
import org.junit.Test;

/**
 * @author vaxtomis
 */
public class TokenScannerTest {
    TokenScanner tokenScanner = new TokenScanner(
            YamlFactory.class.getClassLoader().getResource("").getPath() + "tokenScannerTest.yml");
    @Test
    public void scanNextToken() {
        tokenScanner.scanNextToken();
        System.out.println(tokenScanner.peekChar());
    }

    @Test
    public void scanPlain() {
        tokenScanner.scanNextToken();
        System.out.println(tokenScanner.scanPlain());
    }

    @Test
    public void scanFlowScalar() {
        //tokenScanner.toNext(2);
        tokenScanner.scanNextToken();
        System.out.println(tokenScanner.scanFlowScalar('\''));
        tokenScanner.isLineBreak();
        tokenScanner.toNext(2);
        tokenScanner.scanNextToken();
        System.out.println(tokenScanner.scanFlowScalar('"'));
    }
    @Test
    public void scanClassName() {
        while (tokenScanner.peekChar() == '!') {
            tokenScanner.toNext();
        }
        System.out.println(tokenScanner.scanClassName());
    }
}

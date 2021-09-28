package com.vaxtomis.yaml4test.Tokenizer;

public interface TokenScan {
    Token scanPlain();

    Token scanFlowScalar(char style);

    Token scanClassName();

}

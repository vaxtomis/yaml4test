package com.vaxtomis.yaml4test.Tokenizer;

interface TokenScan {
    Token scanPlain();

    Token scanFlowScalar(char style);

    Token scanClassName();

}

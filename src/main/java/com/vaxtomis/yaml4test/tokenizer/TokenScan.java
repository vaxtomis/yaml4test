package com.vaxtomis.yaml4test.tokenizer;

interface TokenScan {
    Token scanPlain();

    Token scanFlowScalar(char style);

    Token scanClassName();

}

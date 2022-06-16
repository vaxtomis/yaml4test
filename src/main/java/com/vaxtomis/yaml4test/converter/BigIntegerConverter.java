package com.vaxtomis.yaml4test.converter;

import java.math.BigInteger;

/**
 * BigInteger Converter.
 * @author vaxtomis
 */
class BigIntegerConverter extends AbstractCustomConverter {
    @Override
    public BigInteger customConvert(String pairValue) {
        if (pairValue.matches("^(-|\\+)?\\d+$")) {
            return new BigInteger(pairValue);
        } else {
            return new BigInteger("0");
        }
    }
}

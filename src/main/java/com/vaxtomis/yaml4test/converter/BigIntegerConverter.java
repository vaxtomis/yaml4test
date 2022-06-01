package com.vaxtomis.yaml4test.Converter;

import java.math.BigInteger;

/**
 * BigInteger Converter.
 * @author vaxtomis
 */
class BigIntegerConverter extends AbstractCustomConverter {
    @Override
    public BigInteger customConvert(String getV) {
        if (getV.matches("^(-|\\+)?\\d+$")) {
            return new BigInteger(getV);
        } else {
            return new BigInteger("0");
        }
    }
}

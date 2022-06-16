package com.vaxtomis.yaml4test.converter;

import java.math.BigInteger;

import static com.vaxtomis.yaml4test.common.Define.MATCH_BIG_INTEGER;

/**
 * BigInteger Converter.
 * @author vaxtomis
 */
class BigIntegerConverter extends AbstractCustomConverter {
    @Override
    public BigInteger customConvert(String pairValue) {
        if (pairValue.matches(MATCH_BIG_INTEGER)) {
            return new BigInteger(pairValue);
        } else {
            return new BigInteger("0");
        }
    }
}

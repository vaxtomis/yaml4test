package com.vaxtomis.yaml4test.Converter;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.math.BigInteger;

/**
 * BigInteger Converter.
 * @author vaxtomis
 */
public class BigIntegerConverter extends AbstractConverter {
    @Override
    public boolean convertObj(Method method, Object beInject, String getV) {
        BigInteger temp = (canParse(getV, "bigInteger"))?new BigInteger(getV):new BigInteger("0");
        return setterInject(method, beInject, temp);
    }

    @Override
    public void convertObjs(Object newArray, String[] pairValueArray) {
        for (int i = 0; i < pairValueArray.length; i++) {
            String getV = pairValueArray[i];
            BigInteger temp = (canParse(getV, "bigInteger"))?new BigInteger(getV):new BigInteger("0");
            Array.set(newArray, i, temp);
        }
    }
}

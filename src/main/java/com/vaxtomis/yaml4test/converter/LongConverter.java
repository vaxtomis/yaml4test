package com.vaxtomis.yaml4test.converter;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

/**
 * long & Long Converter.
 * @author vaxtomis
 */
class LongConverter extends AbstractConverter {
    @Override
    public boolean convertObj(Method method, Object beInject, String pairValue) {
        long temp = (isCorrectFormat(pairValue, "long"))?Long.parseLong(pairValue):0L;
        return setterInject(method, beInject, temp);
    }

    @Override
    public void convertObjs(Object newArray, String[] pairValueArray) {
        for (int i = 0; i < pairValueArray.length; i++) {
            String pairValue = pairValueArray[i];
            long temp = (isCorrectFormat(pairValue, "long"))?Long.parseLong(pairValue):0L;
            if (!newArray.getClass().isPrimitive()) {
                Array.set(newArray, i, temp);
                continue;
            }
            Array.setLong(newArray, i, temp);
        }
    }
}

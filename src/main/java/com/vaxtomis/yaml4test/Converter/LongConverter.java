package com.vaxtomis.yaml4test.Converter;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

/**
 * long & Long Converter.
 * @author vaxtomis
 */
class LongConverter extends AbstractConverter {
    @Override
    public boolean convertObj(Method method, Object beInject, String getV) {
        long temp = (isCorrectFormat(getV, "long"))?Long.parseLong(getV):0L;
        return setterInject(method, beInject, temp);
    }

    @Override
    public void convertObjs(Object newArray, String[] pairValueArray) {
        for (int i = 0; i < pairValueArray.length; i++) {
            String getV = pairValueArray[i];
            long temp = (isCorrectFormat(getV, "long"))?Long.parseLong(getV):0L;
            Array.setLong(newArray, i, temp);
        }
    }
}

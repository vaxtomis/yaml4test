package com.vaxtomis.yaml4test.converter;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

/**
 * boolean & Boolean Converter.
 * @author vaxtomis
 */
class BooleanConverter extends AbstractConverter {
    @Override
    public boolean convertObj(Method method, Object beInject, String pairValue) {
        boolean temp = (isCorrectFormat(pairValue, "boolean")) && Boolean.parseBoolean(pairValue);
        return setterInject(method, beInject, temp);
    }

    @Override
    public void convertObjs(Object newArray, String[] pairValueArray) {
        for (int i = 0; i < pairValueArray.length; i++) {
            String pairValue = pairValueArray[i];
            boolean temp = (isCorrectFormat(pairValue, "boolean")) && Boolean.parseBoolean(pairValue);
            if (!newArray.getClass().isPrimitive()) {
                Array.set(newArray, i, temp);
                continue;
            }
            Array.setBoolean(newArray, i, temp);
        }
    }
}

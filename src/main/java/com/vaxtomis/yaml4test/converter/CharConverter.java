package com.vaxtomis.yaml4test.converter;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

/**
 * Char & Character Converter.
 * @author vaxtomis
 */
class CharConverter extends AbstractConverter {
    @Override
    public boolean convertObj(Method method, Object beInject, String pairValue) {
        char temp = (pairValue != null)? pairValue.charAt(0):'\u0000';
        return setterInject(method, beInject, temp);
    }

    @Override
    public void convertObjs(Object newArray, String[] pairValueArray) {
        for (int i = 0; i < pairValueArray.length; i++) {
            String pairValue = pairValueArray[i];
            char temp = (pairValue != null)?pairValue.charAt(0):'\u0000';
            if (!newArray.getClass().isPrimitive()) {
                Array.set(newArray, i, temp);
                continue;
            }
            Array.setChar(newArray, i, temp);
        }
    }
}

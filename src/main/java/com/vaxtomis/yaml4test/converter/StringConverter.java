package com.vaxtomis.yaml4test.converter;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

import static com.vaxtomis.yaml4test.common.Define.EMPTY;

/**
 * String Converter.
 * @author vaxtomis
 */
class StringConverter extends AbstractConverter {
    @Override
    public boolean convertObj(Method method, Object beInject, String pairValue) {
        if (pairValue == null) {
            pairValue = EMPTY;
        }
        return setterInject(method, beInject, pairValue);
    }

    @Override
    public void convertObjs(Object newArray, String[] pairValueArray) {
        for (int i = 0; i < pairValueArray.length; i++) {
            String pairValue = pairValueArray[i];
            if (pairValue == null) {
                pairValue = EMPTY;
            }
            Array.set(newArray, i, pairValue);
        }
    }
}

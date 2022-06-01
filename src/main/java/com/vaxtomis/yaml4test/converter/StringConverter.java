package com.vaxtomis.yaml4test.converter;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

import static com.vaxtomis.yaml4test.tokenizer.Define.EMPTY;

/**
 * String Converter.
 * @author vaxtomis
 */
class StringConverter extends AbstractConverter {
    @Override
    public boolean convertObj(Method method, Object beInject, String getV) {
        if (getV == null) {
            getV = EMPTY;
        }
        return setterInject(method, beInject, getV);
    }

    @Override
    public void convertObjs(Object newArray, String[] pairValueArray) {
        for (int i = 0; i < pairValueArray.length; i++) {
            String getV = pairValueArray[i];
            if (getV == null) {
                getV = EMPTY;
            }
            Array.set(newArray, i, getV);
        }
    }
}

package com.vaxtomis.yaml4test.Converter;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

/**
 * String Converter.
 * @author vaxtomis
 */
class StringConverter extends AbstractConverter {
    @Override
    public boolean convertObj(Method method, Object beInject, String getV) {
        if (getV == null) getV = "";
        return setterInject(method, beInject, getV);
    }

    @Override
    public void convertObjs(Object newArray, String[] pairValueArray) {
        for (int i = 0; i < pairValueArray.length; i++) {
            String getV = pairValueArray[i];
            if (getV == null) getV = "";
            Array.set(newArray, i, getV);
        }
    }
}

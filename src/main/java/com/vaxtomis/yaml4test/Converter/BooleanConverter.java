package com.vaxtomis.yaml4test.Converter;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

/**
 * boolean & Boolean Converter.
 * @author vaxtomis
 */
class BooleanConverter extends AbstractConverter {
    @Override
    public boolean convertObj(Method method, Object beInject, String getV) {
        boolean temp = (canParse(getV, "boolean")) && Boolean.parseBoolean(getV);
        return setterInject(method, beInject, temp);
    }

    @Override
    public void convertObjs(Object newArray, String[] pairValueArray) {
        for (int i = 0; i < pairValueArray.length; i++) {
            String getV = pairValueArray[i];
            boolean temp = (canParse(getV, "boolean")) && Boolean.parseBoolean(getV);
            Array.setBoolean(newArray, i, temp);
        }
    }
}

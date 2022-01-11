package com.vaxtomis.yaml4test.Converter;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

/**
 * @author vaxtomis
 */
public abstract class CustomConverter extends AbstractConverter implements CustomConvert {
    @Override
    public boolean convertObj(Method method, Object beInject, String getV) {
        return setterInject(method, beInject, customConvert(getV));
    }

    @Override
    public void convertObjs(Object newArray, String[] pairValueArray) {
        for (int i = 0; i < pairValueArray.length; i++) {
            String getV = pairValueArray[i];
            Array.set(newArray, i, customConvert(getV));
        }
    }
}

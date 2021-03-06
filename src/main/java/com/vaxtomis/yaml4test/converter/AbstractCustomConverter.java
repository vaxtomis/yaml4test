package com.vaxtomis.yaml4test.converter;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

/**
 * @author vaxtomis
 */
public abstract class AbstractCustomConverter extends AbstractConverter implements CustomConvert {
    @Override
    public boolean convertObj(Method method, Object beInject, String pairValue) {
        return setterInject(method, beInject, customConvert(pairValue));
    }

    @Override
    public void convertObjs(Object newArray, String[] pairValueArray) {
        for (int i = 0; i < pairValueArray.length; i++) {
            String getV = pairValueArray[i];
            Array.set(newArray, i, customConvert(getV));
        }
    }
}

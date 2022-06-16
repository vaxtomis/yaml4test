package com.vaxtomis.yaml4test.converter;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

/**
 * int & Integer Converter.
 * @author vaxtomis
 */
class IntConverter extends AbstractConverter {
    @Override
    public boolean convertObj(Method method, Object beInject, String pairValue) {
        int temp = (isCorrectFormat(pairValue, "int"))?Integer.parseInt(pairValue):0;
        return setterInject(method, beInject, temp);
    }

    @Override
    public void convertObjs(Object newArray, String[] pairValueArray) {
        for (int i = 0; i < pairValueArray.length; i++) {
            String pairValue = pairValueArray[i];
            int temp = (isCorrectFormat(pairValue, "int"))?Integer.parseInt(pairValue):0;
            if (!newArray.getClass().isPrimitive()) {
                Array.set(newArray, i, temp);
                continue;
            }
            Array.setInt(newArray, i, temp);
        }
    }
}

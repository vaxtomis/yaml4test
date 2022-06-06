package com.vaxtomis.yaml4test.converter;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

/**
 * int & Integer Converter.
 * @author vaxtomis
 */
class IntConverter extends AbstractConverter {
    @Override
    public boolean convertObj(Method method, Object beInject, String getV) {
        int temp = (isCorrectFormat(getV, "int"))?Integer.parseInt(getV):0;
        return setterInject(method, beInject, temp);
    }

    @Override
    public void convertObjs(Object newArray, String[] pairValueArray) {
        for (int i = 0; i < pairValueArray.length; i++) {
            String getV = pairValueArray[i];
            int temp = (isCorrectFormat(getV, "int"))?Integer.parseInt(getV):0;
            if (!newArray.getClass().isPrimitive()) {
                Array.set(newArray, i, temp);
                continue;
            }
            Array.setInt(newArray, i, temp);
        }
    }
}

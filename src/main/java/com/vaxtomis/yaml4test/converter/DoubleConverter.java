package com.vaxtomis.yaml4test.converter;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

/**
 * double & Double Converter.
 * @author vaxtomis
 */
class DoubleConverter extends AbstractConverter {
    @Override
    public boolean convertObj(Method method, Object beInject, String pairValue) {
        double temp = (isCorrectFormat(pairValue, "double"))?Double.parseDouble(pairValue):0.0d;
        return setterInject(method, beInject, temp);
    }

    @Override
    public void convertObjs(Object newArray, String[] pairValueArray) {
        for (int i = 0; i < pairValueArray.length; i++) {
            String pairValue = pairValueArray[i];
            double temp = (isCorrectFormat(pairValue, "double"))?Double.parseDouble(pairValue):0.0d;
            if (!newArray.getClass().isPrimitive()) {
                Array.set(newArray, i, temp);
                continue;
            }
            Array.setDouble(newArray, i, temp);
        }
    }
}

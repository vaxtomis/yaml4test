package com.vaxtomis.yaml4test.converter;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

/**
 * float & Float Converter.
 * @author vaxtomis
 */
class FloatConverter extends AbstractConverter {
    @Override
    public boolean convertObj(Method method, Object beInject, String pairValue) {
        float temp = (isCorrectFormat(pairValue, "float"))?Float.parseFloat(pairValue):0.0f;
        return setterInject(method, beInject, temp);
    }

    @Override
    public void convertObjs(Object newArray, String[] pairValueArray) {
        for (int i = 0; i < pairValueArray.length; i++) {
            String pairValue = pairValueArray[i];
            float temp = (isCorrectFormat(pairValue, "float"))?Float.parseFloat(pairValue):0.0f;
            if (!newArray.getClass().isPrimitive()) {
                Array.set(newArray, i, temp);
                continue;
            }
            Array.setFloat(newArray, i, temp);
        }
    }
}

package com.vaxtomis.yaml4test.converter;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

/**
 * Byte Converter.
 * @author vaxtomis
 */
class ByteConverter extends AbstractConverter {
    @Override
    public boolean convertObj(Method method, Object beInject, String pairValue) {
        Byte temp = (isCorrectFormat(pairValue, "byte"))?Byte.parseByte(pairValue, 16):0;
        return setterInject(method, beInject, temp);
    }

    @Override
    public void convertObjs(Object newArray, String[] pairValueArray) {
        for (int i = 0; i < pairValueArray.length; i++) {
            String pairValue = pairValueArray[i];
            Byte temp = (isCorrectFormat(pairValue, "byte"))?Byte.parseByte(pairValue, 16):0;
            Array.set(newArray, i, temp);
        }
    }
}

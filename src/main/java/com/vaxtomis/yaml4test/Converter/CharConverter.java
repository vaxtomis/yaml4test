package com.vaxtomis.yaml4test.Converter;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

/**
 * Char & Character Converter.
 * @author vaxtomis
 */
public class CharConverter extends AbstractConverter {
    @Override
    public boolean convertObj(Method method, Object beInject, String getV) {
        char temp = (getV != null)?getV.charAt(0):'\u0000';
        return setterInject(method, beInject, temp);
    }

    @Override
    public void convertObjs(Object newArray, String[] pairValueArray) {
        for (int i = 0; i < pairValueArray.length; i++) {
            String getV = pairValueArray[i];
            char temp = (getV != null)?getV.charAt(0):'\u0000';
            Array.setChar(newArray, i, temp);
        }
    }
}
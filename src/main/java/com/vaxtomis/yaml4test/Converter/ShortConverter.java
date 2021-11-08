package com.vaxtomis.yaml4test.Converter;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

/**
 * short & Short Converter.
 * @author vaxtomis
 */
public class ShortConverter extends AbstractConverter {
    @Override
    public boolean convertObj(Method method, Object beInject, String getV) {
        short temp = (canParse(getV, "short"))?Short.parseShort(getV):0;
        return setterInject(method, beInject, temp);
    }

    @Override
    public void convertObjs(Object newArray, String[] pairValueArray) {
        for (int i = 0; i < pairValueArray.length; i++) {
            String getV = pairValueArray[i];
            short temp = (canParse(getV, "short"))?Short.parseShort(getV):0;
            Array.setShort(newArray, i, temp);
        }
    }
}

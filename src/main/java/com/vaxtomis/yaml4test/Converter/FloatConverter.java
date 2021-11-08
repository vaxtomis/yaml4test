package com.vaxtomis.yaml4test.Converter;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

/**
 * float & Float Converter.
 * @author vaxtomis
 */
public class FloatConverter extends AbstractConverter {
    @Override
    public boolean convertObj(Method method, Object beInject, String getV) {
        float temp = (canParse(getV, "float"))?Float.parseFloat(getV):0.0f;
        return setterInject(method, beInject, temp);
    }

    @Override
    public void convertObjs(Object newArray, String[] pairValueArray) {
        for (int i = 0; i < pairValueArray.length; i++) {
            String getV = pairValueArray[i];
            float temp = (canParse(getV, "float"))?Float.parseFloat(getV):0.0f;
            Array.setFloat(newArray, i, temp);
        }
    }
}

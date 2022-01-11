package com.vaxtomis.yaml4test.Converter;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

/**
 * double & Double Converter.
 * @author vaxtomis
 */
class DoubleConverter extends AbstractConverter {
    @Override
    public boolean convertObj(Method method, Object beInject, String getV) {
        double temp = (canParse(getV, "double"))?Double.parseDouble(getV):0.0d;
        return setterInject(method, beInject, temp);
    }

    @Override
    public void convertObjs(Object newArray, String[] pairValueArray) {
        for (int i = 0; i < pairValueArray.length; i++) {
            String getV = pairValueArray[i];
            double temp = (canParse(getV, "double"))?Double.parseDouble(getV):0.0d;
            Array.setDouble(newArray, i, temp);
        }
    }
}

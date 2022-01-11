package com.vaxtomis.yaml4test.Converter;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.math.BigDecimal;

/**
 * BigDecimal Converter.
 * @author vaxtomis
 */
class BigDecimalConverter extends AbstractConverter {
    @Override
    public boolean convertObj(Method method, Object beInject, String getV) {
        BigDecimal temp = (canParse(getV, "bigDecimal"))?new BigDecimal(getV):new BigDecimal("0.00");
        return setterInject(method, beInject, temp);
    }

    @Override
    public void convertObjs(Object newArray, String[] pairValueArray) {
        for (int i = 0; i < pairValueArray.length; i++) {
            String getV = pairValueArray[i];
            BigDecimal temp = (canParse(getV, "bigDecimal"))?new BigDecimal(getV):new BigDecimal("0.00");
            Array.set(newArray, i, temp);
        }
    }
}

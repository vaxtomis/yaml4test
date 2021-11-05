package com.vaxtomis.yaml4test.Converter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @date 2021/11/5
 */
public abstract class AbstractConverter implements Convert {
    public boolean setterInject(Method method, Object obj, Object getV) {
        if (method == null || obj == null) {
            return false;
        }
        try {
            method.invoke(obj, getV);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * <=== Mark, Need to optimize. ===>
     *
     * Determine whether this type can be parsed.
     */
    public boolean canParse(String getV, String type) {
        if (getV == null) return false;
        switch (type) {
            case "int":
            case "short":
            case "long":
            case "bigInteger":
                return getV.matches("^(-|\\+)?\\d+$");
            case "double":
            case "float":
            case "bigDecimal":
                return getV.matches("^(-?\\d+)(\\.\\d+)?$");
            case "byte":
                if (!getV.matches("^[0-9a-fA-F]+$")) {
                    return false;
                }
                int i = Integer.parseInt(getV, 16);
                return  i >= -128 && i <= 127;
            case "boolean":
                return true;
            default:

        }
        return false;
    }
}

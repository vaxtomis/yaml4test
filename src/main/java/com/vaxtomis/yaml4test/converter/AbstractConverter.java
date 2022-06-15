package com.vaxtomis.yaml4test.converter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Abstract Converter, achieved some common functions.
 * @author vaxtomis
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
     * TODO Need to optimize.
     * <br>
     * Determine whether this type can be parsed.
     */
    public boolean isCorrectFormat(String getV, String type) {
        if (getV == null) {
            return false;
        }
        switch (type) {
            case "int":
            case "short":
            case "long":
                return getV.matches("^(-|\\+)?\\d+$");
            case "double":
            case "float":
                return getV.matches("^(-?\\d+)(\\.\\d+)?$");
            case "byte":
                if (!getV.matches("^[0-9a-fA-F]+$")) {
                    return false;
                }
                int i = Integer.parseInt(getV, 16);
                return  -128 <= i && i <= 127;
            case "boolean":
                return true;
            default:

        }
        return false;
    }
}

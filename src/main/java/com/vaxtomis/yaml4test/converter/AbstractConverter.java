package com.vaxtomis.yaml4test.converter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.vaxtomis.yaml4test.common.Define.*;

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
    public boolean isCorrectFormat(String value, String type) {
        if (value == null) {
            return false;
        }
        switch (type) {
            case "int":
            case "short":
            case "long":
                return value.matches(MATCH_LONG);
            case "double":
            case "float":
                return value.matches(MATCH_FLOAT);
            case "byte":
                if (!value.matches(MATCH_BYTE)) {
                    return false;
                }
                int i = Integer.parseInt(value, 16);
                return  -128 <= i && i <= 127;
            case "boolean":
                return true;
            default:

        }
        return false;
    }
}

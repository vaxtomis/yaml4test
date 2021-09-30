package com.vaxtomis.yaml4test.Producer;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @author vaxtomis
 */
public class Converter {
    private static final HashSet<Class<?>> classSet = new HashSet<>();
    static {
        Class<?>[] classes = new Class[] {String.class, int.class, Integer.class, char.class,
        Character.class, short.class, Short.class, long.class, Long.class, double.class, Double.class, float.class,
        Float.class, BigInteger.class, BigDecimal.class, Byte.class};
        classSet.addAll(Arrays.asList(classes));
    }

    public static boolean convertObj(HashMap<String, Method> methodMap, Class<?> fClazz, Field field, Object beInject, Object rawPairValue) {
        if (rawPairValue == null) {
            return false;
        }
        String getV = rawPairValue.toString();
        String setMethodName = "set" + field.getName().substring(0,1).toUpperCase() + field.getName().substring(1);
        Method method  = methodMap.get(setMethodName);
        boolean res = false;
        try {
            if (fClazz ==String.class) {
                if (getV == null) getV = "";
                res = setterInject(method, beInject, getV);
            }
            else if (fClazz == char.class || fClazz == Character.class) {
                char temp = (getV != null)?getV.charAt(0):'\u0000';
                res = setterInject(method, beInject, temp);
            }
            else if (fClazz == int.class || fClazz == Integer.class) {
                int temp = (canParse(getV, "int"))?Integer.parseInt(getV):0;
                res = setterInject(method, beInject, temp);
            }
            else if (fClazz == long.class || fClazz == Long.class) {
                long temp = (canParse(getV, "long"))?Long.parseLong(getV):0L;
                res = setterInject(method, beInject, temp);
            }
            else if (fClazz == double.class || fClazz == Double.class) {
                double temp = (canParse(getV, "double"))?Double.parseDouble(getV):0.0d;
                res = setterInject(method, beInject, temp);
            }
            else if (fClazz == float.class || fClazz == Float.class) {
                float temp = (canParse(getV, "float"))?Float.parseFloat(getV):0.0f;
                res = setterInject(method, beInject, temp);
            }
            else if (fClazz == short.class || fClazz == Short.class) {
                short temp = (canParse(getV, "short"))?Short.parseShort(getV):0;
                res = setterInject(method, beInject, temp);
            }
            else if (fClazz == boolean.class || fClazz == Boolean.class) {
                boolean temp = (canParse(getV, "boolean")) && Boolean.parseBoolean(getV);
                res = setterInject(method, beInject, temp);
            }
            else if (fClazz == BigInteger.class) {
                BigInteger temp = (canParse(getV, "bigInteger"))?new BigInteger(getV):new BigInteger("0");
                res = setterInject(method, beInject, temp);
            }
            else if (fClazz == BigDecimal.class) {
                BigDecimal temp = (canParse(getV, "bigDecimal"))?new BigDecimal(getV):new BigDecimal("0.00");
                res = setterInject(method, beInject, temp);
            }
            else if (fClazz == Byte.class) {
                Byte temp = (canParse(getV, "byte"))?Byte.parseByte(getV, 16):0;
                res = setterInject(method, beInject, temp);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static void convertObjs(Class<?> componentType, Field field, Object beInject, Object rawPairValue) {
        String[] rawPairArray = (String[]) rawPairValue;
        Object newArray = Array.newInstance(componentType, rawPairArray.length);
        if (componentType == String.class) {
            for (int i = 0; i < rawPairArray.length; i++) {
                String getV = rawPairArray[i];
                if (getV == null) getV = "";
                Array.set(newArray, i, getV);
            }
        }
        else if (componentType == char.class || componentType == Character.class) {
            for (int i = 0; i < rawPairArray.length; i++) {
                String getV = rawPairArray[i];
                char temp = (getV != null)?getV.charAt(0):'\u0000';
                Array.setChar(newArray, i, temp);
            }
        }
        else if (componentType == int.class || componentType == Integer.class) {
            for (int i = 0; i < rawPairArray.length; i++) {
                String getV = rawPairArray[i];
                int temp = (canParse(getV, "int"))?Integer.parseInt(getV):0;
                Array.setInt(newArray, i, temp);
            }
        }
        else if (componentType == long.class || componentType == Long.class) {
            for (int i = 0; i < rawPairArray.length; i++) {
                String getV = rawPairArray[i];
                long temp = (canParse(getV, "long"))?Long.parseLong(getV):0L;
                Array.setLong(newArray, i, temp);
            }
        }
        else if (componentType == double.class || componentType == Double.class) {
            for (int i = 0; i < rawPairArray.length; i++) {
                String getV = rawPairArray[i];
                double temp = (canParse(getV, "double"))?Double.parseDouble(getV):0.0d;
                Array.setDouble(newArray, i, temp);
            }
        }
        else if (componentType == float.class || componentType == Float.class) {
            for (int i = 0; i < rawPairArray.length; i++) {
                String getV = rawPairArray[i];
                float temp = (canParse(getV, "float"))?Float.parseFloat(getV):0.0f;
                Array.setFloat(newArray, i, temp);
            }
        }
        else if (componentType == short.class || componentType == Short.class) {
            for (int i = 0; i < rawPairArray.length; i++) {
                String getV = rawPairArray[i];
                short temp = (canParse(getV, "short"))?Short.parseShort(getV):0;
                Array.setShort(newArray, i, temp);
            }
        }
        else if (componentType == boolean.class || componentType == Boolean.class) {
            for (int i = 0; i < rawPairArray.length; i++) {
                String getV = rawPairArray[i];
                boolean temp = (canParse(getV, "boolean")) && Boolean.parseBoolean(getV);
                Array.setBoolean(newArray, i, temp);
            }
        }
        else if (componentType == BigInteger.class) {
            for (int i = 0; i < rawPairArray.length; i++) {
                String getV = rawPairArray[i];
                BigInteger temp = (canParse(getV, "bigInteger"))?new BigInteger(getV):new BigInteger("0");
                Array.set(newArray, i, temp);
            }
        }
        else if (componentType == BigDecimal.class) {
            for (int i = 0; i < rawPairArray.length; i++) {
                String getV = rawPairArray[i];
                BigDecimal temp = (canParse(getV, "bigDecimal"))?new BigDecimal(getV):new BigDecimal("0.00");
                Array.set(newArray, i, temp);
            }
        }
        else if (componentType == Byte.class) {
            for (int i = 0; i < rawPairArray.length; i++) {
                String getV = rawPairArray[i];
                Byte temp = (canParse(getV, "byte"))?Byte.parseByte(getV, 16):0;
                Array.set(newArray, i, temp);
            }
        }
        try {
            field.set(beInject, newArray);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * <=== Mark, Need to optimize. ===>
     *
     * Determine whether this type can be parsed.
     */
    private static boolean canParse(String getV, String type) {
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
        }
        return false;
    }

    private static boolean setterInject(Method method, Object obj, Object getV) {
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

    public static boolean isPrimitive(Class<?> clazz) {
        return classSet.contains(clazz);
    }
}

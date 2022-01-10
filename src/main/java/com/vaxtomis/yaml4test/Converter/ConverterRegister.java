package com.vaxtomis.yaml4test.Converter;

import com.sun.javaws.jnl.PropertyDesc;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import java.beans.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

/**
 * 将 String 转换为类中属性定义的类型。
 * 策略模式
 * @author vaxtomis
 */
public class ConverterRegister {
    private static final HashSet<Class<?>> classSet = new HashSet<>();
    private static final HashMap<Class<?>, Convert> convertMap = new HashMap<>();
    private static final StringConverter stringCvt = new StringConverter();
    private static final CharConverter charCvt = new CharConverter();
    private static final IntConverter intCvt = new IntConverter();
    private static final LongConverter longCvt = new LongConverter();
    private static final DoubleConverter doubleCvt = new DoubleConverter();
    private static final FloatConverter floatCvt = new FloatConverter();
    private static final ShortConverter shortCvt = new ShortConverter();
    private static final BooleanConverter booleanCvt = new BooleanConverter();
    private static final BigIntegerConverter bigIntCvt = new BigIntegerConverter();
    private static final BigDecimalConverter bigDecCvt = new BigDecimalConverter();
    private static final ByteConverter byteCvt = new ByteConverter();

    static {
        Class<?>[] primitiveClass = new Class[] {String.class, int.class, Integer.class, char.class,
                Character.class, short.class, Short.class, long.class, Long.class, double.class, Double.class, float.class,
                Float.class, BigInteger.class, BigDecimal.class, Byte.class};
        Class<?>[] dateClass = new Class[] {Date.class, LocalDate.class, LocalTime.class, LocalDateTime.class};
        classSet.addAll(Arrays.asList(primitiveClass));
        classSet.addAll(Arrays.asList(dateClass));

        convertMap.put(String.class, stringCvt);
        convertMap.put(char.class, charCvt);
        convertMap.put(Character.class, charCvt);
        convertMap.put(int.class, intCvt);
        convertMap.put(Integer.class, intCvt);
        convertMap.put(long.class, longCvt);
        convertMap.put(Long.class, longCvt);
        convertMap.put(double.class, doubleCvt);
        convertMap.put(Double.class, doubleCvt);
        convertMap.put(float.class, floatCvt);
        convertMap.put(Float.class, floatCvt);
        convertMap.put(short.class, shortCvt);
        convertMap.put(Short.class, shortCvt);
        convertMap.put(boolean.class, booleanCvt);
        convertMap.put(Boolean.class, booleanCvt);
        convertMap.put(BigInteger.class, bigIntCvt);
        convertMap.put(BigDecimal.class, bigDecCvt);
        convertMap.put(Byte.class, byteCvt);
    }

    /**
     * 单个实例的注入，优先尝试使用 Setter 方式去注入
     * methodMap: 需要被注入的类的方法集合，通过 Map 存储
     * field: 被注入类的对应被注入属性的 Field
     * beInject: 被注入的类实例对象
     * rawPairValue: 要注入的信息
     */
    public static boolean injectObj(Method method, Class<?> fClazz, Object beInject, Object rawPairValue) {
        if (rawPairValue == null) {
            return false;
        }
        String getV = rawPairValue.toString();
        Convert convert = convertMap.get(fClazz);
        if (convert == null) {
            return false;
        }
        return convert.convertObj(method, beInject, getV);
    }

    /**
     * 对 Array 进行注入
     * field: 被注入类的对应被注入属性的 Field
     * beInject: 被注入的类实例对象
     * rawPairValue: 要注入的信息
     */
    public static void injectObjs(Class<?> componentType, Field field, Object beInject, Object rawPairValue) {
        String[] pairValueArray = (String[]) rawPairValue;
        // 创建空的 Array 对象
        Object newArray = Array.newInstance(componentType, pairValueArray.length);
        convertMap.get(componentType).convertObjs(newArray, pairValueArray);
        try {
            field.set(beInject, newArray);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static boolean isPrimitive(Class<?> clazz) {
        return classSet.contains(clazz);
    }

    public static boolean register(Class<?> clazz, Convert converter) {
        if (classSet.contains(clazz) || convertMap.containsKey(clazz)) {
            return false;
        }
        classSet.add(clazz);
        convertMap.put(clazz, converter);
        return true;
    }

}

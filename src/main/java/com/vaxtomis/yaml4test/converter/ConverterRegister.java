package com.vaxtomis.yaml4test.converter;

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
 * 将 String 转换为类中属性定义的类型。<br>
 * 策略模式
 * @author vaxtomis
 */
public class ConverterRegister {
    private static final HashSet<Class<?>> CLASS_SET = new HashSet<>();
    private static final HashMap<Class<?>, Convert> CONVERT_MAP = new HashMap<>();
    private static final StringConverter STRING_CVT = new StringConverter();
    private static final CharConverter CHAR_CVT = new CharConverter();
    private static final IntConverter INT_CVT = new IntConverter();
    private static final LongConverter LONG_CVT = new LongConverter();
    private static final DoubleConverter DOUBLE_CVT = new DoubleConverter();
    private static final FloatConverter FLOAT_CVT = new FloatConverter();
    private static final ShortConverter SHORT_CVT = new ShortConverter();
    private static final BooleanConverter BOOLEAN_CVT = new BooleanConverter();
    private static final BigIntegerConverter BIG_INT_CVT = new BigIntegerConverter();
    private static final BigDecimalConverter BIG_DEC_CVT = new BigDecimalConverter();
    private static final ByteConverter BYTE_CVT = new ByteConverter();

    static {
        Class<?>[] primitiveClass = new Class[] {String.class, int.class, Integer.class, char.class,
                Character.class, short.class, Short.class, long.class, Long.class, double.class, Double.class, float.class,
                Float.class, BigInteger.class, BigDecimal.class, Byte.class};
        Class<?>[] dateClass = new Class[] {Date.class, LocalDate.class, LocalTime.class, LocalDateTime.class};
        CLASS_SET.addAll(Arrays.asList(primitiveClass));
        CLASS_SET.addAll(Arrays.asList(dateClass));

        CONVERT_MAP.put(String.class, STRING_CVT);
        CONVERT_MAP.put(char.class, CHAR_CVT);
        CONVERT_MAP.put(Character.class, CHAR_CVT);
        CONVERT_MAP.put(int.class, INT_CVT);
        CONVERT_MAP.put(Integer.class, INT_CVT);
        CONVERT_MAP.put(long.class, LONG_CVT);
        CONVERT_MAP.put(Long.class, LONG_CVT);
        CONVERT_MAP.put(double.class, DOUBLE_CVT);
        CONVERT_MAP.put(Double.class, DOUBLE_CVT);
        CONVERT_MAP.put(float.class, FLOAT_CVT);
        CONVERT_MAP.put(Float.class, FLOAT_CVT);
        CONVERT_MAP.put(short.class, SHORT_CVT);
        CONVERT_MAP.put(Short.class, SHORT_CVT);
        CONVERT_MAP.put(boolean.class, BOOLEAN_CVT);
        CONVERT_MAP.put(Boolean.class, BOOLEAN_CVT);
        CONVERT_MAP.put(BigInteger.class, BIG_INT_CVT);
        CONVERT_MAP.put(BigDecimal.class, BIG_DEC_CVT);
        CONVERT_MAP.put(Byte.class, BYTE_CVT);
    }

    /**
     * 单个实例的注入，优先尝试使用 Setter 方式去注入<br>
     * @param method setter 方法
     * @param beInject 被注入的类实例对象
     * @param rawPairValue 要注入的信息
     * @return boolean
     */
    public static boolean injectObj(Method method, Class<?> fClazz, Object beInject, Object rawPairValue) {
        assert rawPairValue != null;
        String getV = rawPairValue.toString();
        Convert convert = CONVERT_MAP.get(fClazz);
        if (convert == null) {
            return false;
        }
        return convert.convertObj(method, beInject, getV);
    }

    /**
     * 对 Array 进行注入<br>
     * @param componentType 被注入的对象类型
     * @param field 被注入类的对应被注入属性的 Field
     * @param beInject 被注入的类实例对象
     * @param rawPairValue 要注入的信息
     * @return
     */
    public static void injectObjs(Class<?> componentType, Field field, Object beInject, Object rawPairValue) {
        String[] pairValueArray = (String[]) rawPairValue;
        // 创建空的 Array 对象
        Object newArray = Array.newInstance(componentType, pairValueArray.length);
        CONVERT_MAP.get(componentType).convertObjs(newArray, pairValueArray);
        try {
            field.set(beInject, newArray);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static boolean isPrimitive(Class<?> clazz) {
        return CLASS_SET.contains(clazz);
    }

    public static boolean register(Class<?> clazz, Convert converter) {
        if (CLASS_SET.contains(clazz) || CONVERT_MAP.containsKey(clazz)) {
            return false;
        }
        CLASS_SET.add(clazz);
        CONVERT_MAP.put(clazz, converter);
        return true;
    }

}

package com.vaxtomis.yaml4test.converter;

import java.lang.reflect.Method;

public interface Convert {
    /**
     * 单个实例的注入，优先尝试使用 Setter 方式去注入
     */
    boolean convertObj(Method method, Object beInject, String getV);

    /**
     * 对 Array 进行注入
     */
    void convertObjs(Object newArray, String[] pairValueArray);
}

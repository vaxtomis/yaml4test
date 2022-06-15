package com.vaxtomis.yaml4test.converter;

/**
 * Custom conversion interface for adding custom conversion methods (String to class instance).
 * <br>
 * 自定义转换接口，用于添加自定义的转换方法（String 转 类实例）。
 * @author vaxtomis
 */
public interface CustomConvert<T> {
    T customConvert(String getV);
}

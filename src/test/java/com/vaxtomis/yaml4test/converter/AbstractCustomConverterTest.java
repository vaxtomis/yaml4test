package com.vaxtomis.yaml4test.converter;

import com.vaxtomis.yaml4test.annotation.Yaml4test;
import com.vaxtomis.yaml4test.annotation.YamlInject;
import com.vaxtomis.yaml4test.YamlFactory;
import org.junit.Test;

/**
 * @author vaxtomis
 */
@Yaml4test(Path = "customizeConverterTest.yml")
public class AbstractCustomConverterTest {
    @YamlInject
    private Address address;
    {
        // 注册自定义的转换器，将字符串转换为对应类型的对象
        ConverterRegister.register(CustomPosition.class, new PositionConverter());
        YamlFactory.refreshFactory(this);
    }
    @Test
    public void test() {
        System.out.println(address.position.toString());
    }
}

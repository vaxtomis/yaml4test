package com.vaxtomis.yaml4test.Converter;

import com.vaxtomis.yaml4test.Annotation.Yaml4test;
import com.vaxtomis.yaml4test.Annotation.YamlInject;
import com.vaxtomis.yaml4test.YamlFactory;
import org.junit.Test;

/**
 * @author vaxtomis
 */
@Yaml4test(Path = "customizeConverterTest.yml")
public class CustomConverterTest {
    @YamlInject
    private Address address;
    {
        // 注册自定义的转换器，将字符串转换为对应类型的对象
        PositionConverter converter = new PositionConverter();
        ConverterRegister.register(CustomizePosition.class, converter);
        YamlFactory.refreshFactory(this);
    }
    @Test
    public void test() {
        System.out.println(address.position.toString());
    }
}

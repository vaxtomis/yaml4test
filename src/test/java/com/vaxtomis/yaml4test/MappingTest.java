package com.vaxtomis.yaml4test;

import com.vaxtomis.yaml4test.annotation.Yaml4test;
import com.vaxtomis.yaml4test.annotation.YamlInject;
import com.vaxtomis.yaml4test.TestPojo.classA;
import com.vaxtomis.yaml4test.TestPojo.classB;
import org.junit.Test;

/**
 * @author vaxtomis
 */
@Yaml4test(Path = "test.yml")
public class MappingTest {
    @YamlInject
    private classA A1;
    @YamlInject(Name = "A2", Scope = YamlInject.Scope.Prototype)
    private classA aaa2;

    {
        YamlFactory.refreshFactory(this);
    }
    @Test
    public void MappingTest() {
        classA A3 = (classA) YamlFactory.getBean("A3");
        System.out.println(A1);
        System.out.println(aaa2);
        System.out.println(A3);
        classA A4 = (classA) YamlFactory.getBean("A1");
        System.out.println(A1 == A4);
    }

    @Test
    public void MappingTest2() {
        classB B = (classB) YamlFactory.getBean("B");
        classA A4 = (classA) YamlFactory.getBean("A1");
        System.out.println(B);
        System.out.println(A4);
    }
}

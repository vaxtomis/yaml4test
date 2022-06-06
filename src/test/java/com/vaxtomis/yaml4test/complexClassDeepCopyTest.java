package com.vaxtomis.yaml4test;

import com.vaxtomis.yaml4test.annotation.Yaml4test;
import com.vaxtomis.yaml4test.annotation.YamlInject;
import com.vaxtomis.yaml4test.TestPojo2.classE;
import org.junit.Test;

/**
 * @author vaxtomis
 */
@Yaml4test(Path = "complexClassDeepCopyTest.yml")
public class complexClassDeepCopyTest {
    @YamlInject(Scope = YamlInject.Scope.Prototype)
    private classE e;

    {
        YamlFactory.refreshFactory(this);
    }

    @Test
    public void deepCopyTest() {
        System.out.println(e);
    }
}

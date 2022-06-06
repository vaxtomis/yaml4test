package com.vaxtomis.yaml4test;

import com.vaxtomis.yaml4test.TestPojo2.classF;
import com.vaxtomis.yaml4test.annotation.Yaml4test;
import com.vaxtomis.yaml4test.annotation.YamlInject;
import com.vaxtomis.yaml4test.TestPojo.classC;
import com.vaxtomis.yaml4test.TestPojo.classD;
import org.junit.Test;

import java.util.Arrays;
/**
 * @author vaxtomis
 */
@Yaml4test(Path = "test2.yml")
public class SequenceTest {
    @YamlInject
    private classD D;
    @YamlInject(Scope = YamlInject.Scope.Prototype)
    private classC[] C;
    @YamlInject(Scope = YamlInject.Scope.Prototype)
    private classD[] E;
    @YamlInject
    classF F;

    {
        YamlFactory.refreshFactory(this);
    }
    @Test
    public void SequenceTest() {
        System.out.println(D);
        System.out.println(Arrays.toString(C));
        System.out.println(Arrays.toString(E));
        System.out.println(F);
    }
}

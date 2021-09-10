package com.vaxtomis.yaml4test;

import com.vaxtomis.yaml4test.Annotation.Yaml4test;
import com.vaxtomis.yaml4test.Annotation.YamlInject;
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
    @YamlInject
    private classC[] C;
    @YamlInject
    private classD[] E;
    {
        YamlFactory.refreshFactory(this);
    }
    @Test
    public void SequenceTest() {
        System.out.println(D);
        System.out.println(Arrays.toString(C));
        System.out.println(Arrays.toString(E));
    }
}

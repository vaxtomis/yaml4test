import Annotation.Yaml4test;
import Annotation.YamlInject;
import TestPojo.classA;
import org.junit.Test;

/**
 * @author vaxtomis
 */
@Yaml4test(Path = "test/test.yml")
public class MappingTest {
    @YamlInject
    private classA A1;
    @YamlInject(Name = "A2")
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
}

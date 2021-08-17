import Producer.classA;
import annotation.Yaml4test;
import annotation.YamlInject;
import org.junit.Test;

/**
 * @author vaxtomis
 */
@Yaml4test(Path = "test/test.yml")
public class YamlFactoryTest {
    @YamlInject
    private classA A1;

    @Test
    public void YamlFactoryTest() {
        YamlFactory.refreshFactory(this);
        System.out.println(A1);
        System.out.println(A1.getB());
        System.out.println(A1.getB().getSuba());
    }
}

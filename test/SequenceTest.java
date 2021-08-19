import Annotation.Yaml4test;
import Annotation.YamlInject;
import TestPojo.classC;
import TestPojo.classD;
import org.junit.Test;

/**
 * @author vaxtomis
 */
@Yaml4test(Path = "test/test2.yml")
public class SequenceTest {
    @YamlInject
    private classD D;
    @YamlInject
    private classC[] C;
    @Test
    public void SequenceTest() {
        YamlFactory.refreshFactory(this);

    }
}
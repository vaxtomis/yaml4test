package Producer;

import Parser.Parser;
import TestPojo.classA;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author vaxtomis
 */
public class ProducerTest {
    Parser parser = new Parser();
    Producer producer = new Producer();
    Map<String, ?> map = new HashMap<>();
    @Test
    public void producerTest() throws ClassNotFoundException {
        long start=System.currentTimeMillis();
        parser.setPath("test/test.yml");
        producer.setInnerMap(map);
        producer.setEvents(parser.getEventList());
        producer.build();
        long end=System.currentTimeMillis();
        System.out.println("程序运行时间： "+(end-start)+"ms");
        classA A1 = (classA) map.get("A1");
        classA A2 = (classA) map.get("A2");
        classA A3 = (classA) map.get("A3");
        classA A4 = (classA) map.get("A4");
        classA A5 = (classA) map.get("A5");
        classA A6 = (classA) map.get("A6");
        System.out.println(A1.toString());
        System.out.println(A2.toString());
        System.out.println(A3.toString());
        System.out.println(A4.toString());
        System.out.println(A5.toString());
        System.out.println(A6.toString());
    }
}

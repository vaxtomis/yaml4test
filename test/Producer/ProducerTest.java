package Producer;

import Parser.*;
import org.junit.Test;

import java.util.LinkedList;
import java.util.Map;

/**
 * @author vaxtomis
 */
public class ProducerTest {
    @Test
    public void producerTest() {
        long start=System.currentTimeMillis();
        Parser parser = new Parser("test/test.yml");
        parser.loopProcessing();
        LinkedList<Event> events = parser.getEventList();
        Producer producer = new Producer(events);
        producer.build();
        System.gc();
        long end=System.currentTimeMillis();
        System.out.println("程序运行时间： "+(end-start)+"ms");
        Map map = producer.getInnerMap();
        classA a = (classA) map.get("A1");
        System.out.println(a.toString());
    }
}

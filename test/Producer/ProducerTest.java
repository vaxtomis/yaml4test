package Producer;

import Parser.*;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author vaxtomis
 */
public class ProducerTest {
    public static void main(String[] args) {
        Parser parser = new Parser("test/test.yml");
        parser.loopProcessing();
        LinkedList<Event> events = parser.getEventList();
        Producer producer = new Producer(events);
        producer.build();
        Map map = producer.getInnerMap();
        classA a = (classA) map.get("A");
        //System.out.println(a.toString());
        System.out.println(a.getC());
        System.out.println(a.getD());
        System.out.println(a.getB().getSuba());
        System.out.println(a.getB().getSubb());
    }
}

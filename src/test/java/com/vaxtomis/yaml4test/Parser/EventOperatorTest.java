package com.vaxtomis.yaml4test.Parser;

import com.vaxtomis.yaml4test.Producer.Producer;
import com.vaxtomis.yaml4test.TestPojo.classD;
import com.vaxtomis.yaml4test.YamlFactory;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author vaxtomis
 */
public class EventOperatorTest {
    @Test
    public void EventOperatorTest01() {
        Parser parser = new Parser();
        String path = YamlFactory.class.getClassLoader().getResource("").getPath() + "test2.yml";
        parser.setPath(path);
        EventOperator operator = new EventOperator(parser.getEventList(), new HashMap());
        operator.rebuild();
    }

    @Test
    public void EventOperatorTest02() {
        Parser parser = new Parser();
        String path = YamlFactory.class.getClassLoader().getResource("").getPath() + "test2.yml";
        parser.setPath(path);
        EventOperator operator = new EventOperator(parser.getEventList(), "D.cs[0].E", "modified-E1");
        operator.rebuild();

        Map<String, ?> map = new HashMap<>();
        Producer producer = new Producer();
        producer.setEvents(parser.getEventList());
        producer.setClassPath(YamlFactory.class.getPackage().getName() + ".");
        producer.setInnerMap(map);
        producer.build();

        classD D = (classD) map.get("D");
        System.out.println(D.getCs()[0].getE());
    }

    @Test
    public void EventOperatorTest03() {
        Parser parser = new Parser();
        String path = YamlFactory.class.getClassLoader().getResource("").getPath() + "test2.yml";
        parser.setPath(path);
        EventOperator operator = new EventOperator(parser.getEventList());
        ArrayList<String> array = new ArrayList<>();
        array.add("D.cs[0].E");
        for (Event event : operator.getCutEvents(array)) {
            System.out.println(event.toString());
        }
        /*for (Event event : parser.getEventList()) {
            System.out.println(event.toString());
        }*/

    }
}

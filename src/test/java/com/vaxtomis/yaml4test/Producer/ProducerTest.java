package com.vaxtomis.yaml4test.Producer;

import com.vaxtomis.yaml4test.Parser.Parser;
import com.vaxtomis.yaml4test.TestPojo.classA;

import com.vaxtomis.yaml4test.YamlFactory;
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
        parser.setPath(getClass().getClassLoader().getResource("").getPath() + "test.yml");
        producer.setClassPath(YamlFactory.class.getPackage().getName() + ".");
        producer.setInnerMap(map);
        producer.setEvents(parser.getEventList());
        producer.build();
        long end=System.currentTimeMillis();
        System.out.println("Running time： "+(end-start)+"ms");
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

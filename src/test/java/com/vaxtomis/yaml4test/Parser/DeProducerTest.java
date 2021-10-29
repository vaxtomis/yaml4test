package com.vaxtomis.yaml4test.Parser;

import com.vaxtomis.yaml4test.TestPojo.classC;
import com.vaxtomis.yaml4test.TestPojo.classD;
import com.vaxtomis.yaml4test.YamlFactory;
import org.junit.Test;

/**
 * @author vaxtomis
 */

public class DeProducerTest {
    classC c1 = new classC();
    classC c2 = new classC();
    classD d1 = new classD();
    int[] ints = {1,2,3};
    int[] ints2 = {4,5,6};
    int[] ints3 = {7,8,9};
    classC[] cs = {c1,c2};
    {
        c1.setE("c1-e");
        c1.setF("c1-f");
        c1.setG(ints);

        c2.setE("c2-e");
        c2.setF("c2-f");
        c2.setG(ints2);

        d1.setCs(cs);
        d1.setD(11);
        d1.setE(ints3);
    }

    @Test
    public void DeParserTest01() throws IllegalAccessException {
        DeProducer deProducer = new DeProducer();
        deProducer.parseToEvents(c1, c1.getClass());
        for(Event e : deProducer.getEventList()) {
            System.out.println(e.toString());
        }
    }

    @Test
    public void DeParserTest02() throws IllegalAccessException {
        DeProducer deProducer = new DeProducer();
        deProducer.parseToEvents(d1, d1.getClass());
        for(Event e : deProducer.getEventList()) {
            System.out.println(e.toString());
        }
    }

    @Test
    public void DeParserTest03() throws IllegalAccessException {
        DeProducer deProducer = new DeProducer();
        deProducer.parseToEvents(cs, cs.getClass());
        for(Event e : deProducer.getEventList()) {
            System.out.println(e.toString());
        }
    }

    @Test
    public void parserComparision() {
        Parser parser = new Parser();
        String path = YamlFactory.class.getClassLoader().getResource("").getPath() + "deParserTest.yml";
        parser.setPath(path);
        for(Event e : parser.getEventList()) {
            System.out.println(e.toString());
        }
    }

    @Test
    public void sequenceComparision() {
        Parser parser = new Parser();
        String path = YamlFactory.class
                .getClassLoader()
                .getResource("")
                .getPath() + "deParserTestSequence.yml";
        parser.setPath(path);
        for(Event e : parser.getEventList()) {
            System.out.println(e.toString());
        }
    }
}

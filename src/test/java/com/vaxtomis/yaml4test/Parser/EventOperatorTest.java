package com.vaxtomis.yaml4test.Parser;

import com.vaxtomis.yaml4test.Producer.Producer;
import com.vaxtomis.yaml4test.TestPojo.classD;
import com.vaxtomis.yaml4test.YamlFactory;
import org.junit.Test;

import java.util.*;

/**
 * @author vaxtomis
 */
public class EventOperatorTest {
    /**
     * 测试 EventOperator 对 EventList 对应属性名的遍历。
     * 对应的 System.out.println() 被注释，在 EventOperator 的 batchModify
     * 和 singleModify 中取消注释。
     */
    @Test
    public void EventOperatorTest01() {
        Parser parser = new Parser();
        String path = YamlFactory.class.getClassLoader().getResource("").getPath() + "test2.yml";
        parser.setPath(path);
        EventOperator operator = new EventOperator(parser.getEventList(), new HashMap());
        operator.rebuild();
    }

    /**
     * 测试修改后的 EventList 能否正常生成类实例。
     */
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

    /**
     * 测试 EventList 切分方法。
     */
    @Test
    public void getCutEventsTest() {
        Parser parser = new Parser();
        String path = YamlFactory.class.getClassLoader().getResource("").getPath() + "test2.yml";
        parser.setPath(path);
        EventOperator operator = new EventOperator(parser.getEventList(), "D.cs[0].E", "modified-E1");
        for (Event event : operator.cutEvents()) {
            System.out.println(event.toString());
        }
    }
}

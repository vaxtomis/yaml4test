package com.vaxtomis.yaml4test.Parser;

import org.junit.Test;

import java.util.Arrays;

/**
 * @date 2021/10/15
 */
public class ModifyCollectorTest {
    ModifyCollector collector = new ModifyCollector();

    @Test
    public void generateGroupTest() {
        collector.add("A", "A-1");
        collector.add("A", "A-2");
        collector.add("A", "A-3");
        collector.add("B", "B-1");
        collector.add("B", "B-2");
        collector.add("C", "C-1");
        System.out.println(Arrays.toString(collector.getNames()));
    }
}

package com.vaxtomis.yaml4test.parser;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author vaxtomis
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
        String[][] matrix = collector.generateModifyMatrix();
        for (String[] line : matrix) {
            for (String block : line) {
                System.out.print(block + " ");
            }
            System.out.println();
        }
        for (List list : collector.generateGroup()) {
            System.out.println(list.toString());
        }
    }
}

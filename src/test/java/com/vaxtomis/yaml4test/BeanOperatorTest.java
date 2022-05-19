package com.vaxtomis.yaml4test;

import com.vaxtomis.yaml4test.Parser.ModifyCollector;
import com.vaxtomis.yaml4test.TestPojo.classC;
import com.vaxtomis.yaml4test.TestPojo.classD;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author vaxtomis
 */
public class BeanOperatorTest {
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
    public void DeepCopyTest01() throws IllegalAccessException {
        classC c2 = BeanOperator.deepCopy(c1);
        System.out.println("This is c1: " + c1);
        System.out.println("This is c2 copy by c1: " + c2);
        System.out.println("c1 == c2: " + (c1 == c2));
    }

    @Test
    public void DeepCopyTest02() throws IllegalAccessException {
        classD d2 = BeanOperator.deepCopy(d1);
        System.out.println("This is d1: " + d1);
        System.out.println("This is d2 copy by d1: " + d2);
        System.out.println("d1 == d2: " + (d1 == d2));
        System.out.println("d1.cs[0] == d2.cs[0]: " + (d1.getCs()[0] == d2.getCs()[0]));
    }

    @Test
    public void modifyCopyTest01() throws IllegalAccessException {
        classC mc1 = BeanOperator.modifyCopy(c1, "E", "modified-E");
        classC mc2 = BeanOperator.modifyCopy(c1, "G[0]", "4");
        classC[] mcs = BeanOperator.modifyCopy(cs, "[0].F", "modified-F");
        System.out.println(c1);
        System.out.println(mc1);
        System.out.println(mc2);
        System.out.println(Arrays.asList(cs));;
        System.out.println(Arrays.asList(mcs));
    }

    @Test
    public void modifyCopyTest02() throws IllegalAccessException {
        ModifyCollector collector = new ModifyCollector();
        collector.add("E", "modified-E");
        collector.add("G[0]", "4");
        classC mc1 = BeanOperator.modifyCopy(c1, collector);
        System.out.println(c1);
        System.out.println(mc1);
    }

    @Test
    public void createModifyGroupTest() throws IllegalAccessException {
        ModifyCollector collector = new ModifyCollector();
        collector.add("E", "M-E-1");
        collector.add("E", "M-E-2");
        collector.add("E", "M-E-3");
        collector.add("E", "M-E-4");
        collector.add("E", "M-E-5");
        collector.add("E", "M-E-6");
        collector.add("E", "M-E-7");
        collector.add("E", "M-E-8");
        collector.add("E", "M-E-9");

        collector.add("F", "M-F-1");
        collector.add("F", "M-F-2");
        collector.add("F", "M-F-3");
        collector.add("F", "M-F-4");
        collector.add("F", "M-F-5");
        collector.add("F", "M-F-6");
        collector.add("F", "M-F-7");
        collector.add("F", "M-F-8");
        collector.add("F", "M-F-9");
        collector.add("F", "M-F-10");

        collector.add("G[0]", "44");
        collector.add("G[0]", "444");
        collector.add("G[0]", "4444");
        collector.add("G[0]", "44444");
        collector.add("G[0]", "444444");
        collector.add("G[0]", "4444444");

        collector.add("G[1]", "55");
        collector.add("G[1]", "555");
        collector.add("G[1]", "5555");
        collector.add("G[1]", "55555");
        collector.add("G[1]", "555555");
        collector.add("G[1]", "5555555");
        collector.add("G[1]", "55555555");
        collector.add("G[1]", "555555555");

        collector.add("G[2]", "66");
        collector.add("G[2]", "666");
        collector.add("G[2]", "6666");
        collector.add("G[2]", "66666");
        collector.add("G[2]", "666666");
        collector.add("G[2]", "6666666");
        collector.add("G[2]", "66666666");
        collector.add("G[2]", "666666666");

        List list = BeanOperator.createModifiedGroup(c1, collector);
        for (Object obj : list) {
            System.out.println(obj);
        }
        System.out.println(list.size());
    }
}

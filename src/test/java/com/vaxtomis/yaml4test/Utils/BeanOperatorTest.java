package com.vaxtomis.yaml4test.Utils;

import com.vaxtomis.yaml4test.TestPojo.classC;
import com.vaxtomis.yaml4test.TestPojo.classD;
import org.junit.Test;

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
}

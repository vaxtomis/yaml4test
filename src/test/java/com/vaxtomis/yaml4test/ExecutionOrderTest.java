package com.vaxtomis.yaml4test;

import org.junit.*;

/**
 * @author vaxtomis
 */
public class ExecutionOrderTest {
    static {
        System.out.println("1. 静态方法块");
    }
    {
        System.out.println("3. 方法块");
    }

    @BeforeClass
    public static void beforeClass() {
        System.out.println("2. beforeClass");
    }

    @Before
    public void before() {
        System.out.println("4. before");
    }

    @Test
    public void test1() {
        System.out.println("5. test-1");
    }

    @Test
    public void test2() {
        System.out.println("5. test-2");
    }

    @After
    public void after() {
        System.out.println("6. after");
    }

    @AfterClass
    public static void afterClass() {
        System.out.println("7. afterClass");
    }
}

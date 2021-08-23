package com.vaxtomis.yaml4test.TestPojo;

import java.math.BigDecimal;

public class classA {
    private classB B;
    private int C;
    private BigDecimal D;

    public classB getB() {
        return B;
    }

    public void setB(classB b) {
        B = b;
    }

    public int getC() {
        return C;
    }

    public void setC(int c) {
        C = c;
    }

    public BigDecimal getD() {
        return D;
    }

    public void setD(BigDecimal d) {
        D = d;
    }

    @Override
    public String toString() {
        return "TestPojo.classA{" +
                "B=" + B +
                ", C='" + C + '\'' +
                ", D='" + D + '\'' +
                '}';
    }
}

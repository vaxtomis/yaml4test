package com.vaxtomis.yaml4test.TestPojo;

import java.math.BigDecimal;
import java.math.BigInteger;

public class classA {
    private classB B;
    private BigInteger C;
    private BigDecimal D;

    public classB getB() {
        return B;
    }

    public void setB(classB b) {
        B = b;
    }

    public BigInteger getC() {
        return C;
    }

    public void setC(BigInteger c) {
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

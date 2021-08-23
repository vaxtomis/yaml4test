package com.vaxtomis.yaml4test.TestPojo;

import java.util.Arrays;

public class classD {
    private classC[] cs;
    private int d;
    private int[] e;

    public void setD(int d) {
        this.d = d;
    }

    public classC[] getCs() {
        return cs;
    }

    public int getD() {
        return d;
    }

    public int[] getE() {
        return e;
    }

    @Override
    public String toString() {
        return "classD{" +
                "cs=" + Arrays.toString(cs) +
                ", d=" + d +
                ", e=" + Arrays.toString(e) +
                '}';
    }
}

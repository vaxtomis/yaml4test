package com.vaxtomis.yaml4test.TestPojo2;

import java.util.Arrays;

/**
 * @author vaxtomis
 */
public class classF {
    String name;
    Long[] longs;
    boolean bool;
    int fi = 6;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long[] getLongs() {
        return longs;
    }

    public void setLongs(Long[] longs) {
        this.longs = longs;
    }

    public boolean isBool() {
        return bool;
    }

    public void setBool(boolean bool) {
        this.bool = bool;
    }

    public int getFi() {
        return fi;
    }

    public void setFi(int fi) {
        this.fi = fi;
    }

    @Override
    public String toString() {
        return "classF{" +
                "name='" + name + '\'' +
                ", longs=" + Arrays.toString(longs) +
                ", bool=" + bool +
                ", fi=" + fi +
                '}';
    }
}

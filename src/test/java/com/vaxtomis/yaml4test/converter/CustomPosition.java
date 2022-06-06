package com.vaxtomis.yaml4test.converter;

/**
 * @author vaxtomis
 */
public class CustomPosition {
    Double x;
    Double y;

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "经纬度 {" +
                "经度为：" + x +
                ", 纬度为：" + y +
                '}';
    }
}

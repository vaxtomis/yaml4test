package com.vaxtomis.yaml4test.converter;

/**
 * @author vaxtomis
 */
public class Address {
    String name;
    String owner;
    CustomPosition position;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public CustomPosition getPosition() {
        return position;
    }

    public void setPosition(CustomPosition position) {
        this.position = position;
    }
}

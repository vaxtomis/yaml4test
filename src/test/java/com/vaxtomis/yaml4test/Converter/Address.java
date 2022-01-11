package com.vaxtomis.yaml4test.Converter;

/**
 * @author vaxtomis
 */
public class Address {
    String name;
    String owner;
    CustomizePosition position;

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

    public CustomizePosition getPosition() {
        return position;
    }

    public void setPosition(CustomizePosition position) {
        this.position = position;
    }
}

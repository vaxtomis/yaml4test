package com.vaxtomis.yaml4test.Producer;
import java.util.Objects;

/**
 * @description Unassigned key-value pair.
 * 未分配的键值对
 * @author vaxtomis
 */
public class RawPair<T> {
    private final String name;
    private T value;

    public RawPair(String name) {
        this.name = name;
        this.value = null;
    }

    public void setValue(Object value) {
        this.value = (T) value;
    }


    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RawPair<?> rawPair = (RawPair<?>) o;
        return name.equals(rawPair.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

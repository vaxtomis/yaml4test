package Producer;

import java.util.Objects;

/**
 * @description Unassigned key-value pair.
 * @author vaxtomis
 */
public class RawPair<T> {
    private String name;
    private T value;

    public RawPair(String name) {
        this.name = name;
        this.value = null;
    }

    public boolean isComplete() {
        return value != null;
    }

    public void setValue(T value) {
        this.value = value;
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

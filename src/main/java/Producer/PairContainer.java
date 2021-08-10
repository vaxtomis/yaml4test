package Producer;

import java.util.LinkedList;
import java.util.WeakHashMap;

/**
 * @description Used to store key-value pairs for later injection.
 * @author vaxtomis
 */
public class PairContainer<T> {
    private WeakHashMap<String, RawPair<T>> cacheMap;
    private LinkedList<RawPair<T>> queue;

    public PairContainer() {
        this.cacheMap = new WeakHashMap<>();
        this.queue = new LinkedList<>();
    }

    public void add(RawPair<T> pair) {
        cacheMap.put(pair.getName(), pair);
        queue.push(pair);
    }

    public void clear() {
        queue.clear();
    }

    public T getRawPairValue(String name) {
        return cacheMap.get(name).getValue();
    }

    public RawPair<T> getLatest() {
        return queue.poll();
    }
}

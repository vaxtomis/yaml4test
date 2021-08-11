package Producer;

import java.util.LinkedList;
import java.util.WeakHashMap;

/**
 * @description Used to store key-value pairs for later injection.
 * @author vaxtomis
 */
public class PairContainer {
    private WeakHashMap<String, RawPair<?>> cacheMap;
    private LinkedList<RawPair<?>> queue;

    public PairContainer() {
        this.cacheMap = new WeakHashMap<>();
        this.queue = new LinkedList<>();
    }

    public void add(RawPair<?> pair) {
        cacheMap.put(pair.getName(), pair);
        queue.add(pair);
    }

    public void clear() {
        queue.clear();
    }

    public <T> T getRawPairValue(String name) {
        RawPair<?> rawPair = cacheMap.get(name);
        return (T)rawPair.getValue();
    }

    public RawPair<?> pollLast() {
        return queue.pollLast();
    }

    public int size() {
        return queue.size();
    }

    public RawPair<?> getLast() {
        return queue.getLast();
    }
}
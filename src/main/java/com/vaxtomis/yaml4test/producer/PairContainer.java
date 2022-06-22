package com.vaxtomis.yaml4test.producer;

import java.util.LinkedList;
import java.util.WeakHashMap;

/**
 * <p>
 * Used to store key-value pairs for later injection.<br>
 * 存储 {@link RawPair} 的容器类。
 * </p>
 * @author vaxtomis
 */
public class PairContainer {
    private final WeakHashMap<String, RawPair<?>> cacheMap;
    private final LinkedList<RawPair<?>> queue;
    private int pairNo = 0;

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
        if (rawPair == null) {
            return null;
        }
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

    public int getPairNo() {
        return pairNo++;
    }
}

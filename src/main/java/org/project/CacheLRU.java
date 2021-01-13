package org.project;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class CacheLRU {

    Map<String, BufferedImage> cache;
    final int maxEntry;

    public CacheLRU(int maxEntry) {
        cache = new HashMap<>();
        this.maxEntry = maxEntry;
    }

    public boolean contains(String key) {
        return cache.containsKey(key);
    }

    private final Object lock = new Object();
    public void addNewImages(String key, BufferedImage bufferedImage) {
        synchronized (lock){
            if (cache.size() < maxEntry) {
                cache.put(key, bufferedImage);
            } else {
                String firstKey = cache.entrySet().iterator().next().getKey();
                cache.remove(firstKey);
            }
        }
    }

    public BufferedImage getBufferedImage(String key) {
        return cache.get(key);
    }

}

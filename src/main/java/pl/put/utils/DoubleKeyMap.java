package pl.put.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: Krystian Świdurski
 */
public class DoubleKeyMap<A, B, C> {

    private Map<A, Map<B, C>> map = new HashMap<>();

    public void put(A a, B b, C c) {
        Map<B, C> bcMap = map.get(a);
        if (bcMap == null) {
            bcMap = new HashMap<>();
            map.put(a, bcMap);
        }
        bcMap.put(b, c);
    }

    public boolean contains(A a, B b) {
        if (map.containsKey(a)) {
            Map<B, C> bcMap = map.get(a);
            if (bcMap == null) {
                return false;
            }
            return bcMap.containsKey(b);
        }
        return false;
    }

    public C get(A a, B b) {
        Map<B, C> bcMap = map.get(a);
        if (bcMap == null) {
            return null;
        }
        return bcMap.get(b);
    }

    public Map<B, C> get(A a) {
        return map.get(a);
    }
}

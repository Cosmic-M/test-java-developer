package model;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class Market {
    public static final Map<Integer, Integer> bids = new TreeMap<>(Comparator.reverseOrder());
    public static final Map<Integer, Integer> asks = new TreeMap<>();

    public static void clear() {
        bids.clear();
        asks.clear();
    }
}

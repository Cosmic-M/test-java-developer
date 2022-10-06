package model;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class Market {
    private static Market instance;
    public Map<Integer, Integer> bids;
    public Map<Integer, Integer> asks;

    private Market() {
        bids = new TreeMap<>(Comparator.reverseOrder());
        asks = new TreeMap<>();
    }

    public static Market getInstance() {
        if (instance == null) {
            instance = new Market();
        }
        return instance;
    }

    public static void clearMaps() {
        instance.bids.clear();
        instance.asks.clear();
    }
}

package model;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class Market {
    private final Map<Integer, Integer> bids = new TreeMap<>(Comparator.reverseOrder());
    private final Map<Integer, Integer> asks = new TreeMap<>();

    public TreeMap<Integer, Integer> getBids() {
        return (TreeMap<Integer, Integer>) bids;
    }

    public TreeMap<Integer, Integer> getAsks() {
        return (TreeMap<Integer, Integer>) asks;
    }
}

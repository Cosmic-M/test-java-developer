package model;

import java.util.NavigableMap;
import java.util.TreeMap;

public class OrderBook {
    private static OrderBook instance;
    public NavigableMap<Integer, Integer> orders;

    private OrderBook() {
        orders = new TreeMap<>();
    }

    public static OrderBook getInstance() {
        if (instance == null) {
            instance = new OrderBook();
        }
        return instance;
    }

    public static void clearOrderBook() {
        instance.orders.clear();
    }
}

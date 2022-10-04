package model;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class Market {
    private Map<BigInteger, BigInteger> bids = new TreeMap<>(Comparator.reverseOrder());
    private Map<BigInteger, BigInteger> asks = new TreeMap<>();

    public TreeMap<BigInteger, BigInteger> getBids() {
        return (TreeMap<BigInteger, BigInteger>) bids;
    }

    public void setBids(Map<BigInteger, BigInteger> bids) {
        this.bids = bids;
    }

    public TreeMap<BigInteger, BigInteger> getAsks() {
        return (TreeMap<BigInteger, BigInteger>) asks;
    }

    public void setAsks(Map<BigInteger, BigInteger> asks) {
        this.asks = asks;
    }
}

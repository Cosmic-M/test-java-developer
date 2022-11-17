package service.impl;

import java.util.Map;
import model.Market;
import service.ParseService;

public class ParseServiceImpl implements ParseService {
    private static final String NEW_LINE = "\n";
    private static final String COMMA = ",";
    private final StringBuilder result = new StringBuilder();

    @Override
    public void findBestAsk() {
        int bestAsk = Market.asks.keySet().stream().findFirst().orElseThrow(
                () -> new RuntimeException("Cannot introduce best ask because of there isn't "
                        + "any relevant goods to buy in the order book"));
        result.append(bestAsk)
                .append(COMMA)
                .append(Market.asks.get(bestAsk))
                .append(NEW_LINE);
    }

    @Override
    public void findBestBid() {
        int bestBid = Market.bids.keySet().stream().findFirst().orElseThrow(
                () -> new RuntimeException("Cannot introduce best bid because of there isn't "
                        + "any relevant goods to sell in the order book"));
        result.append(bestBid)
                .append(COMMA)
                .append(Market.bids.get(bestBid))
                .append(NEW_LINE);
    }

    @Override
    public void updateBids(int price, int size) {
        if (size == 0) {
            Market.bids.remove(price);
            return;
        }
        for (Map.Entry<Integer, Integer> entry : Market.asks.entrySet()) {
            if (price >= entry.getKey()) {
                if (size == entry.getValue()) {
                    Market.asks.remove(entry.getKey());
                    return;
                } else if (size < entry.getValue()) {
                    Market.asks.put(entry.getKey(), entry.getValue() - size);
                    return;
                } else {
                    size -= entry.getValue();
                    Market.asks.remove(entry.getKey());
                    updateBids(price, size);
                    return;
                }
            } else {
                Market.bids.put(price, size);
                return;
            }
        }
        Market.bids.put(price, size);
    }

    @Override
    public void updateAsks(int price, int size) {
        if (size == 0) {
            Market.asks.remove(price);
            return;
        }
        for (Map.Entry<Integer, Integer> entry : Market.bids.entrySet()) {
            if (price <= entry.getKey()) {
                if (size == entry.getValue()) {
                    Market.bids.remove(entry.getKey());
                    return;
                } else if (size < entry.getValue()) {
                    Market.bids.put(entry.getKey(), entry.getValue() - size);
                    return;
                } else {
                    size -= entry.getValue();
                    Market.bids.remove(entry.getKey());
                    updateAsks(price, size);
                    return;
                }
            } else {
                Market.asks.put(price, size);
                return;
            }
        }
        Market.asks.put(price, size);
    }

    @Override
    public void findSizeByPrice(int price) {
        Integer quantity = Market.bids.get(price);
        if (quantity == null) {
            quantity = Market.asks.get(price);
            if (quantity == null) {
                result.append(0)
                        .append(NEW_LINE);
            } else {
                result.append(quantity)
                        .append(NEW_LINE);
            }
        } else {
            result.append(quantity)
                    .append(NEW_LINE);
        }
    }

    @Override
    public void removeFromAsks(int sizeToSubtract) {
        for (Map.Entry<Integer, Integer> entry : Market.asks.entrySet()) {
            if (sizeToSubtract == entry.getValue()) {
                Market.asks.remove(entry.getKey());
                return;
            } else if (sizeToSubtract < entry.getValue()) {
                Market.asks.put(entry.getKey(), entry.getValue() - sizeToSubtract);
                return;
            } else {
                sizeToSubtract -= entry.getValue();
                Market.asks.remove(entry.getKey());
                removeFromAsks(sizeToSubtract);
                return;
            }
        }
        throw new RuntimeException("Cannot operate remove operation with 'BUY', because "
                + "there isn't enough stored goods corresponds to request quantity!");
    }

    @Override
    public void removeFromBids(int sizeToSubtract) {
        for (Map.Entry<Integer, Integer> entry : Market.bids.entrySet()) {
            if (sizeToSubtract == entry.getValue()) {
                Market.bids.remove(entry.getKey());
                return;
            } else if (sizeToSubtract < entry.getValue()) {
                Market.bids.put(entry.getKey(), entry.getValue() - sizeToSubtract);
                return;
            } else {
                sizeToSubtract -= entry.getValue();
                Market.bids.remove(entry.getKey());
                removeFromBids(sizeToSubtract);
                return;
            }
        }
        throw new RuntimeException("Cannot operate remove operation with 'SELL', because "
                + "there isn't enough stored goods corresponds to request quantity!");
    }

    @Override
    public StringBuilder getResult() {
        return result;
    }
}

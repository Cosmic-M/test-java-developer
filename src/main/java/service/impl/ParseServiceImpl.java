package service.impl;

import java.util.Map;
import java.util.TreeMap;
import model.Market;
import service.ParseService;

public class ParseServiceImpl implements ParseService {
    private static final String COMMA = ",";
    private static final String SPLITERATOR = "\r\n";
    private static final Character START_WITH_B = 'b';
    private static final Character ENDS_WITH_D = 'd';
    private static final int EXECUTE_SYMBOLS = 2;
    private final Market market;

    public ParseServiceImpl(Market market) {
        this.market = market;
    }

    @Override
    public StringBuilder parse(StringBuilder operations) {
        StringBuilder result = new StringBuilder();
        boolean firstLine = true;
        while (operations.length() > EXECUTE_SYMBOLS) {
            char c = operations.charAt(0);
            operations.delete(0, 2);
            switch (c) {
                case 'u':
                    int splitIndex = operations.indexOf(COMMA);
                    int price = Integer.parseInt(operations.substring(0, splitIndex));
                    operations.delete(0, ++splitIndex);
                    splitIndex = operations.indexOf(COMMA);
                    int size = Integer.parseInt(operations.substring(0, splitIndex));
                    operations.delete(0, ++splitIndex);
                    if (operations.charAt(0) == START_WITH_B) {
                        updateBids(price, size);
                    } else {
                        updateAsks(price, size);
                    }
                    splitIndex = operations.indexOf(COMMA);
                    operations.delete(0, --splitIndex);
                    break;
                case 'q':
                    if (!firstLine) {
                        result.append(System.lineSeparator());
                    } else {
                        firstLine = false;
                    }
                    if (operations.charAt(0) == START_WITH_B) {
                        char endsWith = operations.charAt(7);
                        if (endsWith == ENDS_WITH_D) {
                            int bidKey = market.getBids().entrySet().stream()
                                    .filter(element -> element.getValue() > 0)
                                    .map(Map.Entry::getKey)
                                    .findFirst()
                                    .orElseThrow(
                                            () -> new RuntimeException("Cannot get element from bids. "
                                                    + "Looks like map has no bids with size > 0"));
                            result.append(bidKey)
                                    .append(",")
                                    .append(market.getBids().get(bidKey));
                        } else {
                            int askKey = market.getAsks().entrySet().stream()
                                    .filter(element -> element.getValue() > 0)
                                    .map(Map.Entry::getKey)
                                    .findFirst()
                                    .orElseThrow(
                                            () -> new RuntimeException("Cannot get element from asks. "
                                                    + "Looks like map has no asks with size > 0"));
                            result.append(askKey)
                                    .append(",")
                                    .append(market.getAsks().get(askKey));
                        }
                    } else {
                        splitIndex = operations.indexOf(COMMA);
                        operations.delete(0, ++splitIndex);
                        splitIndex = operations.indexOf(SPLITERATOR);
                        price = Integer.parseInt(operations.substring(0, splitIndex));
                        result.append(querySizeByPrice(price));
                    }
                    splitIndex = operations.indexOf(COMMA);
                    operations.delete(0, --splitIndex);
                    break;
                default:
                    splitIndex = operations.indexOf(COMMA);
                    int nextSplitIndex = operations.indexOf(SPLITERATOR);
                    int sizeToRemove = Integer.parseInt(operations.substring(++splitIndex, nextSplitIndex));
                    if (operations.charAt(0) == START_WITH_B) {
                        removeFromAsks(sizeToRemove);
                    } else {
                        removeFromBids(sizeToRemove);
                    }
                    splitIndex = operations.indexOf(COMMA, nextSplitIndex);
                    operations.delete(0, --splitIndex);
                    break;
            }
        }
        return result;
    }

    private void updateBids(int price, int size) {
        TreeMap<Integer, Integer> asks = market.getAsks();
        for (Map.Entry<Integer, Integer> entry : asks.entrySet()) {
            if (entry.getValue() < 0) {
                continue;
            }
            if (price >= entry.getKey()) {
                if (size >= 0) {
                    size -= (entry.getValue());
                    entry.setValue(0);
                } else {
                    entry.setValue(entry.getValue() - size);
                    return;
                }
            }
        }
        market.getBids().put(price, size);
    }

    private void updateAsks(int price, int size) {
        TreeMap<Integer, Integer> bids = market.getBids();
        for (Map.Entry<Integer, Integer> entry : bids.entrySet()) {
            if (entry.getValue() < 0) {
                continue;
            }
            if (price <= entry.getKey()) {
                if (size >= entry.getValue()) {
                    size -= entry.getValue();
                    entry.setValue(0);
                } else {
                    entry.setValue(entry.getValue() - size);
                    return;
                }
            }
        }
        market.getAsks().put(price, size);
    }

    private int querySizeByPrice(int price) {
        Integer quantity = market.getBids().get(price);
        if (quantity == null) {
            quantity = market.getAsks().get(price);
            if (quantity == null) {
                return 0;
            }
        }
        return quantity;
    }

    private void removeFromAsks(int sizeToSubtract) {
            TreeMap<Integer, Integer> asks = market.getAsks();
            for (Map.Entry<Integer, Integer> entry : asks.entrySet()) {
                if (entry.getValue() < 0) {
                    continue;
                }
                if (sizeToSubtract >= entry.getValue()) {
                    sizeToSubtract -= entry.getValue();
                    entry.setValue(0);
                } else {
                    entry.setValue(entry.getValue() - sizeToSubtract);
                    return;
                }
            }
            throw new RuntimeException("Cannot operate remove operation with 'BUY', because you "
                    + " have no enough stored goods corresponds to request quantity!");
        }

    private void removeFromBids(int sizeToSubtract) {
        TreeMap<Integer, Integer> bids = market.getBids();
        for (Map.Entry<Integer, Integer> entry : bids.entrySet()) {
            if (entry.getValue() < 0) {
                continue;
            }
            if (sizeToSubtract >= entry.getValue()) {
                sizeToSubtract -= entry.getValue();
                entry.setValue(0);
            } else {
                entry.setValue(entry.getValue() - sizeToSubtract);
                return;
            }
        }
        throw new RuntimeException("Cannot operate remove operation with 'SELL', because you "
                + "have no enough stored goods corresponds to request quantity!");
    }
}

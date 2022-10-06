package service.impl;

import java.util.Map;
import model.Market;
import service.ParseService;

public class ParseServiceImpl implements ParseService {
    private static final String COMMA = ",";
    private static final Character START_WITH_B = 'b';
    private static final Character ENDS_WITH_D = 'd';
    private static final int EXECUTE_SYMBOLS = 2;
    private static final int BEST_OPERATION_STRING_SIZE = 7;
    private final Market market = Market.getInstance();


    @Override
    public StringBuilder parse(StringBuilder operations) {
        StringBuilder result = new StringBuilder();
        boolean firstLine = true;
        while (operations.length() > EXECUTE_SYMBOLS) {
            switch (operations.charAt(0)) {
                case 'u':
                    operations.delete(0, 2);
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
                    operations.delete(0, 2);
                    if (!firstLine) {
                        result.append("\n");
                    } else {
                        firstLine = false;
                    }
                    if (operations.charAt(0) == START_WITH_B) {
                        char endsWith = operations.charAt(BEST_OPERATION_STRING_SIZE);
                        if (endsWith == ENDS_WITH_D) {
                            int bidKey = market.bids.entrySet().stream()
                                    .filter(element -> element.getValue() > 0)
                                    .map(Map.Entry::getKey)
                                    .findFirst()
                                    .orElseThrow(
                                            () -> new RuntimeException("Cannot get element from bids. "
                                                    + "Looks like map has no bids with size > 0"));
                            result.append(bidKey)
                                    .append(",")
                                    .append(market.bids.get(bidKey));
                        } else {
                            int askKey = market.asks.entrySet().stream()
                                    .filter(element -> element.getValue() > 0)
                                    .map(Map.Entry::getKey)
                                    .findFirst()
                                    .orElseThrow(
                                            () -> new RuntimeException("Cannot get element from asks. "
                                                    + "Looks like map has no asks with size > 0"));
                            result.append(askKey)
                                    .append(",")
                                    .append(market.asks.get(askKey));
                        }
                    } else {
                        splitIndex = operations.indexOf(COMMA);
                        operations.delete(0, ++splitIndex);
                        splitIndex = operations.indexOf(COMMA);
                        price = Integer.parseInt(operations.substring(0, --splitIndex));
                        result.append(querySizeByPrice(price));
                    }
                    splitIndex = operations.indexOf(COMMA);
                    operations.delete(0, --splitIndex);
                    break;
                default:
                    operations.delete(0, 2);
                    splitIndex = operations.indexOf(COMMA);
                    int nextSplitIndex = operations.indexOf(COMMA, splitIndex + 1);
                    int sizeToRemove = Integer.parseInt(operations.substring(++splitIndex, --nextSplitIndex));
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
        for (Map.Entry<Integer, Integer> entry : market.asks.entrySet()) {
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
        market.bids.put(price, size);
    }

    private void updateAsks(int price, int size) {
        for (Map.Entry<Integer, Integer> entry : market.bids.entrySet()) {
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
        market.asks.put(price, size);
    }

    private int querySizeByPrice(int price) {
        Integer quantity = market.bids.get(price);
        if (quantity == null) {
            quantity = market.asks.get(price);
            if (quantity == null) {
                return 0;
            }
        }
        return quantity;
    }

    private void removeFromAsks(int sizeToSubtract) {
            for (Map.Entry<Integer, Integer> entry : market.asks.entrySet()) {
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
        for (Map.Entry<Integer, Integer> entry : market.bids.entrySet()) {
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

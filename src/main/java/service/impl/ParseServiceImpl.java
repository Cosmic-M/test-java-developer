package service.impl;

import model.OrderBook;
import service.ParseService;

public class ParseServiceImpl implements ParseService {
    private static final String COMMA = ",";
    private static final Character START_WITH_B = 'b';
    private static final Character ENDS_WITH_D = 'd';
    private static final int EXECUTE_SYMBOLS = 2;
    private static final int BEST_OPERATION_STRING_SIZE = 7;
    private final OrderBook orderBook = OrderBook.getInstance();
    private int bestBid;
    private int bestAsk;

    @Override
    public StringBuilder parse(StringBuilder operations) {
        StringBuilder result = new StringBuilder();
        bestAsk = 0;
        bestBid = Integer.MAX_VALUE;
        orderBook.orders.put(bestAsk,0);
        orderBook.orders.put(bestBid,0);
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
                            result.append(bestBid)
                                    .append(",")
                                    .append(orderBook.orders.get(bestBid));
                        } else {
                            result.append(bestAsk)
                                    .append(",")
                                    .append(orderBook.orders.get(bestAsk));
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
                        removeFromBestAsks(sizeToRemove);
                    } else {
                        removeFromBestBids(sizeToRemove);
                    }
                    splitIndex = operations.indexOf(COMMA, nextSplitIndex);
                    operations.delete(0, --splitIndex);
                    break;
            }
        }
        return result;
    }

    private void updateBids(int price, int size) {
        if (price < bestAsk) {
            orderBook.orders.put(price, size);
            if (price > bestBid) {
                bestBid = price;
            }
            return;
        }
        do {
            if (orderBook.orders.get(bestAsk) >= size) {
                orderBook.orders.put(bestAsk, orderBook.orders.get(bestAsk) - size);
                updateBestAsk();
                return;
            } else {
                size -= orderBook.orders.get(bestAsk);
                orderBook.orders.put(bestAsk, 0);
                do {
                    bestAsk = orderBook.orders.ceilingKey(++bestAsk);
                } while (orderBook.orders.get(bestAsk) == 0 && bestAsk < price);
                if (price < bestAsk) {
                    orderBook.orders.put(price, size);
                    bestBid = price;
                    updateBestBid();
                    return;
                }
            }
        } while (size > 0);
    }

    private void updateBestBid() {
        if (orderBook.orders.get(bestBid) > 0 || bestBid == 0) {
            return;
        }
        int size;
        int price;
        do {
            size = orderBook.orders.floorEntry(--bestBid).getValue();
            price = orderBook.orders.floorEntry(bestBid).getKey();
            if (price == 0) {
                bestBid = Integer.MAX_VALUE;
                return;
            }
        }  while (size == 0);
        bestBid = orderBook.orders.floorEntry(bestBid).getKey();
    }

    private void updateAsks(int price, int size) {
        if (price > bestBid) {
            orderBook.orders.put(price, size);
            if (price < bestAsk) {
                bestAsk = price;
            }
            return;
        }
        do {
            if (orderBook.orders.get(bestBid) >= size) {
                orderBook.orders.put(bestBid, orderBook.orders.get(bestBid) - size);
                updateBestBid();
                return;
            } else {
                size -= orderBook.orders.get(bestBid);
                orderBook.orders.put(bestBid, 0);
                do {
                    bestBid = orderBook.orders.floorKey(--bestBid);
                } while (orderBook.orders.get(bestBid) == 0 && bestBid > price);
                if (price > bestBid) {
                    orderBook.orders.put(price, size);
                    bestAsk = price;
                    updateBestAsk();
                    return;
                }
            }
        } while (size > 0);
    }

    private void updateBestAsk() {
        if (orderBook.orders.get(bestAsk) > 0 || bestAsk == Integer.MAX_VALUE) {
            return;
        }
        int size;
        int price;
        do {
            size = orderBook.orders.ceilingEntry(++bestAsk).getValue();
            price = orderBook.orders.ceilingEntry(bestAsk).getKey();
            if (price == Integer.MAX_VALUE) {
                bestAsk = 0;
                return;
            }
        }  while (size == 0);
        bestAsk = orderBook.orders.ceilingEntry(bestAsk).getKey();
    }

    private int querySizeByPrice(int price) {
        return orderBook.orders.get(price) == null ? 0 : orderBook.orders.get(price);
    }

    private void removeFromBestAsks(int sizeToSubtract) {
        do {
            if (orderBook.orders.get(bestAsk) >= sizeToSubtract) {
                orderBook.orders.put(bestAsk, orderBook.orders.get(bestAsk) - sizeToSubtract);
                updateBestBid();
                updateBestAsk();
                return;
            } else {
                sizeToSubtract -= orderBook.orders.get(bestAsk);
                orderBook.orders.put(bestAsk, 0);
                updateBestAsk();
            }
        } while (sizeToSubtract > 0 && bestAsk < Integer.MAX_VALUE);
        throw new RuntimeException("Cannot operate remove operation with 'BUY', because you "
                + " have no enough stored goods corresponds to request quantity!");
    }

    private void removeFromBestBids(int sizeToSubtract) {
        do {
            if (orderBook.orders.get(bestBid) >= sizeToSubtract) {
                orderBook.orders.put(bestBid, orderBook.orders.get(bestBid) - sizeToSubtract);
                updateBestAsk();
                updateBestBid();
                return;
            } else {
                sizeToSubtract -= orderBook.orders.get(bestBid);
                orderBook.orders.put(bestBid, 0);
                updateBestBid();
            }
        } while (sizeToSubtract > 0 && bestBid > 0);
        throw new RuntimeException("Cannot operate remove operation with 'SELL', because you "
                + "have no enough stored goods corresponds to request quantity!");
    }
}

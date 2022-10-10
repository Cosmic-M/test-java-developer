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
    private static final char UPDATE_OPERATION = 'u';
    private static final char QUERY_OPERATION = 'q';
    private static final char SUBTRACT_OPERATION = 'o';
    private int splitIndex;
    private int price;
    private StringBuilder result;
    private int bestBid;
    private int bestAsk;
    private boolean firstLine;


    @Override
    public StringBuilder parse(StringBuilder operations) {
        result = new StringBuilder();
        bestAsk = Integer.MAX_VALUE;
        bestBid = 0;
        orderBook.orders.put(bestAsk, 0);
        orderBook.orders.put(bestBid, 0);
        firstLine = true;
        findFirstEntry(operations);
        while (operations.length() > EXECUTE_SYMBOLS) {
            if (bestBid == 0 && bestAsk == Integer.MAX_VALUE) {
                findFirstEntry(operations);
                if (operations.length() <= EXECUTE_SYMBOLS) {
                    return result;
                }
            }
            switch (operations.charAt(0)) {
                case UPDATE_OPERATION -> updateData(operations);
                case QUERY_OPERATION -> addQueryResultToQueryList(operations);
                case SUBTRACT_OPERATION -> subtractData(operations);
                default -> throw new RuntimeException("Symbol of operation doesn't match to "
                        + "relevant options. Please check input file and run app again");
            }
        }
        return result;
    }

    private void findFirstEntry(StringBuilder operations) {
        while (operations.length() > EXECUTE_SYMBOLS) {
            switch (operations.charAt(0)) {
                case UPDATE_OPERATION -> {
                    updateData(operations);
                    return;
                }
                case QUERY_OPERATION -> addQueryResultToQueryList(operations);
                default -> throw new RuntimeException("Exception in sequence of operations: "
                        + "there is no sense in 'SUBTRACT' operation if order`s "
                        + "book has no any goods available");
            }
        }
    }

    private void updateData(StringBuilder operations) {
        operations.delete(0, 2);
        splitIndex = operations.indexOf(COMMA);
        price = Integer.parseInt(operations.substring(0, splitIndex));
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
    }

    private void addQueryResultToQueryList(StringBuilder operations) {
        if (!firstLine) {
            result.append("\n");
        } else {
            firstLine = false;
        }
        operations.delete(0, 2);
        if (operations.charAt(0) == START_WITH_B) {
            char endsWith = operations.charAt(BEST_OPERATION_STRING_SIZE);
            if (endsWith == ENDS_WITH_D) {
                if (bestBid == 0) {
                    throw new RuntimeException("Cannot introduce best bid because of there isn't "
                    + "any relevant goods to sell in the order book");
                }
                result.append(bestBid)
                        .append(",")
                        .append(orderBook.orders.get(bestBid));
            } else {
                if (bestAsk == Integer.MAX_VALUE) {
                    throw new RuntimeException("Cannot introduce best ask because of there isn't "
                            + "any relevant goods to buy in the order book");
                }
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
    }

    private void subtractData(StringBuilder operations) {
        operations.delete(0, 2);
        splitIndex = operations.indexOf(COMMA);
        int nextSplitIndex = operations.indexOf(COMMA, splitIndex + 1);
        int sizeToRemove = Integer
                .parseInt(operations.substring(++splitIndex, --nextSplitIndex));
        if (operations.charAt(0) == START_WITH_B) {
            removeFromBestAsks(sizeToRemove);
        } else {
            removeFromBestBids(sizeToRemove);
        }
        splitIndex = operations.indexOf(COMMA, nextSplitIndex);
        operations.delete(0, --splitIndex);
    }

    private void updateBids(int price, int size) {
        do {
            if (price >= bestAsk) {
                if (size < orderBook.orders.get(bestAsk)) {
                    orderBook.orders.put(bestAsk, orderBook.orders.get(bestAsk) - size);
                    return;
                } else if (size == orderBook.orders.get(bestAsk)) {
                    orderBook.orders.put(bestAsk, 0);
                    updateBestBidAsk();
                    return;
                } else {
                    size -= orderBook.orders.get(bestAsk);
                    orderBook.orders.put(bestAsk, 0);
                    updateBestBidAsk();
                }
            } else {
                if (price >= bestBid) {
                    orderBook.orders.put(price, size);
                    bestBid = price;
                    return;
                }
                orderBook.orders.put(price, size);
                return;
            }
        } while (size > 0);
    }

    private void updateAsks(int price, int size) {
        do {
            if (price <= bestBid) {
                if (size < orderBook.orders.get(bestBid)) {
                    orderBook.orders.put(bestBid, orderBook.orders.get(bestBid) - size);
                    return;
                } else if (size == orderBook.orders.get(bestBid)) {
                    orderBook.orders.put(bestBid, 0);
                    updateBestBidAsk();
                    return;
                } else {
                    size -= orderBook.orders.get(bestBid);
                    orderBook.orders.put(bestBid, 0);
                    updateBestBidAsk();
                }
            } else {
                if (price <= bestAsk) {
                    orderBook.orders.put(price, size);
                    bestAsk = price;
                    return;
                }
                orderBook.orders.put(price, size);
                return;
            }
        } while (size > 0);
    }

    private void updateBestBidAsk() {
        if (bestBid > 0 && orderBook.orders.get(bestBid) == 0) {
            do {
                bestBid = orderBook.orders.floorKey(bestBid - 1);
                if (orderBook.orders.get(bestBid) > 0) {
                    break;
                }
            }  while (bestBid > 0);
        }
        if (bestAsk < Integer.MAX_VALUE && orderBook.orders.get(bestAsk) == 0) {
            do {
                bestAsk = orderBook.orders.ceilingKey(bestAsk + 1);
                if (orderBook.orders.get(bestAsk) > 0) {
                    break;
                }
            }  while (bestAsk < Integer.MAX_VALUE);
        }
    }

    private int querySizeByPrice(int price) {
        return orderBook.orders.get(price) == null ? 0 : orderBook.orders.get(price);
    }

    private void removeFromBestAsks(int sizeToRemove) {
        do {
            if (orderBook.orders.get(bestAsk) > sizeToRemove) {
                orderBook.orders.put(bestAsk, orderBook.orders.get(bestAsk) - sizeToRemove);
                return;
            } else if (orderBook.orders.get(bestAsk) == sizeToRemove) {
                orderBook.orders.put(bestAsk, 0);
                updateBestBidAsk();
                return;
            } else {
                sizeToRemove -= orderBook.orders.get(bestAsk);
                orderBook.orders.put(bestAsk, 0);
                updateBestBidAsk();
            }
        } while (sizeToRemove > 0);
        throw new RuntimeException("Cannot operate remove operation with 'BUY', because there "
                + "isn't enough stored goods corresponds to request quantity!");
    }

    private void removeFromBestBids(int sizeToRemove) {
        do {
            if (orderBook.orders.get(bestBid) > sizeToRemove) {
                orderBook.orders.put(bestBid, orderBook.orders.get(bestBid) - sizeToRemove);
                return;
            } else if (orderBook.orders.get(bestBid) == sizeToRemove) {
                orderBook.orders.put(bestBid, 0);
                updateBestBidAsk();
                return;
            } else {
                sizeToRemove -= orderBook.orders.get(bestBid);
                orderBook.orders.put(bestBid, 0);
                updateBestBidAsk();
            }
        } while (sizeToRemove > 0);
        throw new RuntimeException("Cannot operate remove operation with 'SELL', because there "
                + "isn't enough stored goods corresponds to request quantity!");
    }
}

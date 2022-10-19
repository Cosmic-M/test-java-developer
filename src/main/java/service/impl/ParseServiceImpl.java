package service.impl;

import model.OrderBook;
import service.ParseService;

public class ParseServiceImpl implements ParseService {
    private static final Character START_WITH_B = 'b';
    private static final Character ENDS_WITH_Y = 'y';
    private static final Character ENDS_WITH_D = 'd';
    private static final int BEST_OPERATION_STRING_SIZE = 9;
    private final OrderBook orderBook = OrderBook.getInstance();
    private int bestBid;
    private int bestAsk;
    private static final char UPDATE_OPERATION = 'u';
    private static final char QUERY_OPERATION = 'q';
    private static final char SUBTRACT_OPERATION = 'o';
    private final StringBuilder result;
    private boolean firstLine;

    public ParseServiceImpl() {
        bestBid = 0;
        bestAsk = Integer.MAX_VALUE;
        result = new StringBuilder();
        firstLine = true;
        orderBook.orders.put(bestBid, 0);
        orderBook.orders.put(bestAsk, 0);
    }

    @Override
    public void parse(StringBuilder operation) {
        switch (operation.charAt(0)) {
            case UPDATE_OPERATION -> updateData(operation);
            case QUERY_OPERATION -> addQueryResultToQueryList(operation);
            case SUBTRACT_OPERATION -> subtractData(operation);
        }
    }

    public void resetOrderBook() {
        OrderBook.clearOrderBook();
    }

    public StringBuilder getResult() {
        return result;
    }

    private void updateData(StringBuilder operation) {
        operation.reverse();
        int pow = 1;
        int size = 0;
        int price = 0;
        int index = 4;
        while (Character.isDigit(operation.charAt(index))) {
            size += Character.getNumericValue(operation.charAt(index++)) * pow;
            pow *= 10;
        }
        pow = 1;
        index++;
        while (Character.isDigit(operation.charAt(index))) {
            price += Character.getNumericValue(operation.charAt(index++)) * pow;
            pow *= 10;
        }
        if (operation.charAt(0) == ENDS_WITH_D) {
            updateBids(price, size);
        } else {
            updateAsks(price, size);
        }
    }

    private void addQueryResultToQueryList(StringBuilder operation) {
        if (!firstLine) {
            result.append("\n");
        } else {
            firstLine = false;
        }
        if (operation.charAt(2) == START_WITH_B) {
            char endsWith = operation.charAt(BEST_OPERATION_STRING_SIZE);
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
            operation.reverse();
            int pow = 1;
            int price = 0;
            int index = 0;
            while (Character.isDigit(operation.charAt(index))) {
                price += Character.getNumericValue(operation.charAt(index++)) * pow;
                pow *= 10;
            }
            result.append(querySizeByPrice(price));
        }
    }

    private void subtractData(StringBuilder operation) {
        operation.reverse();
        int pow = 1;
        int sizeToRemove = 0;
        int index = 0;
        while (Character.isDigit(operation.charAt(index))) {
            sizeToRemove += Character.getNumericValue(operation.charAt(index++)) * pow;
            pow *= 10;
        }
        if (operation.charAt(++index) == ENDS_WITH_Y) {
            removeFromBestAsks(sizeToRemove);
        } else {
            removeFromBestBids(sizeToRemove);
        }
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
                    updateBestBidAsk();
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
                    updateBestBidAsk();
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
        } while (sizeToRemove > 0 && bestAsk != Integer.MAX_VALUE);
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
        } while (sizeToRemove > 0 && bestBid != 0);
        throw new RuntimeException("Cannot operate remove operation with 'SELL', because there "
                + "isn't enough stored goods corresponds to request quantity!");
    }
}

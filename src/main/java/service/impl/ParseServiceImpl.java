package service.impl;

import model.OrderBook;

public class ParseServiceImpl implements ParseService {
    private static int bestBid;
    private static int bestAsk;
    private static final StringBuilder result;

    static {
        bestBid = 0;
        bestAsk = Integer.MAX_VALUE;
        result = new StringBuilder();
        OrderBook.orders.put(bestBid, 0);
        OrderBook.orders.put(bestAsk, 0);
    }

    public static StringBuilder getResult() {
        return result;
    }

    @Override
    public void markBestAsk() {
        if (bestAsk == Integer.MAX_VALUE) {
            throw new RuntimeException("Cannot introduce best ask because of there isn't "
                    + "any relevant goods to buy in the order book");
        }
        result.append(bestAsk)
                .append(",")
                .append(OrderBook.orders.get(bestAsk))
                .append("\n");
    }

    @Override
    public void markBestBid() {
        if (bestBid == 0) {
            throw new RuntimeException("Cannot introduce best bid because of there isn't "
                    + "any relevant goods to sell in the order book");
        }
        result.append(bestBid)
                .append(",")
                .append(OrderBook.orders.get(bestBid))
                .append("\n");
    }

    @Override
    public void updateBids(int price, int size) {
        do {
            if (price >= bestAsk) {
                if (size < OrderBook.orders.get(bestAsk)) {
                    OrderBook.orders.put(bestAsk, OrderBook.orders.get(bestAsk) - size);
                    return;
                } else if (size == OrderBook.orders.get(bestAsk)) {
                    OrderBook.orders.put(bestAsk, 0);
                    updateBestBidAsk();
                    return;
                } else {
                    size -= OrderBook.orders.get(bestAsk);
                    OrderBook.orders.put(bestAsk, 0);
                    updateBestBidAsk();
                }
            } else {
                if (price >= bestBid) {
                    OrderBook.orders.put(price, size);
                    bestBid = price;
                    updateBestBidAsk();
                    return;
                }
                OrderBook.orders.put(price, size);
                return;
            }
        } while (size > 0);
    }

    @Override
    public void updateAsks(int price, int size) {
        do {
            if (price <= bestBid) {
                if (size < OrderBook.orders.get(bestBid)) {
                    OrderBook.orders.put(bestBid, OrderBook.orders.get(bestBid) - size);
                    return;
                } else if (size == OrderBook.orders.get(bestBid)) {
                    OrderBook.orders.put(bestBid, 0);
                    updateBestBidAsk();
                    return;
                } else {
                    size -= OrderBook.orders.get(bestBid);
                    OrderBook.orders.put(bestBid, 0);
                    updateBestBidAsk();
                }
            } else {
                if (price <= bestAsk) {
                    OrderBook.orders.put(price, size);
                    bestAsk = price;
                    updateBestBidAsk();
                    return;
                }
                OrderBook.orders.put(price, size);
                return;
            }
        } while (size > 0);
    }

    private static void updateBestBidAsk() {
        if (bestBid > 0 && OrderBook.orders.get(bestBid) == 0) {
            do {
                bestBid = OrderBook.orders.floorKey(bestBid - 1);
                if (OrderBook.orders.get(bestBid) > 0) {
                    break;
                }
            }  while (bestBid > 0);
        }
        if (bestAsk < Integer.MAX_VALUE && OrderBook.orders.get(bestAsk) == 0) {
            do {
                bestAsk = OrderBook.orders.ceilingKey(bestAsk + 1);
                if (OrderBook.orders.get(bestAsk) > 0) {
                    break;
                }
            }  while (bestAsk < Integer.MAX_VALUE);
        }
    }

    public void markSizeByPrice(int price) {
        int size = OrderBook.orders.get(price) == null ? 0 : OrderBook.orders.get(price);
        result.append(size);
        result.append("\n");
    }

    @Override
    public void removeFromBestAsks(int sizeToRemove) {
        do {
            if (OrderBook.orders.get(bestAsk) > sizeToRemove) {
                OrderBook.orders.put(bestAsk, OrderBook.orders.get(bestAsk) - sizeToRemove);
                return;
            } else if (OrderBook.orders.get(bestAsk) == sizeToRemove) {
                OrderBook.orders.put(bestAsk, 0);
                updateBestBidAsk();
                return;
            } else {
                sizeToRemove -= OrderBook.orders.get(bestAsk);
                OrderBook.orders.put(bestAsk, 0);
                updateBestBidAsk();
            }
        } while (sizeToRemove > 0 && bestAsk != Integer.MAX_VALUE);
        throw new RuntimeException("Cannot operate remove operation with 'BUY', because there "
                + "isn't enough stored goods corresponds to request quantity!");
    }

    @Override
    public void removeFromBestBids(int sizeToRemove) {
        do {
            if (OrderBook.orders.get(bestBid) > sizeToRemove) {
                OrderBook.orders.put(bestBid, OrderBook.orders.get(bestBid) - sizeToRemove);
                return;
            } else if (OrderBook.orders.get(bestBid) == sizeToRemove) {
                OrderBook.orders.put(bestBid, 0);
                updateBestBidAsk();
                return;
            } else {
                sizeToRemove -= OrderBook.orders.get(bestBid);
                OrderBook.orders.put(bestBid, 0);
                updateBestBidAsk();
            }
        } while (sizeToRemove > 0 && bestBid != 0);
        throw new RuntimeException("Cannot operate remove operation with 'SELL', because there "
                + "isn't enough stored goods corresponds to request quantity!");
    }
}

package service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import model.Market;
import service.ParseService;

public class ParseServiceImpl implements ParseService {
    private static final String BEST_BID = "best_bid";
    private static final String BEST_ASK = "best_ask";
    private static final String BUY = "buy";
    private static final String BID = "bid";
    private static final String ASK = "ask";
    private final Market market;

    public ParseServiceImpl(Market market) {
        this.market = market;
    }

    @Override
    public List<String> parse(List<String> operations) {
        List<String> result = new ArrayList<>();
        for (String operation : operations) {
            char c = operation.charAt(0);
            if (c == 'u') {
                update(operation);
            } else if (c == 'o') {
                remove(operation);
            } else if (c == 'q') {
                String[] request = operation.split(",");
                switch (request[1]) {
                    case BEST_BID -> {
                        Integer bidKey = market.getBids().entrySet().stream()
                                .filter(size -> size.getValue() > 0)
                                .map(Map.Entry::getKey)
                                .findFirst()
                                .orElseThrow(
                                        () -> new RuntimeException("Cannot get element from bids. "
                                                + "Looks like map has no bids with size > 0"));
                        result.add(bidKey.toString() + "," + market.getBids().get(bidKey));
                    }
                    case BEST_ASK -> {
                        Integer askKey = market.getAsks().entrySet().stream()
                                .filter(size -> size.getValue() > 0)
                                .map(Map.Entry::getKey)
                                .findFirst()
                                .orElseThrow(
                                        () -> new RuntimeException("Cannot get element from asks. "
                                                + "Looks like map has no asks with size > 0"));
                        result.add(askKey.toString() + "," + market.getAsks().get(askKey));
                    }
                    default -> {
                        Integer bigPrice = Integer.valueOf(request[2]);
                        Integer quantity = market.getBids().get(bigPrice);
                        if (quantity == null) {
                            quantity = market.getAsks().get(bigPrice);
                            if (quantity == null) {
                                quantity = 0;
                            }
                        }
                        result.add(quantity.toString());
                    }
                }
            } else {
                throw new RuntimeException("Incorrect input data. Operation should start with "
                        + "symbols 'u', 'o' or 'q'. Check input file and restart app");
            }
        }
        return result;
    }

    private void update(String operation) {
        String[] request = operation.split(",");
        if (request[3].equals(BID)) {
            TreeMap<Integer, Integer> asks = market.getAsks();
            Integer costBid = Integer.valueOf(request[1]);
            int sizeBid = Integer.parseInt(request[2]);
            for (Map.Entry<Integer, Integer> entry : asks.entrySet()) {
                if (entry.getValue() < 0) {
                    continue;
                }
                if (costBid >= entry.getKey()) {
                    if (sizeBid >= 0) {
                        sizeBid -= (entry.getValue());
                        entry.setValue(0);
                    } else {
                        entry.setValue(entry.getValue() - sizeBid);
                        return;
                    }
                }
            }
            market.getBids().put(costBid, sizeBid);
        } else if (request[3].equals(ASK)) {
            TreeMap<Integer, Integer> bids = market.getBids();
            Integer costAsk = Integer.valueOf(request[1]);
            int sizeAsk = Integer.parseInt(request[2]);
            for (Map.Entry<Integer, Integer> entry : bids.entrySet()) {
                if (entry.getValue() < 0) {
                    continue;
                }
                if (costAsk <= entry.getKey()) {
                    if (sizeAsk >= entry.getValue()) {
                        sizeAsk -= entry.getValue();
                        entry.setValue(0);
                    } else {
                        entry.setValue(entry.getValue() - sizeAsk);
                        return;
                    }
                }
            }
            market.getAsks().put(costAsk, sizeAsk);
        } else {
            throw new RuntimeException("Incorrect input data. field should be "
                    + "'bid' or 'ask'. Check input file and restart app");
        }
    }

    private void remove(String operation) {
        String[] request = operation.split(",");
        String typeOperation = request[1];
        int sizeToSubtract = Integer.parseInt(request[2]);
        if (BUY.equals(typeOperation)) {
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
                    + " have no sufficient stored goods corresponds to request quantity!");
        }
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
                + "have no adequate stored goods corresponds to request quantity!");
    }
}

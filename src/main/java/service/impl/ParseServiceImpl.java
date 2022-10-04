package service.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import model.Market;
import service.ParseService;

public class ParseServiceImpl implements ParseService {
    private static final String BEST_BID = "best_bid";
    private static final String BEST_ASK = "best_ask";
    private static final String SIZE = "size";
    private static final String BUY = "buy";
    private static final String SELL = "sell";
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
                    case BEST_BID:
                        BigInteger bidKey = market.getBids().entrySet().stream()
                                        .filter(size -> size.getValue().compareTo(BigInteger.ZERO) > 0)
                                .map(Map.Entry::getKey)
                                .findFirst()
                                .orElseThrow(
                                        () -> new RuntimeException("Cannot get element from bids. "
                                                + "Looks like map is empty"));
                        result.add(bidKey.toString() + "," + market.getBids().get(bidKey));
                        break;
                    case BEST_ASK:
                        BigInteger askKey = market.getAsks().entrySet().stream()
                                .filter(size -> size.getValue().compareTo(BigInteger.ZERO) > 0)
                                .map(Map.Entry::getKey)
                                .findFirst()
                                .orElseThrow(
                                        () -> new RuntimeException("Cannot get element from asks. "
                                                + "Looks like map is empty"));
                        result.add(askKey.toString() + "," + market.getAsks().get(askKey));
                        break;
                    case SIZE:
                        BigInteger bigPrice = new BigInteger(request[2]);
                        BigInteger quantity = market.getBids().get(bigPrice);
                        if (quantity == null) {
                            quantity = market.getAsks().get(bigPrice);
                            if (quantity == null) {
                                quantity = BigInteger.ZERO;
                            }
                        }
                        result.add(quantity.toString());
                        break;
                    default:
                        throw new RuntimeException("incorrect input data: field [" + operation
                                + "] should correspond next options: 'best_bid', 'best_ask' or "
                                + "'size'. Check input file and try again");
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
            TreeMap<BigInteger, BigInteger> asks = market.getAsks();
            BigInteger costBid = new BigInteger(request[1]);
            BigInteger sizeBid = new BigInteger(request[2]);
            for (Map.Entry<BigInteger, BigInteger> entry : asks.entrySet()) {
                if (entry.getValue().compareTo(BigInteger.ZERO) < 0) {
                    continue;
                }
                if (costBid.compareTo(entry.getKey()) >= 0) {
                    if (sizeBid.compareTo(entry.getValue()) >= 0) {
                        sizeBid = sizeBid.subtract(entry.getValue());
                        entry.setValue(BigInteger.ZERO);
                    } else {
                        entry.setValue(entry.getValue().subtract(sizeBid));
                        return;
                    }
                }
            }
            market.getBids().put(costBid, sizeBid);
        } else if (request[3].equals(ASK)) {
            TreeMap<BigInteger, BigInteger> bids = market.getBids();
            BigInteger costAsk = new BigInteger(request[1]);
            BigInteger sizeAsk = new BigInteger(request[2]);
            for (Map.Entry<BigInteger, BigInteger> entry : bids.entrySet()) {
                if (entry.getValue().compareTo(BigInteger.ZERO) < 0) {
                    continue;
                }
                if (costAsk.compareTo(entry.getKey()) <= 0) {
                    if (sizeAsk.compareTo(entry.getValue()) >= 0) {
                        sizeAsk = sizeAsk.subtract(entry.getValue());
                        entry.setValue(BigInteger.ZERO);
                    } else {
                        entry.setValue(entry.getValue().subtract(sizeAsk));
                        return;
                    }
                }
            }
            market.getAsks().put(new BigInteger(request[1]), new BigInteger(request[2]));
        } else {
            throw new RuntimeException("Incorrect input data. field should be "
                    + "'bid' or 'ask'. Check input file and restart app");
        }
    }

    private void remove(String operation) {
        String[] request = operation.split(",");
        String typeOperation = request[1];
        BigInteger sizeToSubtract = new BigInteger(request[2]);
        switch (typeOperation) {
            case BUY:
                TreeMap<BigInteger, BigInteger> asks = market.getAsks();
                for (Map.Entry<BigInteger, BigInteger> entry : asks.entrySet()) {
                    if (entry.getValue().compareTo(BigInteger.ZERO) < 0) {
                        continue;
                    }
                    if (sizeToSubtract.compareTo(entry.getValue()) >= 0) {
                        sizeToSubtract = sizeToSubtract.subtract(entry.getValue());
                        entry.setValue(BigInteger.ZERO);
                    } else {
                        entry.setValue(entry.getValue().subtract(sizeToSubtract));
                        return;
                    }
                }
                throw new RuntimeException("Cannot operate remove operation with 'BUY', case you have no "
                        + "adequate stored goods corresponds to request quantity!");
            case SELL:
                TreeMap<BigInteger, BigInteger> bids = market.getBids();
                for (Map.Entry<BigInteger, BigInteger> entry : bids.entrySet()) {
                    if (entry.getValue().compareTo(BigInteger.ZERO) < 0) {
                        continue;
                    }
                    if (sizeToSubtract.compareTo(entry.getValue()) >= 0) {
                        sizeToSubtract = sizeToSubtract.subtract(entry.getValue());
                        entry.setValue(BigInteger.ZERO);
                    } else {
                        entry.setValue(entry.getValue().subtract(sizeToSubtract));
                        return;
                    }
                }
                throw new RuntimeException("Cannot operate remove operation with 'SELL', case you have no "
                        + "adequate stored goods corresponds to request quantity!");
            default:
                throw new RuntimeException("incorrect input data: field [" + operation
                        + "] should correspond next options: 'buy', or "
                        + "'sell'. Check input file and try again");
        }
    }
}

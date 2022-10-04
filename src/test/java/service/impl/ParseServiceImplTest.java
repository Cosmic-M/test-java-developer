package service.impl;

import model.Market;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ParseService;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ParseServiceImplTest {
    private ParseService parseService;

    @BeforeEach
    public void initial() {
        Market market = new Market();
        parseService = new ParseServiceImpl(market);
    }

    @Test
    public void parse_initialData_ok() {
        List<String> input = List.of(
                "u,9,1,bid",
                "u,11,5,ask",
                "q,best_bid",
                "u,10,2,bid",
                "q,best_bid",
                "o,sell,1",
                "q,size,10",
                "u,9,0,bid",
                "u,11,0,ask");

        List<String> output = List.of("9,1", "10,2", "1");

        assertEquals(output, parseService.parse(input));
    }

    @Test
    public void parse_fillDataConsequentlyStartsWithBid_ok() {
        List<String> input = List.of(
                "u,9,5,bid",
                "u,10,5,bid",
                "u,11,5,bid",
                "u,12,5,bid",
                "u,14,5,ask",
                "u,13,5,ask",
                "u,12,3,ask",
                "u,11,2,ask",
                "o,sell,3",
                "o,buy,1",
                "q,best_bid",
                "q,best_ask",
                "q,size,10");

        List<String> output = List.of("11,2", "13,4", "5");

        assertEquals(output, parseService.parse(input));
    }

    @Test
    public void parse_fillDataConsequentlyStartsWithAsk_ok() {
        List<String> input = List.of(
                "u,14,5,ask",
                "u,13,5,ask",
                "u,12,3,ask",
                "u,11,2,ask",
                "u,9,5,bid",
                "u,10,5,bid",
                "u,11,5,bid",
                "u,12,5,bid",
                "o,sell,2",
                "o,buy,2",
                "q,best_bid",
                "q,best_ask",
                "q,size,12");

        List<String> output = List.of("11,3", "13,3", "0");

        assertEquals(output, parseService.parse(input));
    }

    @Test
    public void parse_ifSpecifiedPrizeIsAbsentPrintZero_ok() {
        List<String> input = List.of(
                "u,14,5,ask",
                "u,10,5,bid",
                "q,size,12");

        List<String> output = List.of("0");

        assertEquals(output, parseService.parse(input));
    }

    @Test
    public void parse_removesSizeOutOfAsks_ok() {
        List<String> input = List.of(
                "u,15,2,ask",
                "u,14,2,ask",
                "u,13,2,ask",
                "u,12,3,ask",
                "o,buy,6",
                "q,best_ask");

        List<String> output = List.of("14,1");

        assertEquals(output, parseService.parse(input));
    }

    @Test
    public void parse_removesSizeOutOfBids_ok() {
        List<String> input = List.of(
                "u,9,2,bid",
                "u,10,2,bid",
                "u,11,2,bid",
                "u,12,3,bid",
                "o,sell,6",
                "q,best_bid");

        List<String> output = List.of("10,1");

        assertEquals(output, parseService.parse(input));
    }
}

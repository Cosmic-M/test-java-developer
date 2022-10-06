package service.impl;

import model.Market;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ParseService;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ParseServiceImplTest {
    private ParseService parseService;

    @BeforeEach
    public void initial() {
        parseService = new ParseServiceImpl();
        Market.clearMaps();
    }

    @Test
    public void parse_initialData_ok() {
        StringBuilder input = new StringBuilder("u,9,1,bid"
                        + "u,11,5,ask"
                        + "q,best_bid"
                        + "u,10,2,bid"
                        + "q,best_bid"
                        + "o,sell,1"
                        + "q,size,10"
                        + "u,9,0,bid"
                        + "u,11,0,ask"
                        + "q,");

        String output = "9,1\n10,2\n1";
        assertEquals(output, parseService.parse(input).toString());
    }

    @Test
    public void parse_emptyFile_ok() {
        StringBuilder input = new StringBuilder();

        assertEquals("", parseService.parse(input).toString());
    }

    @Test
    public void parse_fillDataConsequentlyStartsWithBid_ok() {
        StringBuilder input = new StringBuilder("u,9,5,bid"
                        + "u,10,5,bid"
                        + "u,11,5,bid"
                        + "u,12,5,bid"
                        + "u,14,5,ask"
                        + "u,13,5,ask"
                        + "u,12,3,ask"
                        + "u,11,2,ask"
                        + "o,sell,3"
                        + "o,buy,1"
                        + "q,best_bid"
                        + "q,best_ask"
                        + "q,size,10"
                        + "q,");

        String output = "11,2\n13,4\n5";
        assertEquals(output, parseService.parse(input).toString());
    }

    @Test
    public void parse_fillDataConsequentlyStartsWithAsk_ok() {
        StringBuilder input = new StringBuilder("u,14,5,ask"
                        + "u,13,5,ask"
                        + "u,12,3,ask"
                        + "u,11,2,ask"
                        + "u,9,5,bid"
                        + "u,10,5,bid"
                        + "u,11,5,bid"
                        + "u,12,5,bid"
                        + "o,sell,2"
                        + "o,buy,2"
                        + "q,best_bid"
                        + "q,best_ask"
                        + "q,size,12"
                        + "q,");

        String output = "11,3\n13,3\n0";
        assertEquals(output, parseService.parse(input).toString());
    }

    @Test
    public void parse_ifSpecifiedPrizeIsAbsentPrintZero_ok() {
        StringBuilder input = new StringBuilder("u,14,5,ask"
                        + "u,10,5,bid"
                        + "q,size,12"
                        + "q,");

        assertEquals("0", parseService.parse(input).toString());
    }

    @Test
    public void parse_removesSizeOutOfAsks_ok() {
        StringBuilder input = new StringBuilder("u,15,2,ask"
                        + "u,14,2,ask"
                        + "u,13,2,ask"
                        + "u,12,3,ask"
                        + "o,buy,6"
                        + "q,best_ask"
                        + "q,");

        assertEquals("14,1", parseService.parse(input).toString());
    }

    @Test
    public void parse_removesSizeOutOfBids_ok() {
        StringBuilder input = new StringBuilder("u,9,2,bid"
                        + "u,10,2,bid"
                        + "u,11,2,bid"
                        + "u,12,3,bid"
                        + "o,sell,6"
                        + "q,best_bid"
                        + "q,");

        assertEquals("10,1", parseService.parse(input).toString());
    }

    @Test
    public void parse_manyCrossingData_ok() {
        StringBuilder input = new StringBuilder("u,15,2,ask"
                        + "q,size,10"
                        + "u,10,3,bid"
                        + "u,10,2,ask"
                        + "q,size,10"
                        + "u,10,5,bid"
                        + "q,size,10"
                        + "u,9,5,bid"
                        + "u,8,11,ask"
                        + "u,10,3,bid"
                        + "u,9,4,bid"
                        + "q,best_bid"
                        + "o,sell,4"
                        + "q,best_bid"
                        + "q,");

        String output = "0\n1\n5\n10,2\n9,2";

        assertEquals(output, parseService.parse(input).toString());
    }
}

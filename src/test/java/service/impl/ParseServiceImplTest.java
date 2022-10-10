package service.impl;

import model.OrderBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ParseService;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParseServiceImplTest {
    private ParseService parseService;

    @BeforeEach
    public void initial() {
        parseService = new ParseServiceImpl();
        OrderBook.clearOrderBook();
    }

    @Test
    public void parse_emptyFile_ok() {
        StringBuilder input = new StringBuilder();

        assertEquals("", parseService.parse(input).toString());
    }

    @Test
    public void parse_findBestBidIfAsksAbsent_ok() {
        StringBuilder input = new StringBuilder("u,9,4,bid"
                + "u,11,1,bid"
                + "u,12,3,bid"
                + "u,10,2,bid"
                + "u,11,7,bid"
                + "u,10,5,bid"
                + "u,7,3,bid"
                + "u,12,8,bid"
                + "u,5,155,bid"
                + "u,13,4,bid"
                + "u,8,14,bid"
                + "q,best_bid"
                + "q,");

        String output = "13,4";
        assertEquals(output, parseService.parse(input).toString());
    }

    @Test
    public void parse_findBesAskIfBidsAbsent_ok() {
        StringBuilder input = new StringBuilder("u,17,4,ask"
                + "u,11,3,ask"
                + "u,10,3,ask"
                + "u,15,2,ask"
                + "u,15,2,ask"
                + "u,10,3,ask"
                + "u,19,15,ask"
                + "u,17,1,ask"
                + "q,best_ask"
                + "q,");

        String output = "10,3";
        assertEquals(output, parseService.parse(input).toString());
    }

    @Test
    public void parse_findBestAskAndBidAfterDifferentUpdates_ok() {
        StringBuilder input = new StringBuilder("u,17,4,ask"
                + "u,14,3,ask"
                + "u,13,3,ask"
                + "u,12,3,ask"
                + "u,11,3,ask"
                + "u,9,2,bid"
                + "u,10,2,bid"
                + "u,11,2,bid"
                + "u,12,2,bid"
                + "q,best_bid"
                + "q,best_ask"
                + "q,");

        String output = "10,2\n12,2";
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
    public void parse_manySizeRequests_ok() {
        StringBuilder input = new StringBuilder("u,14,5,ask"
                + "q,size,14"
                + "u,13,5,bid"
                + "q,size,13"
                + "u,16,2,bid"
                + "q,size,14"
                + "u,11,2,ask"
                + "q,size,13"
                + "q,");

        assertEquals("5\n5\n3\n3", parseService.parse(input).toString());
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

    @Test
    public void parse_tooManyCrossingData_ok() {
        StringBuilder input = new StringBuilder("u,15,5,ask"
                + "u,10,5,bid"
                + "u,8,3,ask"
                + "u,9,5,bid"
                + "u,8,3,ask"
                + "u,6,5,bid"
                + "u,12,5,ask"
                + "u,13,5,ask"
                + "u,9,5,ask"
                + "o,sell,1"
                + "o,buy,2"
                + "o,sell,2"
                + "o,buy,10"
                + "u,10,5,bid"
                + "u,9,5,ask"
                + "o,buy,4"
                + "o,sell,2"
                + "u,15,5,ask"
                + "u,10,5,bid"
                + "u,8,3,ask"
                + "u,9,5,bid"
                + "u,8,3,ask"
                + "u,6,5,bid"
                + "u,12,5,ask"
                + "u,13,5,ask"
                + "u,9,5,ask"
                + "o,sell,1"
                + "o,buy,2"
                + "o,sell,2"
                + "o,buy,10"
                + "q,best_bid"
                + "q,best_ask"
                + "q,size,12"
                + "q,");

        String output = "6,2\n15,4\n0";

        assertEquals(output, parseService.parse(input).toString());
    }

    @Test
    public void parse_bestBidRefresh_ok() {
        StringBuilder input = new StringBuilder("u,15,2,ask"
                + "u,13,3,ask"
                + "u,15,5,bid"
                + "u,10,3,bid"
                + "u,8,3,ask"
                + "u,12,1,bid"
                + "u,14,3,ask"
                + "q,best_bid"
                + "q,best_ask"
                + "q,");

        String output = "12,1\n14,3";

        assertEquals(output, parseService.parse(input).toString());
    }

    @Test
    public void parse_bestAskRefresh_ok() {
        StringBuilder input = new StringBuilder("u,11,5,bid"
                + "u,12,4,bid"
                + "u,10,9,ask"
                + "u,12,7,ask"
                + "u,13,7,bid"
                + "u,11,4,bid"
                + "u,13,5,ask"
                + "q,best_bid"
                + "q,best_ask"
                + "q,");

        String output = "11,4\n13,5";

        assertEquals(output, parseService.parse(input).toString());
    }

    @Test
    public void parse_bigComboWithBidsAsks_ok() {
        StringBuilder input = new StringBuilder("u,15,2,ask"
                + "u,14,3,ask"
                + "u,13,5,ask"
                + "u,12,3,ask"
                + "u,11,2,ask"
                + "u,9,1,bid"
                + "u,8,4,bid"
                + "u,7,10,bid"
                + "u,6,4,bid"
                + "u,5,1,bid"
                + "o,buy,2"
                + "o,buy,2"
                + "u,11,1,ask"
                + "o,buy,3"
                + "o,buy,2"
                + "o,buy,2"
                + "o,buy,2"
                + "o,buy,2"
                + "o,sell,3"
                + "o,sell,3"
                + "u,9,1,bid"
                + "u,8,1,bid"
                + "u,10,1,bid"
                + "o,sell,3"
                + "o,sell,3"
                + "o,sell,3"
                + "o,sell,3"
                + "o,sell,3"
                + "q,best_bid"
                + "q,best_ask"
                + "q,");

        String output = "6,1\n15,1";

        assertEquals(output, parseService.parse(input).toString());
    }

    @Test
    public void parse_repeatQuerySizeData_ok() {
        StringBuilder input = new StringBuilder("u,11,1,ask"
                + "u,9,1,bid"
                + "u,12,1,ask"
                + "u,8,1,bid"
                + "u,11,1,ask"
                + "u,9,1,bid"
                + "u,9,1,ask"
                + "u,11,1,bid"
                + "q,size,9"
                + "q,size,9"
                + "q,size,10"
                + "q,size,11"
                + "q,size,11"
                + "q,best_bid"
                + "q,best_ask"
                + "o,sell,1"
                + "o,buy,1"
                + "q,size,8"
                + "q,size,12"
                + "q,");

        String output = "0\n0\n0\n0\n0\n8,1\n12,1\n0\n0";

        assertEquals(output, parseService.parse(input).toString());
    }

    @Test
    public void parse_manyDataWithResetOneBest_ok() {
        StringBuilder input = new StringBuilder("u,15,2,ask"
                + "u,14,3,ask"
                + "u,13,5,ask"
                + "u,13,5,ask"
                + "u,13,5,ask"
                + "u,13,5,ask"
                + "u,12,3,ask"
                + "u,11,2,ask"
                + "u,9,1,bid"
                + "u,8,4,bid"
                + "u,7,10,bid"
                + "u,6,4,bid"
                + "u,5,1,bid"
                + "o,buy,1"
                + "o,buy,1"
                + "u,10,1,ask"
                + "o,buy,1"
                + "u,10,1,ask"
                + "o,buy,1"
                + "o,buy,1"
                + "o,buy,1"
                + "u,11,1,ask"
                + "o,buy,11"
                + "u,4,1,bid"
                + "u,11,3,ask"
                + "o,buy,2"
                + "u,10,1,ask"
                + "o,sell,1"
                + "u,20,2,bid"
                + "o,sell,2"
                + "u,5,2,ask"
                + "o,sell,2"
                + "u,9,1,bid"
                + "u,8,1,bid"
                + "o,buy,1"
                + "u,10,1,bid"
                + "o,sell,15"
                + "q,best_bid"
                + "q,size,15"
                + "q,size,4"
                + "q,");

        String output = "5,1\n0\n1";

        assertEquals(output, parseService.parse(input).toString());
    }

    @Test
    public void parse_resetBestAskBeforeUpdate_ok() {
        StringBuilder input = new StringBuilder("u,13,5,ask"
                + "u,8,5,bid"
                + "u,14,5,bid"
                + "u,12,5,ask"
                + "q,best_bid"
                + "q,best_ask"
                + "q,");

        String output = "8,5\n12,5";

        assertEquals(output, parseService.parse(input).toString());
    }

    @Test
    public void parse_resetBestBidBeforeUpdate_ok() {
        StringBuilder input = new StringBuilder("u,8,5,bid"
                + "u,12,5,ask"
                + "u,7,5,ask"
                + "u,9,5,bid"
                + "q,best_bid"
                + "q,best_ask"
                + "q,");

        String output = "9,5\n12,5";

        assertEquals(output, parseService.parse(input).toString());
    }
}

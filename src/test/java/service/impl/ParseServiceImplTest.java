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
        Market market = new Market();
        parseService = new ParseServiceImpl(market);
    }

    @Test
    public void parse_initialData_ok() {
        StringBuilder input = new StringBuilder(
                """
                        u,9,1,bid\r
                         u,11,5,ask\r
                         q,best_bid\r
                         u,10,2,bid\r
                        q,best_bid\r
                         o,sell,1\r
                         q,size,10\r
                         u,9,0,bid\r
                        u,11,0,ask\r
                        q,""");

        String output =
                "9,1\r\n10,2\r\n1";
        assertEquals(output, parseService.parse(input).toString());
    }

    @Test
    public void parse_emptyFile_ok() {
        StringBuilder input = new StringBuilder("");

        assertEquals("", parseService.parse(input).toString());
    }

    @Test
    public void parse_fillDataConsequentlyStartsWithBid_ok() {
        StringBuilder input = new StringBuilder(
                """
                        u,9,5,bid\r
                        u,10,5,bid\r
                        u,11,5,bid\r
                        u,12,5,bid\r
                        u,14,5,ask\r
                        u,13,5,ask\r
                        u,12,3,ask\r
                        u,11,2,ask\r
                        o,sell,3\r
                        o,buy,1\r
                        q,best_bid\r
                        q,best_ask\r
                        q,size,10\r
                        q,""");

        String output =
                "11,2\r\n13,4\r\n5";
        assertEquals(output, parseService.parse(input).toString());
    }

    @Test
    public void parse_fillDataConsequentlyStartsWithAsk_ok() {
        StringBuilder input = new StringBuilder(
                """
                        u,14,5,ask\r
                        u,13,5,ask\r
                        u,12,3,ask\r
                        u,11,2,ask\r
                        u,9,5,bid\r
                        u,10,5,bid\r
                        u,11,5,bid\r
                        u,12,5,bid\r
                        o,sell,2\r
                        o,buy,2\r
                        q,best_bid\r
                        q,best_ask\r
                        q,size,12\r
                        q,""");

        String output =
                "11,3\r\n13,3\r\n0";

        assertEquals(output, parseService.parse(input).toString());
    }

    @Test
    public void parse_ifSpecifiedPrizeIsAbsentPrintZero_ok() {
        StringBuilder input = new StringBuilder(
                """
                        u,14,5,ask\r
                        u,10,5,bid\r
                        q,size,12\r
                        q,""");

        assertEquals("0", parseService.parse(input).toString());
    }

    @Test
    public void parse_removesSizeOutOfAsks_ok() {
        StringBuilder input = new StringBuilder(
                """
                        u,15,2,ask\r
                        u,14,2,ask\r
                        u,13,2,ask\r
                        u,12,3,ask\r
                        o,buy,6\r
                        q,best_ask\r
                        q,""");

        assertEquals("14,1", parseService.parse(input).toString());
    }

    @Test
    public void parse_removesSizeOutOfBids_ok() {
        StringBuilder input = new StringBuilder(
                """
                        u,9,2,bid\r
                        u,10,2,bid\r
                        u,11,2,bid\r
                        u,12,3,bid\r
                        o,sell,6\r
                        q,best_bid\r
                        q,""");

        assertEquals("10,1", parseService.parse(input).toString());
    }

    @Test
    public void parse_manyCrossingData_ok() {
        StringBuilder input = new StringBuilder(
                """
                        u,15,2,ask\r
                        q,size,10\r
                        u,10,3,bid\r
                        u,10,2,ask\r
                        q,size,10\r
                        u,10,5,bid\r
                        q,size,10\r
                        u,9,5,bid\r
                        u,8,11,ask\r
                        u,10,3,bid\r
                        u,9,4,bid\r
                        q,best_bid\r
                        o,sell,4\r
                        q,best_bid\r
                        q,""");

        String output =
                "0\r\n1\r\n5\r\n10,2\r\n9,2";

        assertEquals(output, parseService.parse(input).toString());
    }
}

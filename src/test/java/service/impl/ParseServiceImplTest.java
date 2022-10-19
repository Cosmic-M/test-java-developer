package service.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.FileReaderService;
import service.ParseService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParseServiceImplTest {
    private ParseService parseService;
    private FileReaderService fileReaderService;

    @BeforeEach
    public void initial() {
        parseService = new ParseServiceImpl();
        fileReaderService = new FileReaderServiceImpl();
    }

    @AfterEach
    public void resetOrderBook() {
        parseService.resetOrderBook();
    }

    @Test
    public void parse_findBestBidIfAsksAbsent_ok() {
        StringBuilder result = fileReaderService.readFile(
                "src/test/resources/test_files/findBestBidIfAsksAbsent.txt");
        assertEquals("13,4", result.toString());
    }

    @Test
    public void parse_findBesAskIfBidsAbsent_ok() {
        StringBuilder result = fileReaderService.readFile(
                "src/test/resources/test_files/findBestAskIfDidsAbsent.txt");
        assertEquals("10,3", result.toString());
    }

    @Test
    public void parse_findBestAskAndBidAfterDifferentUpdates_ok() {
        StringBuilder result = fileReaderService.readFile(
                "src/test/resources/test_files/findBestAskAndBidAfterDifferentUpdates");
        assertEquals("10,2\n12,2", result.toString());
    }

    @Test
    public void parse_ifSpecifiedPrizeIsAbsentPrintZero_ok() {
        StringBuilder result = fileReaderService.readFile(
                "src/test/resources/test_files/ifSpecifiedPrizeIsAbsentPrintZero.txt");
        assertEquals("0", result.toString());
    }

    @Test
    public void parse_manySizeRequests_ok() {
        StringBuilder result = fileReaderService.readFile(
                "src/test/resources/test_files/manySizeRequests.txt");
        assertEquals("5\n5\n3\n3", result.toString());
    }

    @Test
    public void parse_removesSizeOutOfAsks_ok() {
        StringBuilder result = fileReaderService.readFile(
                "src/test/resources/test_files/removesSizeOutOfAsks.txt");
        assertEquals("14,1", result.toString());
    }

    @Test
    public void parse_removesSizeOutOfBids_ok() {
        StringBuilder result = fileReaderService.readFile(
                "src/test/resources/test_files/removesSizeOutOfBids.txt");
        assertEquals("10,1", result.toString());
    }

    @Test
    public void parse_fillDataConsequentlyStartsWithBid_ok() {
        StringBuilder result = fileReaderService.readFile(
                "src/test/resources/test_files/fillDataConsequentlyStartsWithBid.txt");
        assertEquals("11,2\n13,4\n5", result.toString());
    }

    @Test
    public void parse_fillDataConsequentlyStartsWithAsk_ok() {
        StringBuilder result = fileReaderService.readFile(
                "src/test/resources/test_files/fillDataConsequentlyStartsWithAsk.txt");
        assertEquals("11,3\n13,3\n0", result.toString());
    }

    @Test
    public void parse_manyCrossingData_ok() {
        StringBuilder result = fileReaderService.readFile(
                "src/test/resources/test_files/manyCrossingData.txt");
        assertEquals("0\n1\n5\n10,2\n9,2", result.toString());
    }

    @Test
    public void parse_tooManyCrossingData_ok() {
        StringBuilder result = fileReaderService.readFile(
                "src/test/resources/test_files/tooManyCrossingData.txt");
        assertEquals("6,2\n15,4\n0", result.toString());
    }

    @Test
    public void parse_bestBidRefresh_ok() {
        StringBuilder result = fileReaderService.readFile(
                "src/test/resources/test_files/bestBidRefresh.txt");
    assertEquals("12,1\n14,3", result.toString());
}

    @Test
    public void parse_bestAskRefresh_ok() {
        StringBuilder result = fileReaderService.readFile(
                "src/test/resources/test_files/parse_bestAskRefresh.txt");
        assertEquals("11,4\n13,5", result.toString());
    }

    @Test
    public void parse_bigComboWithBidsAsks_ok() {
        StringBuilder result = fileReaderService.readFile(
                "src/test/resources/test_files/bigComboWithBidsAsks.txt");
        assertEquals("6,1\n15,1", result.toString());
    }

    @Test
    public void parse_repeatQuerySizeData_ok() {
        StringBuilder result = fileReaderService.readFile(
                "src/test/resources/test_files/repeatQuerySizeData.txt");
        assertEquals("0\n0\n0\n0\n0\n8,1\n12,1\n0\n0", result.toString());
    }

    @Test
    public void parse_resetBestAskBeforeUpdate_ok() {
        StringBuilder result = fileReaderService.readFile(
                "src/test/resources/test_files/resetBestAskBeforeUpdate.txt");
        assertEquals("8,5\n12,5", result.toString());
    }

    @Test
    public void parse_resetBestBidBeforeUpdate_ok() {
        StringBuilder result = fileReaderService.readFile(
                "src/test/resources/test_files/resetBestBidBeforeUpdate.txt");
        assertEquals("9,5\n12,5", result.toString());
    }

    @Test
    public void parse_subtractOperationFirstEntry_notOk() {
        Exception exc = assertThrows(RuntimeException.class,
                () -> fileReaderService.readFile(
                        "src/test/resources/test_files/subtractOperationFirstEntry.txt"));
        assertEquals("Cannot operate remove operation with 'BUY', because there isn't "
                + "enough stored goods corresponds to request quantity!", exc.getMessage());
    }

    @Test
    public void parse_requestBestBidFirstEntry_notOk() {
    Exception exc = assertThrows(RuntimeException.class,
            () -> fileReaderService.readFile(
                    "src/test/resources/test_files/requestBestBidFirstEntry.txt"));
    assertEquals("Cannot introduce best bid because of there isn't "
                         + "any relevant goods to sell in the order book", exc.getMessage());
}

    @Test
    public void parse_requestAskFirstEntry_notOk() {
        Exception exc = assertThrows(RuntimeException.class,
                () -> fileReaderService.readFile(
                        "src/test/resources/test_files/requestAskFirstEntry.txt"));
        assertEquals("Cannot introduce best ask because of there isn't "
                + "any relevant goods to buy in the order book", exc.getMessage());
    }

    @Test
    public void parse_notEnoughGoodsToSubtractFromBids_notOk() {
        Exception exc = assertThrows(RuntimeException.class,
                () -> fileReaderService.readFile(
                        "src/test/resources/test_files/notEnoughGoodsToSubtractFromBids.txt"));
        assertEquals("Cannot operate remove operation with 'SELL', because there isn't "
                + "enough stored goods corresponds to request quantity!", exc.getMessage());
    }

    @Test
    public void parse_notEnoughGoodsToSubtractFromAsks_notOk() {
        Exception exc = assertThrows(RuntimeException.class,
                () -> fileReaderService.readFile(
                        "src/test/resources/test_files/notEnoughGoodsToSubtractFromAsks.txt"));
        assertEquals("Cannot operate remove operation with 'BUY', because there isn't "
                + "enough stored goods corresponds to request quantity!", exc.getMessage());
    }
}

package service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import model.Market;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.FileReaderService;
import service.ParseService;

class ParseServiceImplTest {
    private ParseService parseService;

    @BeforeEach
    public void initial() {
        parseService = new ParseServiceImpl();
        Market.clear();
    }

    @Test
    public void parse_blinkFile_ok() {
        FileReaderService fileReaderService = new FileReaderServiceImpl(parseService);
        fileReaderService.read(new File("src/test/resources/blinkFile.txt"));
        assertEquals("", parseService.getResult().toString());
    }

    @Test
    public void parse_initialData_ok() {
        FileReaderService fileReaderService = new FileReaderServiceImpl(parseService);
        fileReaderService.read(new File("src/test/resources/bestBidRefresh.txt"));
        assertEquals("12,1\n14,3\n", parseService.getResult().toString());
    }

    @Test
    public void parse_findBesAskIfBidsAbsent_ok() {
        FileReaderService fileReaderService = new FileReaderServiceImpl(parseService);
        fileReaderService.read(new File("src/test/resources/findBestAskIfBidsAbsent.txt"));
        assertEquals("10,3\n", parseService.getResult().toString());
    }

    @Test
    public void parse_findBestAskAndBidAfterDifferentUpdates_ok() {
        FileReaderService fileReaderService = new FileReaderServiceImpl(parseService);
        fileReaderService.read(
                new File("src/test/resources/findBestAskAndBidAfterDifferentUpdates"));
        assertEquals("10,2\n12,2\n", parseService.getResult().toString());
    }

    @Test
    public void parse_ifSpecifiedPriseIsAbsentPrintZero_ok() {
        FileReaderService fileReaderService = new FileReaderServiceImpl(parseService);
        fileReaderService.read(
                new File("src/test/resources/ifSpecifiedPrizeIsAbsentPrintZero.txt"));
        assertEquals("0\n", parseService.getResult().toString());
    }

    @Test
    public void parse_manySizeRequests_ok() {
        FileReaderService fileReaderService = new FileReaderServiceImpl(parseService);
        fileReaderService.read(new File("src/test/resources/manySizeRequests.txt"));
        assertEquals("5\n5\n3\n3\n", parseService.getResult().toString());
    }

    @Test
    public void parse_removesSizeOutOfAsks_ok() {
        FileReaderService fileReaderService = new FileReaderServiceImpl(parseService);
        fileReaderService.read(new File("src/test/resources/removesSizeOutOfAsks.txt"));
        assertEquals("14,1\n", parseService.getResult().toString());
    }

    @Test
    public void parse_removesSizeOutOfBids_ok() {
        FileReaderService fileReaderService = new FileReaderServiceImpl(parseService);
        fileReaderService.read(new File("src/test/resources/removesSizeOutOfBids.txt"));
        assertEquals("10,1\n", parseService.getResult().toString());
    }

    @Test
    public void parse_fillDataConsequentlyStartsWithBid_ok() {
        FileReaderService fileReaderService = new FileReaderServiceImpl(parseService);
        fileReaderService.read(
                new File("src/test/resources/fillDataConsequentlyStartsWithBid.txt"));
        assertEquals("11,2\n13,4\n5\n", parseService.getResult().toString());
    }

    @Test
    public void parse_fillDataConsequentlyStartsWithAsk_ok() {
        FileReaderService fileReaderService = new FileReaderServiceImpl(parseService);
        fileReaderService.read(
                new File("src/test/resources/fillDataConsequentlyStartsWithAsk.txt"));
        assertEquals("11,3\n13,3\n0\n", parseService.getResult().toString());
    }

    @Test
    public void parse_manyCrossingData_ok() {
        FileReaderService fileReaderService = new FileReaderServiceImpl(parseService);
        fileReaderService.read(new File("src/test/resources/manyCrossingData.txt"));
        assertEquals("0\n1\n5\n10,2\n9,2\n", parseService.getResult().toString());
    }

    @Test
    public void parse_tooManyCrossingData_ok() {
        FileReaderService fileReaderService = new FileReaderServiceImpl(parseService);
        fileReaderService.read(new File("src/test/resources/tooManyCrossingData.txt"));
        assertEquals("6,2\n15,4\n0\n", parseService.getResult().toString());
    }

    @Test
    public void parse_findBestBidIfAsksAbsent_ok() {
        FileReaderService fileReaderService = new FileReaderServiceImpl(parseService);
        fileReaderService.read(new File("src/test/resources/findBestBidIfAsksAbsent.txt"));
        assertEquals("13,4\n", parseService.getResult().toString());
    }

    @Test
    public void parse_bestAskRefresh_ok() {
        FileReaderService fileReaderService = new FileReaderServiceImpl(parseService);
        fileReaderService.read(new File("src/test/resources/parse_bestAskRefresh.txt"));
        assertEquals("11,4\n13,5\n", parseService.getResult().toString());
    }

    @Test
    public void parse_bigComboWithBidsAsks_ok() {
        FileReaderService fileReaderService = new FileReaderServiceImpl(parseService);
        fileReaderService.read(new File("src/test/resources/bigComboWithBidsAsks.txt"));
        assertEquals("6,1\n15,1\n", parseService.getResult().toString());
    }

    @Test
    public void parse_repeatQuerySizeData_ok() {
        FileReaderService fileReaderService = new FileReaderServiceImpl(parseService);
        fileReaderService.read(new File("src/test/resources/repeatQuerySizeData.txt"));
        assertEquals("0\n0\n0\n0\n0\n8,1\n12,1\n0\n0\n", parseService.getResult().toString());
    }

    @Test
    public void parse_resetBestAskBeforeUpdate_ok() {
        FileReaderService fileReaderService = new FileReaderServiceImpl(parseService);
        fileReaderService.read(new File("src/test/resources/resetBestAskBeforeUpdate.txt"));
        assertEquals("8,5\n12,5\n", parseService.getResult().toString());
    }

    @Test
    public void parse_resetBestBidBeforeUpdate_ok() {
        FileReaderService fileReaderService = new FileReaderServiceImpl(parseService);
        fileReaderService.read(new File("src/test/resources/resetBestBidBeforeUpdate.txt"));
        assertEquals("9,5\n12,5\n", parseService.getResult().toString());
    }

    @Test
    public void parse_subtractOperationFirstEntry_notOk() {
        FileReaderService fileReaderService = new FileReaderServiceImpl(parseService);
        Exception exc = assertThrows(RuntimeException.class, () -> fileReaderService.read(
                new File("src/test/resources/subtractOperationFirstEntry.txt")));
        assertEquals("Cannot operate remove operation with 'BUY', because there isn't "
                + "enough stored goods corresponds to request quantity!", exc.getMessage());
    }

    @Test
    public void parse_requestBestBidFirstEntry_notOk() {
        FileReaderService fileReaderService = new FileReaderServiceImpl(parseService);
        Exception exc = assertThrows(RuntimeException.class, () -> fileReaderService.read(
                        new File("src/test/resources/requestBestBidFirstEntry.txt")));
        assertEquals("Cannot introduce best bid because of there isn't "
                + "any relevant goods to sell in the order book", exc.getMessage());
    }

    @Test
    public void parse_requestAskFirstEntry_notOk() {
        FileReaderService fileReaderService = new FileReaderServiceImpl(parseService);
        Exception exc = assertThrows(RuntimeException.class, () -> fileReaderService.read(
                        new File("src/test/resources/requestAskFirstEntry.txt")));
        assertEquals("Cannot introduce best ask because of there isn't "
                + "any relevant goods to buy in the order book", exc.getMessage());
    }

    @Test
    public void parse_notEnoughGoodsToSubtractFromBids_notOk() {
        FileReaderService fileReaderService = new FileReaderServiceImpl(parseService);
        Exception exc = assertThrows(RuntimeException.class, () -> fileReaderService.read(
                new File("src/test/resources/notEnoughGoodsToSubtractFromBids.txt")));
        assertEquals("Cannot operate remove operation with 'SELL', because there isn't "
                + "enough stored goods corresponds to request quantity!", exc.getMessage());
    }

    @Test
    public void parse_notEnoughGoodsToSubtractFromAsks_notOk() {
        FileReaderService fileReaderService = new FileReaderServiceImpl(parseService);
        Exception exc = assertThrows(RuntimeException.class, () -> fileReaderService.read(
                new File("src/test/resources/notEnoughGoodsToSubtractFromAsks.txt")));
        assertEquals("Cannot operate remove operation with 'BUY', because there isn't "
                + "enough stored goods corresponds to request quantity!", exc.getMessage());
    }

    @Test
    public void parse_bigNumbersManipulation_ok() {
        FileReaderService fileReaderService = new FileReaderServiceImpl(parseService);
        fileReaderService.read(new File("src/test/resources/bigNumbersManipulation.txt"));
        assertEquals("950,1395\n10900,1295\n", parseService.getResult().toString());
    }

    @Test
    public void parse_updatesWithSizeZero_ok() {
        FileReaderService fileReaderService = new FileReaderServiceImpl(parseService);
        fileReaderService.read(new File("src/test/resources/updatesWithZero.txt"));
        assertEquals("9,2\n12,3\n", parseService.getResult().toString());
    }
}

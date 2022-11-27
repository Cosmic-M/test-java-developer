package service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import concurrent.DataBinder;
import model.Market;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ParseServiceImplTest {
    private ParseServiceImpl parseService;
    private DataBinder dataBinder;

    @BeforeEach
    public void initial() {
        dataBinder = new DataBinder();
        parseService = new ParseServiceImpl(dataBinder);
        Market.clear();
    }

    @Test
    public void parse_blinkFile_ok() {
        File inputFile = new File("src/test/resources/blinkFile.txt");
        Thread fileReaderThread = new Thread(new FileReaderServiceImpl(dataBinder, inputFile));
        Thread dataProcessorThread = new Thread(parseService);
        fileReaderThread.start();
        dataProcessorThread.start();
        try {
            dataProcessorThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted during join method: " + e);
        }
        assertEquals("", parseService.getResult().toString());
    }

    @Test
    public void parse_initialData_ok() {
        File inputFile = new File("src/test/resources/bestBidRefresh.txt");
        Thread fileReaderThread = new Thread(new FileReaderServiceImpl(dataBinder, inputFile));
        Thread dataProcessorThread = new Thread(parseService);
        fileReaderThread.start();
        dataProcessorThread.start();
        try {
            dataProcessorThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted during join method: " + e);
        }
        assertEquals("12,1\n14,3\n", parseService.getResult().toString());
    }

    @Test
    public void parse_findBesAskIfBidsAbsent_ok() {
        File inputFile = new File("src/test/resources/findBestAskIfBidsAbsent.txt");
        Thread fileReaderThread = new Thread(new FileReaderServiceImpl(dataBinder, inputFile));
        Thread dataProcessorThread = new Thread(parseService);
        fileReaderThread.start();
        dataProcessorThread.start();
        try {
            dataProcessorThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted during join method: " + e);
        }
        assertEquals("10,3\n", parseService.getResult().toString());
    }

    @Test
    public void parse_findBestAskAndBidAfterDifferentUpdates_ok() {
        File inputFile = new File("src/test/resources/findBestAskAndBidAfterDifferentUpdates");
        Thread fileReaderThread = new Thread(new FileReaderServiceImpl(dataBinder, inputFile));
        Thread dataProcessorThread = new Thread(parseService);
        fileReaderThread.start();
        dataProcessorThread.start();
        try {
            dataProcessorThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted during join method: " + e);
        }
        assertEquals("10,2\n12,2\n", parseService.getResult().toString());
    }

    @Test
    public void parse_ifSpecifiedPriseIsAbsentPrintZero_ok() {
        File inputFile = new File("src/test/resources/ifSpecifiedPrizeIsAbsentPrintZero.txt");
        Thread fileReaderThread = new Thread(new FileReaderServiceImpl(dataBinder, inputFile));
        Thread dataProcessorThread = new Thread(parseService);
        fileReaderThread.start();
        dataProcessorThread.start();
        try {
            dataProcessorThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted during join method: " + e);
        }
        assertEquals("0\n", parseService.getResult().toString());
    }

    @Test
    public void parse_manySizeRequests_ok() {
        File inputFile = new File("src/test/resources/manySizeRequests.txt");
        Thread fileReaderThread = new Thread(new FileReaderServiceImpl(dataBinder, inputFile));
        Thread dataProcessorThread = new Thread(parseService);
        fileReaderThread.start();
        dataProcessorThread.start();
        try {
            dataProcessorThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted during join method: " + e);
        }
        assertEquals("5\n5\n3\n3\n", parseService.getResult().toString());
    }

    @Test
    public void parse_removesSizeOutOfAsks_ok() {
        File inputFile = new File("src/test/resources/removesSizeOutOfAsks.txt");
        Thread fileReaderThread = new Thread(new FileReaderServiceImpl(dataBinder, inputFile));
        Thread dataProcessorThread = new Thread(parseService);
        fileReaderThread.start();
        dataProcessorThread.start();
        try {
            dataProcessorThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted during join method: " + e);
        }
        assertEquals("14,1\n", parseService.getResult().toString());
    }

    @Test
    public void parse_removesSizeOutOfBids_ok() {
        File inputFile = new File("src/test/resources/removesSizeOutOfBids.txt");
        Thread fileReaderThread = new Thread(new FileReaderServiceImpl(dataBinder, inputFile));
        Thread dataProcessorThread = new Thread(parseService);
        fileReaderThread.start();
        dataProcessorThread.start();
        try {
            dataProcessorThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted during join method: " + e);
        }
        assertEquals("10,1\n", parseService.getResult().toString());
    }

    @Test
    public void parse_fillDataConsequentlyStartsWithBid_ok() {
        File inputFile = new File("src/test/resources/fillDataConsequentlyStartsWithBid.txt");
        Thread fileReaderThread = new Thread(new FileReaderServiceImpl(dataBinder, inputFile));
        Thread dataProcessorThread = new Thread(parseService);
        fileReaderThread.start();
        dataProcessorThread.start();
        try {
            dataProcessorThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted during join method: " + e);
        }
        assertEquals("11,2\n13,4\n5\n", parseService.getResult().toString());
    }

    @Test
    public void parse_fillDataConsequentlyStartsWithAsk_ok() {
        File inputFile = new File("src/test/resources/fillDataConsequentlyStartsWithAsk.txt");
        Thread fileReaderThread = new Thread(new FileReaderServiceImpl(dataBinder, inputFile));
        Thread dataProcessorThread = new Thread(parseService);
        fileReaderThread.start();
        dataProcessorThread.start();
        try {
            dataProcessorThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted during join method: " + e);
        }
        assertEquals("11,3\n13,3\n0\n", parseService.getResult().toString());
    }

    @Test
    public void parse_manyCrossingData_ok() {
        File inputFile = new File("src/test/resources/manyCrossingData.txt");
        Thread fileReaderThread = new Thread(new FileReaderServiceImpl(dataBinder, inputFile));
        Thread dataProcessorThread = new Thread(parseService);
        fileReaderThread.start();
        dataProcessorThread.start();
        try {
            dataProcessorThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted during join method: " + e);
        }
        assertEquals("0\n1\n5\n10,2\n9,2\n", parseService.getResult().toString());
    }

    @Test
    public void parse_tooManyCrossingData_ok() {
        File inputFile = new File("src/test/resources/tooManyCrossingData.txt");
        Thread fileReaderThread = new Thread(new FileReaderServiceImpl(dataBinder, inputFile));
        Thread dataProcessorThread = new Thread(parseService);
        fileReaderThread.start();
        dataProcessorThread.start();
        try {
            dataProcessorThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted during join method: " + e);
        }
        assertEquals("6,2\n15,4\n0\n", parseService.getResult().toString());
    }

    @Test
    public void parse_findBestBidIfAsksAbsent_ok() {
        File inputFile = new File("src/test/resources/findBestBidIfAsksAbsent.txt");
        Thread fileReaderThread = new Thread(new FileReaderServiceImpl(dataBinder, inputFile));
        Thread dataProcessorThread = new Thread(parseService);
        fileReaderThread.start();
        dataProcessorThread.start();
        try {
            dataProcessorThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted during join method: " + e);
        }
        assertEquals("13,4\n", parseService.getResult().toString());
    }

    @Test
    public void parse_bestAskRefresh_ok() {
        File inputFile = new File("src/test/resources/parse_bestAskRefresh.txt");
        Thread fileReaderThread = new Thread(new FileReaderServiceImpl(dataBinder, inputFile));
        Thread dataProcessorThread = new Thread(parseService);
        fileReaderThread.start();
        dataProcessorThread.start();
        try {
            dataProcessorThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted during join method: " + e);
        }
        assertEquals("11,4\n13,5\n", parseService.getResult().toString());
    }

    @Test
    public void parse_bigComboWithBidsAsks_ok() {
        File inputFile = new File("src/test/resources/bigComboWithBidsAsks.txt");
        Thread fileReaderThread = new Thread(new FileReaderServiceImpl(dataBinder, inputFile));
        Thread dataProcessorThread = new Thread(parseService);
        fileReaderThread.start();
        dataProcessorThread.start();
        try {
            dataProcessorThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted during join method: " + e);
        }
        assertEquals("6,1\n15,1\n", parseService.getResult().toString());
    }

    @Test
    public void parse_repeatQuerySizeData_ok() {
        File inputFile = new File("src/test/resources/repeatQuerySizeData.txt");
        Thread fileReaderThread = new Thread(new FileReaderServiceImpl(dataBinder, inputFile));
        Thread dataProcessorThread = new Thread(parseService);
        fileReaderThread.start();
        dataProcessorThread.start();
        try {
            dataProcessorThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted during join method: " + e);
        }
        assertEquals("0\n0\n0\n0\n0\n8,1\n12,1\n0\n0\n", parseService.getResult().toString());
    }

    @Test
    public void parse_resetBestAskBeforeUpdate_ok() {
        File inputFile = new File("src/test/resources/resetBestAskBeforeUpdate.txt");
        Thread fileReaderThread = new Thread(new FileReaderServiceImpl(dataBinder, inputFile));
        Thread dataProcessorThread = new Thread(parseService);
        fileReaderThread.start();
        dataProcessorThread.start();
        try {
            dataProcessorThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted during join method: " + e);
        }
        assertEquals("8,5\n12,5\n", parseService.getResult().toString());
    }

    @Test
    public void parse_resetBestBidBeforeUpdate_ok() {
        File inputFile = new File("src/test/resources/resetBestBidBeforeUpdate.txt");
        Thread fileReaderThread = new Thread(new FileReaderServiceImpl(dataBinder, inputFile));
        Thread dataProcessorThread = new Thread(parseService);
        fileReaderThread.start();
        dataProcessorThread.start();
        try {
            dataProcessorThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted during join method: " + e);
        }
        assertEquals("9,5\n12,5\n", parseService.getResult().toString());
    }

    @Test
    public void parse_bigNumbersManipulation_ok() {
        File inputFile = new File("src/test/resources/bigNumbersManipulation.txt");
        Thread fileReaderThread = new Thread(new FileReaderServiceImpl(dataBinder, inputFile));
        Thread dataProcessorThread = new Thread(parseService);
        fileReaderThread.start();
        dataProcessorThread.start();
        try {
            dataProcessorThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted during join method: " + e);
        }
        assertEquals("950,1395\n10900,1295\n", parseService.getResult().toString());
    }

    @Test
    public void parse_updatesWithSizeZero_ok() {
        File inputFile = new File("src/test/resources/updatesWithZero.txt");
        Thread fileReaderThread = new Thread(new FileReaderServiceImpl(dataBinder, inputFile));
        Thread dataProcessorThread = new Thread(parseService);
        fileReaderThread.start();
        dataProcessorThread.start();
        try {
            dataProcessorThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted during join method: " + e);
        }
        assertEquals("9,2\n12,3\n", parseService.getResult().toString());
    }
}

package service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import service.FileReaderService;
import service.ParseService;

public class FileReaderServiceImpl implements FileReaderService {
    private static final int COMMA = 44;
    private static final int NEW_LINE_SYMBOL = 10;
    private static final int CARRIAGE_RETURN_SYMBOL = 13;
    private static final int LOW_A_SYMBOL = 97;
    private static final int LOW_B_SYMBOL = 98;
    private static final int LOW_O_SYMBOL = 111;
    private static final int LOW_S_SYMBOL = 115;
    private static final int LOW_U_SYMBOL = 117;
    private static final int UNDERSCORE = 95;
    private static final int ZERO = 48;
    private final ParseService parseService;

    public FileReaderServiceImpl(ParseService parseService) {
        this.parseService = parseService;
    }

    @Override
    public void read(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            int value = reader.read();
            while (value != -1) {
                int size;
                int price;
                switch (value) {
                    case LOW_U_SYMBOL: {
                        reader.skip(1);
                        price = getParamOneCondition(reader);
                        size = getParamOneCondition(reader);
                        value = reader.read();
                        if (value == LOW_A_SYMBOL) {
                            parseService.updateAsks(price, size);
                        } else {
                            parseService.updateBids(price, size);
                        }
                        break;
                    }
                    case LOW_O_SYMBOL: {
                        reader.skip(1);
                        value = reader.read();
                        final boolean subtractFromAsks = value == LOW_B_SYMBOL;
                        readerSkipTo(reader, COMMA);
                        size = getParamTwoCondition(reader);
                        if (subtractFromAsks) {
                            parseService.removeFromAsks(size);
                        } else {
                            parseService.removeFromBids(size);
                        }
                        break;
                    }
                    default: {
                        reader.skip(1);
                        value = reader.read();
                        if (value == LOW_S_SYMBOL) {
                            readerSkipTo(reader, COMMA);
                            price = getParamTwoCondition(reader);
                            parseService.findSizeByPrice(price);
                            break;
                        }
                        readerSkipTo(reader, UNDERSCORE);
                        value = reader.read();
                        if (value == LOW_A_SYMBOL) {
                            parseService.findBestAsk();
                        } else {
                            parseService.findBestBid();
                        }
                    }
                }
                if (value != 10) {
                    readerSkipTo(reader, 10);
                }
                value = reader.read();
            }
        } catch (IOException exc) {
            throw new RuntimeException("Cannot read file " + file.getName());
        }
    }

    private void readerSkipTo(BufferedReader reader, int dec) throws IOException {
        int value = reader.read();
        while (value != dec && value != -1) {
            value = reader.read();
        }
    }

    private int getParamOneCondition(BufferedReader reader) throws IOException {
        int value = reader.read();
        int result = value - ZERO;
        value = reader.read();
        while (value != COMMA) {
            result *= 10;
            result += value - ZERO;
            value = reader.read();
        }
        return result;
    }

    private int getParamTwoCondition(BufferedReader reader) throws IOException {
        int value = reader.read();
        int result = value - ZERO;
        value = reader.read();
        while (value != NEW_LINE_SYMBOL && value != CARRIAGE_RETURN_SYMBOL) {
            result *= 10;
            result += value - ZERO;
            value = reader.read();
        }
        return result;
    }
}

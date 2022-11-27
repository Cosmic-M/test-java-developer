package service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import concurrent.DataBinder;

public class FileReaderServiceImpl implements Runnable {
    private final File file;
    private final DataBinder dataBinder;
    private static final int PLUG = -1;
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

    public FileReaderServiceImpl(DataBinder dataBinder, File file) {
        this.dataBinder = dataBinder;
        this.file = file;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            int value = reader.read();
            while (value != -1) {
                int size, price;
                boolean subtractFromAsks;
                switch (value) {
                    case LOW_U_SYMBOL:
                        reader.skip(1L);
                        value = reader.read();
                        price = value - ZERO;
                        value = reader.read();
                        while (value != COMMA) {
                            price *= 10;
                            price += value - ZERO;
                            value = reader.read();
                        }
                        value = reader.read();
                        size = value - ZERO;
                        value = reader.read();
                        while (value != COMMA) {
                            size *= 10;
                            size += value - ZERO;
                            value = reader.read();
                        }
                        value = reader.read();
                        if (value == LOW_A_SYMBOL) {
                            dataBinder.renewDataNotify(1, price, size);
                            break;
                        }
                        dataBinder.renewDataNotify(2, price, size);
                        break;
                    case LOW_O_SYMBOL:
                        reader.skip(1L);
                        value = reader.read();
                        subtractFromAsks = (value == LOW_B_SYMBOL);
                        readerSkipTo(reader, COMMA);
                        value = reader.read();
                        size = value - ZERO;
                        value = reader.read();
                        while (value != NEW_LINE_SYMBOL && value != CARRIAGE_RETURN_SYMBOL) {
                            size *= 10;
                            size += value - ZERO;
                            value = reader.read();
                        }
                        if (subtractFromAsks) {
                            dataBinder.renewDataNotify(3, PLUG, size);
                            break;
                        }
                        dataBinder.renewDataNotify(4, PLUG, size);
                        break;
                    default:
                        reader.skip(1L);
                        value = reader.read();
                        if (value == LOW_S_SYMBOL) {
                            readerSkipTo(reader, COMMA);
                            value = reader.read();
                            price = value - ZERO;
                            value = reader.read();
                            while (value != NEW_LINE_SYMBOL && value != CARRIAGE_RETURN_SYMBOL) {
                                price *= 10;
                                price += value - ZERO;
                                value = reader.read();
                            }
                            dataBinder.renewDataNotify(7, price, PLUG);
                            break;
                        }
                        readerSkipTo(reader, UNDERSCORE);
                        value = reader.read();
                        if (value == LOW_A_SYMBOL) {
                            dataBinder.renewDataNotify(5, PLUG, PLUG);
                            break;
                        }
                        dataBinder.renewDataNotify(6, PLUG, PLUG);
                        break;
                }
                if (value != NEW_LINE_SYMBOL)
                    readerSkipTo(reader, NEW_LINE_SYMBOL);
                value = reader.read();
            }
            dataBinder.renewDataNotify(PLUG, PLUG, PLUG);
        } catch (IOException exc) {
        throw new RuntimeException("Cannot read file " + file.getName());
    }
}

    private static void readerSkipTo(BufferedReader reader, int dec) throws IOException {
        int value = reader.read();
        while (value != dec && value != -1)
            value = reader.read();
    }
}

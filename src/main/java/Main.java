
import service.impl.ParseService;
import service.impl.ParseServiceImpl;
import java.io.*;

public class Main {
    private static int price;
    private static int size;
    private static int pow;
    private static boolean subtractFromAsks = true;
    private static BufferedReader reader;

    public static void main(String... args) {
        ParseService parseService = new ParseServiceImpl();
        File file = new File("input.txt");
        try {
            reader = new BufferedReader(new FileReader(file));
            int value = reader.read();
            while (value != -1) {
                switch (value) {
                    case 117 -> {
                        reader.skip(1);
                        value = reader.read();
                        pow = 1;
                        price = 0;
                        size = 0;
                        while (value != 44) {
                            price *= pow;
                            price += value - 48;
                            pow *= 10;
                            value = reader.read();
                        }
                        value = reader.read();
                        pow = 1;
                        while (value != 44) {
                            size *= pow;
                            size += value - 48;
                            pow *= 10;
                            value = reader.read();
                        }
                        value = reader.read();
                        if (value == 97) {
                            parseService.updateAsks(price, size);
                        } else {
                            parseService.updateBids(price, size);
                        }
                    }
                    case 111 -> {
                        reader.skip(1);
                        value = reader.read();
                        subtractFromAsks = value == 98;
                        readerSkipTo(44);
                        value = reader.read();
                        pow = 1;
                        size = 0;
                        while (value != 13) {
                            size *= pow;
                            size += value - 48;
                            pow *= 10;
                            value = reader.read();
                        }
                        if (subtractFromAsks) {
                            parseService.removeFromBestAsks(size);
                        } else {
                            parseService.removeFromBestBids(size);
                        }
                    }
                    default -> {
                        reader.skip(1);
                        value = reader.read();
                        if (value == 115) {
                            readerSkipTo(44);
                            value = reader.read();
                            pow = 1;
                            price = 0;
                            while (value != 13) {
                                price *= pow;
                                price += value - 48;
                                pow *= 10;
                                value = reader.read();
                            }
                            parseService.markSizeByPrice(price);
                            break;
                        }
                        readerSkipTo(95);
                        value = reader.read();
                        if (value == 97) {
                            parseService.markBestAsk();
                        } else {
                            parseService.markBestBid();
                        }
                    }
                }
                readerSkipTo(10);
                value = reader.read();
            }
            file = new File("output.txt");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(ParseServiceImpl.getResult().toString());
            } catch (IOException e) {
                throw new RuntimeException("Can't write data to file " + file.getName() + e);
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot read file: " + file.getName() + e);
        }
    }

    private static void readerSkipTo(int dec) throws IOException {
        int value = reader.read();
        while (value != dec) {
            value = reader.read();
        }
    }
}

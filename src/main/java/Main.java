import java.util.List;

import model.Market;
import service.FileReaderService;
import service.impl.FileReaderServiceImpl;
import service.FileWriterService;
import service.impl.FileWriterServiceImpl;
import service.ParseService;
import service.impl.ParseServiceImpl;

public class Main {
    public static void main(String...args) {
        FileReaderService fileReaderService = new FileReaderServiceImpl();
        List<String> stringList = fileReaderService.readFile("input.txt");
        Market market = new Market();
        ParseService parseService = new ParseServiceImpl(market);
        List<String> toFile = parseService.parse(stringList);
        FileWriterService fileWriterService = new FileWriterServiceImpl();
        fileWriterService.writeFile("output.txt", toFile);
    }
}

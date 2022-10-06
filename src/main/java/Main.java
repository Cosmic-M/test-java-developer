import service.FileReaderService;
import service.impl.FileReaderServiceImpl;
import service.FileWriterService;
import service.impl.FileWriterServiceImpl;
import service.ParseService;
import service.impl.ParseServiceImpl;

public class Main {
    public static void main(String...args) {
        FileReaderService fileReaderService = new FileReaderServiceImpl();
        StringBuilder input = fileReaderService.readFile("input.txt");
        ParseService parseService = new ParseServiceImpl();
        StringBuilder output = parseService.parse(input);
        FileWriterService fileWriterService = new FileWriterServiceImpl();
        fileWriterService.writeFile("output.txt", output);
    }
}

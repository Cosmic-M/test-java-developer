import service.FileReaderService;
import service.impl.FileReaderServiceImpl;
import service.FileWriterService;
import service.impl.FileWriterServiceImpl;

public class Main {
    public static void main(String...args) {
        FileReaderService fileReaderService = new FileReaderServiceImpl();
        StringBuilder output = fileReaderService.readFile("input.txt");
        FileWriterService fileWriterService = new FileWriterServiceImpl();
        fileWriterService.writeFile("output.txt", output);
    }
}

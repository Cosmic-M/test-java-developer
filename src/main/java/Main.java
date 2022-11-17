import java.io.File;
import java.io.IOException;
import service.FileReaderService;
import service.FileWriterService;
import service.ParseService;
import service.impl.FileReaderServiceImpl;
import service.impl.FileWriterServiceImpl;
import service.impl.ParseServiceImpl;

public class Main {
    public static void main(String...args) {
        File outputFile = new File("output.txt");
        try {
            outputFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException("Cannot create file " + outputFile.getName());
        }
        ParseService parseService = new ParseServiceImpl();
        FileReaderService fileReaderService = new FileReaderServiceImpl(parseService);
        fileReaderService.read(new File("input.txt"));
        StringBuilder dataToWrite = parseService.getResult();
        FileWriterService fileWriterService = new FileWriterServiceImpl();
        fileWriterService.write(outputFile, dataToWrite);
    }
}

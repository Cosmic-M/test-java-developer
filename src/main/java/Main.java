import java.io.File;
import concurrent.DataBinder;
import service.FileWriterService;
import service.impl.FileReaderServiceImpl;
import service.impl.FileWriterServiceImpl;
import service.impl.ParseServiceImpl;

public class Main {
    public static void main(String...args) {
        File outputFile = new File("output.txt");
        DataBinder dataBinder = new DataBinder();
        File inputFile = new File("input.txt");
        ParseServiceImpl parseService = new ParseServiceImpl(dataBinder);

        Thread fileReaderThread = new Thread(new FileReaderServiceImpl(dataBinder, inputFile));
        Thread dataProcessorThread = new Thread(parseService);
        fileReaderThread.start();
        dataProcessorThread.start();
        try {
            dataProcessorThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted during join method: " + e);
        }
        StringBuilder dataToWrite = parseService.getResult();
        FileWriterService fileWriterService = new FileWriterServiceImpl();
        fileWriterService.write(outputFile, dataToWrite);
    }
}

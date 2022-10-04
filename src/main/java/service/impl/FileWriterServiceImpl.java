package service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import service.FileWriterService;

public class FileWriterServiceImpl implements FileWriterService {
    @Override
    public void writeFile(String filePath, List<String> list) {
        File file = new File(filePath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            if (list.get(0) != null) {
                writer.write(list.get(0));
            }
            for (int i = 1 ; i < list.size(); i++) {
                writer.write(System.lineSeparator());
                writer.write(list.get(i));
            }
        } catch (IOException e) {
            throw new RuntimeException("Can't write data to file " + file.getName() + e);
        }
    }
}

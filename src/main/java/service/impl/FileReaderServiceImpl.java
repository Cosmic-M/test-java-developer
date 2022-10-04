package service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import service.FileReaderService;

public class FileReaderServiceImpl implements FileReaderService {
    @Override
    public List<String> readFile(String filePath) {
        List<String> strings;
        File file = new File(filePath);
        try {
            strings = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException("Cannot read file: " + file.getName() + e);
        }
        return strings;
    }
}

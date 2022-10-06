package service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import service.FileReaderService;

public class FileReaderServiceImpl implements FileReaderService {
    @Override
    public StringBuilder readFile(String filePath) {
        StringBuilder builder = new StringBuilder();
        File file = new File(filePath);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            int value = reader.read();
            while (value != -1) {
                builder.append((char) value);
                value = reader.read();
            }
            return builder.isEmpty() ? builder : builder.append("\r\nq,");
        } catch (IOException e) {
            throw new RuntimeException("Cannot read file: " + file.getName() + e);
        }
    }
}

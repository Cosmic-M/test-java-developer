package service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import service.FileReaderService;

public class FileReaderServiceImpl implements FileReaderService {
    private final ParseServiceImpl parseService = new ParseServiceImpl();

    @Override
    public StringBuilder readFile(String filePath) {
        File file = new File(filePath);
        StringBuilder builder = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            int value = reader.read();
            while (value != -1) {
                if (value == 10) {
                    parseService.parse(builder);
                    builder.delete(0, builder.length());
                    value = reader.read();
                    continue;
                } else if (value == 13) {
                    value = reader.read();
                    continue;
                }
                builder.append((char) value);
                value = reader.read();
            }
            return parseService.getResult();
        } catch (IOException e) {
            throw new RuntimeException("Cannot read file: " + file.getName() + e);
        }
    }
}

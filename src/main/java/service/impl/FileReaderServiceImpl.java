package service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import service.FileReaderService;

public class FileReaderServiceImpl implements FileReaderService {
    private static final String EXECUTIVE_FOR_WINDOWS = "\r\n";
    private static final String EXECUTIVE2_FOR_LINUX = "\n";

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
            StringBuilder cleanBuilder = new StringBuilder(builder.toString()
                    .replaceAll(EXECUTIVE_FOR_WINDOWS, "")
                    .replaceAll(EXECUTIVE2_FOR_LINUX, ""));
            return cleanBuilder.isEmpty() ? cleanBuilder : cleanBuilder.append("q,");
        } catch (IOException e) {
            throw new RuntimeException("Cannot read file: " + file.getName() + e);
        }
    }
}

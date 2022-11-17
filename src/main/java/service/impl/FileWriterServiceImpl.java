package service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import service.FileWriterService;

public class FileWriterServiceImpl implements FileWriterService {
    @Override
    public void write(File file, StringBuilder output) {
        if (output.length() != 0) {
            output.delete(output.length() - 1, output.length());
        }
        try {
            Files.write(file.toPath(), output.toString().getBytes());
        } catch (IOException exc) {
            throw new RuntimeException("Cannot write file " + file.getName());
        }
    }
}

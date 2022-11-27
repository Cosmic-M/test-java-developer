package service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import service.FileWriterService;

public class FileWriterServiceImpl implements FileWriterService {
    public void write(File file, StringBuilder output) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            try {
                if (output.length() != 0)
                    output.delete(output.length() - 1, output.length());
                writer.write(output.toString());
                writer.close();
            } catch (Throwable throwable) {
                try {
                    writer.close();
                } catch (Throwable throwable1) {
                    throwable.addSuppressed(throwable1);
                }
                throw throwable;
            }
        } catch (IOException e) {
            throw new RuntimeException("Can't write data to file " + file.getName() + e);
        }
    }
}

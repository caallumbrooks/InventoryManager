package org.example;
import java.io.*;

public class FileManager {
    public static void clearTransactionsFile(String transactionsPath) throws IOException {
        FileWriter writer = new FileWriter(transactionsPath);
        writer.close();
    }
}
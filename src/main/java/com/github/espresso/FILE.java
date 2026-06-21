package com.github.espresso;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.io.IOException;

public class FILE {

    // Base File
    private static class BaseFile{
        protected String filePath;
        public BaseFile(String filePath){
            this.filePath = filePath;
        }
    }

    // Static Utility Functions
    public static boolean exists(String filePath){
        return Files.exists(Paths.get(filePath));
    }

    public static void create(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            throw new FileAlreadyExistsException("File " + filePath + " already exists");
        }
        Files.createFile(path);
    }

    public static void delete(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        boolean deleted = Files.deleteIfExists(path);
        if (!deleted) {
            throw new IOException("File does not exist: " + filePath);
        }
    }


    // Read Function
    public static class Read extends BaseFile{

        public Read(String filePath){
            super(filePath);
        }

        private int currentLine = 0;
        private List<String> cachedLines = null;

        private List<String> getLines() throws IOException {
            if (cachedLines == null) {
                cachedLines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
            }
            return cachedLines;
        }

        //It will return a list of all the lines present in the file
        public List<String> readAllLines() throws IOException {
            return getLines();
        }

        public void resetReader() {
            currentLine = 0;
            cachedLines = null;
        }

        public int currentLineNumber(){
            return currentLine + 1;
        }

        public boolean hasNextLine() throws IOException {
            return currentLine < getLines().size();
        }

        public String readLine(int n) throws IOException{
            List<String> lines = getLines();
            if (n < 1 || n > lines.size()) throw new IndexOutOfBoundsException("Line " + n + " does not exist.");
            return lines.get(n - 1);
        }

        public String readLine() throws IOException {
            List<String> lines = getLines();
            if (currentLine >= lines.size()) {
                throw new NoSuchElementException("End of file reached for: " + filePath);
            }
            return lines.get(currentLine++);
        }
    }

    public static class Append extends BaseFile{
        public Append(String filePath){
            super(filePath);
        }

        public void append(String line) throws IOException {
            Files.writeString(  Paths.get(filePath),
                                line + System.lineSeparator(),
                                StandardCharsets.UTF_8,
                                StandardOpenOption.CREATE,              // Creates the File if it doesn't Exist
                                StandardOpenOption.APPEND);             // Appends it to the end of the file
        }

        public void append(List<String> lines) throws IOException {
            Files.write(Paths.get(filePath),
                        lines, StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND);
        }
    }

    public static class Write extends BaseFile{
        public Write(String filePath){
            super(filePath);
        }

        public void write(String line) throws IOException {
            Files.writeString(  Paths.get(filePath),
                                line + System.lineSeparator(),
                                StandardCharsets.UTF_8,
                                StandardOpenOption.CREATE,              // Creates the File if it doesn't Exist
                                StandardOpenOption.TRUNCATE_EXISTING);  // Erases existing content and Overwrites
        }

        public void write(List<String> lines) throws IOException {
            Files.write(Paths.get(filePath),
                        lines, StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING);
        }
    }
}
package com.github.espresso;

import java.nio.file.StandardOpenOption;
import java.util.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FILE {

    private static class BaseFile{
        protected String filePath;
        public BaseFile(String filePath){
            this.filePath = filePath;
        }
    }

    public static class Read extends BaseFile{
        public Read(String filePath){
            super(filePath);
        }

        //It will return a list of all the lines present in the file
        public List<String> readAllLines() throws IOException {
            return Files.readAllLines(Paths.get(filePath));
        }
    }

    public static class Append extends BaseFile{
        public Append(String filePath){
            super(filePath);
        }

        public void append(String line) throws IOException {

            line = line + System.lineSeparator();

            Files.write(Paths.get(filePath),
                        line.getBytes(),
                        StandardOpenOption.CREATE,              // Creates the File if it doesn't Exist
                        StandardOpenOption.APPEND);             // Appends it to the end of the file
        }
    }

    public static class Write extends BaseFile{
        public Write(String filePath){
            super(filePath);
        }
        public void write(String line) throws IOException {
            line = line + System.lineSeparator();

            Files.write(Paths.get(filePath),
                        line.getBytes(),
                        StandardOpenOption.CREATE,              // Creates the File if it doesn't Exist
                        StandardOpenOption.TRUNCATE_EXISTING);  // Erases existing content and Overwrites
        }


    }
}
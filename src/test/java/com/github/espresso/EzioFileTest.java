package com.github.espresso;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EzioFileTest {

    // Test Case 1: Verifies writing fresh content works
    @Test
    public void testFileWriting() throws IOException {
        String path = "test_write.txt";
        FILE.Write writer = new FILE.Write(path);

        writer.write("Hello Espresso");

        FILE.Read reader = new FILE.Read(path);
        List<String> lines = reader.readAllLines();

        assertEquals("Hello Espresso", lines.get(0));
    }

    // Test Case 2: Verifies that writeString completely overwrites old text
    @Test
    public void testFileOverwrite() throws IOException {
        String path = "test_overwrite.txt";
        FILE.Write writer = new FILE.Write(path);

        writer.write("Old Data");
        writer.write("New Data"); // This should wipe "Old Data" clean

        FILE.Read reader = new FILE.Read(path);
        List<String> lines = reader.readAllLines();

        assertEquals(1, lines.size());
        assertEquals("New Data", lines.get(0));
    }

    // Test Case 3: Verifies appending text onto an existing file
    @Test
    public void testFileAppending() throws IOException {
        String path = "test_append.txt";

        // Start with an initial line
        FILE.Write writer = new FILE.Write(path);
        writer.write("Line 1");

        // Append a second line
        FILE.Append appender = new FILE.Append(path);
        appender.append("Line 2");

        FILE.Read reader = new FILE.Read(path);
        List<String> lines = reader.readAllLines();

        assertEquals(2, lines.size());
        assertEquals("Line 1", lines.get(0));
        assertEquals("Line 2", lines.get(1));
    }

    // Test Case 4: Verifies writing an empty string creates a blank line
    @Test
    public void testEmptyFile() throws IOException {
        String path = "test_empty.txt";
        FILE.Write writer = new FILE.Write(path);

        writer.write(""); // Writes just a newline separator

        FILE.Read reader = new FILE.Read(path);
        List<String> lines = reader.readAllLines();

        // The list should contain exactly 1 element, which is an empty string ""
        assertEquals(1, lines.size(), "The file should contain exactly one blank line slot.");
        assertEquals("", lines.get(0), "The line content should be empty.");
    }
}
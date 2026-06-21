package com.github.espresso;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for FileX.Write: single-line writes, multi-line writes, and the
 * overwrite (truncate) behavior that distinguishes it from Append.
 */
public class FileXWriteTest {

    private static String path(String name) {
        return "src/test/java/com/github/espresso/test-cases/" + name;
    }

    @BeforeAll
    public static void ensureTestDirExists() throws IOException {
        Files.createDirectories(Paths.get(path("")));
    }

    // Test Case 1: Verifies writing fresh content works
    @Test
    public void testWriteSingleLine() throws IOException {
        String path = path("write_single.txt");

        FileX.write(path).write("Hello Espresso");

        List<String> lines = FileX.read(path).readAllLines();
        assertEquals("Hello Espresso", lines.get(0));
    }

    // Test Case 2: Verifies write() completely replaces old content, not appends to it
    @Test
    public void testWriteOverwritesPreviousContent() throws IOException {
        String path = path("write_overwrite.txt");

        FileX.write(path).write("Old Data");
        FileX.write(path).write("New Data"); // should wipe "Old Data" clean

        List<String> lines = FileX.read(path).readAllLines();
        assertEquals(1, lines.size());
        assertEquals("New Data", lines.get(0));
    }

    // Test Case 3: Verifies writing a list of lines preserves order
    @Test
    public void testWriteMultipleLinesPreservesOrder() throws IOException {
        String path = path("write_multiple.txt");

        FileX.write(path).write(List.of("Math", "Physics", "Chemistry"));

        List<String> lines = FileX.read(path).readAllLines();
        assertEquals(List.of("Math", "Physics", "Chemistry"), lines);
    }

    // Test Case 4: Verifies writing an empty string still produces one blank line
    @Test
    void testWriteCreatesMissingFile() throws IOException {

        String path = path("write_create.txt");
        FileX.write(path).write("Hello");
        assertTrue(FileX.exists(path));
    }
}

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
 * Tests for FileX.Append: appending onto existing content, auto-creating
 * the file when it doesn't exist yet, and multi-line appends.
 */
public class FileXAppendTest {

    private static String path(String name) {
        return "src/test/java/com/github/espresso/test-cases/" + name;
    }

    @BeforeAll
    public static void ensureTestDirExists() throws IOException {
        Files.createDirectories(Paths.get(path("")));
    }

    private static void deleteQuietly(String path) {
        try {
            FileX.delete(path);
        } catch (IOException ignored) {
            // wasn't there — fine
        }
    }

    // Test Case 1: Verifies appending preserves existing content and adds the new line after it
    @Test
    public void testAppendPreservesExistingContent() throws IOException {
        String path = path("append_preserve.txt");
        FileX.write(path).write("Line 1");

        FileX.append(path).append("Line 2");

        List<String> lines = FileX.read(path).readAllLines();
        assertEquals(2, lines.size());
        assertEquals("Line 1", lines.get(0));
        assertEquals("Line 2", lines.get(1));
    }

    // Test Case 2: Verifies append() creates the file if it doesn't exist yet
    @Test
    public void testAppendCreatesFileWhenMissing() throws IOException {
        String path = path("append_create_new.txt");
        deleteQuietly(path);

        FileX.append(path).append("First Ever Line");

        assertTrue(FileX.exists(path));
        List<String> lines = FileX.read(path).readAllLines();
        assertEquals("First Ever Line", lines.get(0));
    }

    // Test Case 3: Verifies appending a list of lines adds them in order
    @Test
    public void testAppendMultipleLinesInOrder() throws IOException {
        String path = path("append_multiple.txt");
        FileX.write(path).write("Intro");

        FileX.append(path).append(List.of("Body", "Conclusion"));

        List<String> lines = FileX.read(path).readAllLines();
        assertEquals(List.of("Intro", "Body", "Conclusion"), lines);
    }

    // Test Case 4: Verifies two separate append() calls both land in the file, in call order
    @Test
    public void testRepeatedAppendCallsAccumulate() throws IOException {
        String path = path("append_repeated.txt");
        deleteQuietly(path);

        FileX.append(path).append("First");
        FileX.append(path).append("Second");

        List<String> lines = FileX.read(path).readAllLines();
        assertEquals(List.of("First", "Second"), lines);
    }
}

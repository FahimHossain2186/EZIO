package com.github.espresso;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for FileX.Read: bulk reading, sequential reading, EOF behavior,
 * indexed access, and the cache/refresh mechanism.
 */
public class FileXReadTest {

    private static String path(String name) {
        return "src/test/java/com/github/espresso/test-cases/" + name;
    }

    @BeforeAll
    public static void ensureTestDirExists() throws IOException {
        Files.createDirectories(Paths.get(path("")));
    }

    // Test Case 1: readAllLines() should return every line, in order
    @Test
    public void testReadAllLinesReturnsAllContent() throws IOException {
        String path = path("read_all_lines.txt");
        FileX.write(path).write(List.of("Math", "Physics", "Chemistry"));

        List<String> lines = FileX.read(path).readAllLines();

        assertEquals(List.of("Math", "Physics", "Chemistry"), lines);
    }

    // Test Case 2: readLine() should advance sequentially, one call per line
    @Test
    public void testSequentialReadLineAdvancesCursor() throws IOException {
        String path = path("read_sequential.txt");
        FileX.write(path).write(List.of("Line 1", "Line 2"));

        FileX.Read reader = FileX.read(path);

        assertEquals(1, reader.nextLineNumber());
        assertEquals("Line 1", reader.readLine());
        assertEquals(2, reader.nextLineNumber());
        assertEquals("Line 2", reader.readLine());
    }

    // Test Case 3: hasNextLine() should turn false at EOF, and readLine() should throw past it
    @Test
    public void testHasNextLineFalseAtEndOfFile() throws IOException {
        String path = path("read_eof.txt");
        FileX.write(path).write("Only Line");

        FileX.Read reader = FileX.read(path);
        reader.readLine(); // consume the only line

        assertFalse(reader.hasNextLine());
        assertThrows(NoSuchElementException.class, reader::readLine);
    }

    // Test Case 4: readLine(n) should fetch any line directly without disturbing the cursor
    @Test
    public void testReadSpecificLineByNumber() throws IOException {
        String path = path("read_by_number.txt");
        FileX.write(path).write(List.of("A", "B", "C"));

        FileX.Read reader = FileX.read(path);

        assertEquals("B", reader.readLine(2));
        assertThrows(IndexOutOfBoundsException.class, () -> reader.readLine(99));
    }

    // Test Case 5: refresh() should drop the cache so updated file content is picked up
    @Test
    public void testRefreshPicksUpExternallyUpdatedContent() throws IOException {
        String path = path("read_refresh.txt");
        FileX.write(path).write("Original");

        FileX.Read reader = FileX.read(path);
        assertEquals("Original", reader.readAllLines().get(0)); // primes the cache

        FileX.write(path).write("Updated"); // change file after caching
        reader.refresh();

        assertEquals("Updated", reader.readAllLines().get(0));
    }

    @Test
    void testResetReaderResetsCursor() throws IOException {

        String path = path("read_reset.txt");

        FileX.write(path).write(List.of("A", "B"));

        FileX.Read reader = FileX.read(path);

        assertEquals("A", reader.readLine());

        reader.resetReader();

        assertEquals(1, reader.nextLineNumber());
        assertEquals("A", reader.readLine());
    }
}

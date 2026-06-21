package com.github.espresso;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for BaseFile's shared validation logic (path + charset checks).
 *
 * BaseFile is a private nested class, so it can't be instantiated or
 * tested directly. Instead, these tests go through the public factory
 * methods (FileX.read/write/append) — since all three subclasses share
 * the exact same BaseFile constructor, testing through any one of them
 * proves the shared validation works for all of them.
 */
public class FileXBaseFileTest {

    private static String path(String name) {
        return "src/test/java/com/github/espresso/test-cases/" + name;
    }

    // Test Case 1: Null path should be rejected, regardless of which factory is used
    @Test
    public void testNullPathThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> FileX.read(null));
    }

    // Test Case 2: Blank (whitespace-only) path should be rejected
    @Test
    public void testBlankPathThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> FileX.write("   "));
    }

    // Test Case 3: Empty string path should be rejected
    @Test
    public void testEmptyPathThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> FileX.append(""));
    }

    // Test Case 4: Null charset should be rejected even if the path is valid
    @Test
    public void testNullCharsetThrowsIllegalArgumentException() {
        String validPath = path("basefile_charset.txt");
        assertThrows(IllegalArgumentException.class, () -> FileX.write(validPath, null));
    }

    // Test Case 5: A valid path and charset should construct without throwing
    @Test
    public void testValidPathAndCharsetDoesNotThrow() {
        String validPath = path("basefile_valid.txt");
        assertDoesNotThrow(() -> FileX.read(validPath, StandardCharsets.UTF_8));
    }

    @Test
    void testCustomCharsetRoundTrip() throws IOException {
        String path = path("utf16.txt");
        String text = "বাংলা";
        FileX.write(path, StandardCharsets.UTF_16).write(text);
        String result = FileX.read(path, StandardCharsets.UTF_16).readLine();
        assertEquals(text, result);
    }

    @Test
    void testDefaultUtf8RoundTrip() throws IOException {
        String path = path("utf8.txt");
        String text = "こんにちは";
        FileX.write(path).write(text);
        assertEquals(text,FileX.read(path).readLine());
    }
}

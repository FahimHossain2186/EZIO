package com.github.espresso;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for FileX's static utility methods: exists, create,
 * createParentDirectories, and delete.
 */
public class FileXUtilityTest {

    private static String path(String name) {
        return "src/test/java/com/github/espresso/test-cases/" + name;
    }

    @BeforeAll
    public static void ensureTestDirExists() throws IOException {
        Files.createDirectories(Paths.get(path("")));
    }

    // This runs automatically right before EVERY test case to ensure an absolute clean slate
    @BeforeEach
    public void wipeTestEnvironment() {
        deleteQuietly(path("util_missing.txt"));
        deleteQuietly(path("util_create.txt"));
        deleteQuietly(path("util_delete.txt"));
        deleteQuietly(path("nested/deeper/file.txt"));
        try {
            // Remove the nested folders if they exist so testCreateWithDirectories can re-create them cleanly
            Files.deleteIfExists(Paths.get(path("nested/deeper")));
            Files.deleteIfExists(Paths.get(path("nested")));
        } catch (IOException ignored) {
            // Folder wasn't there or couldn't be deleted — fine for test prep
        }
    }

    private static void deleteQuietly(String path) {
        try {
            Files.deleteIfExists(Paths.get(path));
        } catch (IOException ignored) {
            // File wasn't there — perfect for a clean state
        }
    }

    // Test Case 1: A file that was never created should report as not existing
    @Test
    public void testExistsReturnsFalseForMissingFile() {
        String path = path("util_missing.txt");
        assertFalse(FileX.exists(path));
    }

    // Test Case 2: Verifies create() actually produces a file on disk
    @Test
    public void testCreateMakesFileExist() throws IOException {
        String path = path("util_create.txt");

        FileX.create(path);

        assertTrue(FileX.exists(path));
    }

    // Test Case 3: Verifies createWithDirectories() builds missing folders
    @Test
    void testCreateWithDirectoriesCreatesEverything() throws IOException {
        String path = path("nested/deeper/file.txt");

        FileX.createWithDirectories(path);

        assertTrue(FileX.exists(path));
    }

    // Test Case 4: Verifies delete() removes an existing file
    @Test
    public void testDeleteRemovesExistingFile() throws IOException {
        String path = path("util_delete.txt");
        FileX.create(path);
        assertTrue(FileX.exists(path));

        FileX.delete(path);

        assertFalse(FileX.exists(path));
    }

    // Test Case 5: Verifies delete() throws when the file doesn't exist
    @Test
    public void testDeleteThrowsWhenFileMissing() {
        String path = path("util_does_not_exist.txt");

        assertThrows(IOException.class, () -> FileX.delete(path));
    }
}
package com.github.espresso;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

public class FileX {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    // ===========================================================
    //  Factory methods — entry points into the library
    // ===========================================================

    /** Creates a reader for the file at {@code path}, using UTF-8. */
    public static Read read(String path) {
        return new Read(path, DEFAULT_CHARSET);
    }

    /** Creates a reader for the file at {@code path}, using a custom charset. */
    public static Read read(String path, Charset charset) {
        return new Read(path, charset);
    }

    /** Creates a writer (overwrite mode) for the file at {@code path}, using UTF-8. */
    public static Write write(String path) {
        return new Write(path, DEFAULT_CHARSET);
    }

    /** Creates a writer (overwrite mode) for the file at {@code path}, using a custom charset. */
    public static Write write(String path, Charset charset) {
        return new Write(path, charset);
    }

    /** Creates an appender for the file at {@code path}, using UTF-8. */
    public static Append append(String path) {
        return new Append(path, DEFAULT_CHARSET);
    }

    /** Creates an appender for the file at {@code path}, using a custom charset. */
    public static Append append(String path, Charset charset) {
        return new Append(path, charset);
    }

    /* =======================
       Static Utility Methods
       ======================= */

    /**
     * Checks whether a file exists.
     *
     * @param filePath path to the file
     * @return true if the file exists, false otherwise
     */
    public static boolean exists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }

    /**
     * Creates a new, empty file.
     *
     * <p>The parent directory must already exist — use
     * {@link #createParentDirectories(String)} first if it might not.
     *
     * @param filePath path of the file to create
     * @throws FileAlreadyExistsException if a file already exists at that path
     * @throws IOException if the file could not be created for another reason
     */
    public static void create(String filePath) throws IOException {
        Files.createFile(Paths.get(filePath));
    }

    /**
     * Creates any missing parent directories for the given path.
     *
     * <p>Example: for {@code "docs/projects/notes.txt"}, this creates the
     * {@code docs/projects} directory tree if it doesn't already exist.
     * Does nothing if the parent directories already exist.
     *
     * @param filePath path whose parent directories should be created
     * @throws IOException if the directories could not be created
     */
    public static void createParentDirectories(String filePath) throws IOException {
        Path parent = Paths.get(filePath).getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
    }

    /**
     * Creates parent directories if necessary
     * and then creates the file.
     */
    public static void createWithDirectories(
            String filePath)
            throws IOException {

        createParentDirectories(filePath);
        create(filePath);
    }

    /**
     * Deletes a file.
     *
     * @param filePath path of the file to delete
     * @throws IOException if the file does not exist
     */
    public static void delete(String filePath) throws IOException {
        boolean deleted = Files.deleteIfExists(Paths.get(filePath));
        if (!deleted) {
            throw new IOException("File does not exist: " + filePath);
        }
    }

    /* =======================
       Base File
       ======================= */

    /**
     * Common base for all file handles. Holds the target path and charset,
     * and validates them once at construction so subclasses never have to.
     */

    private static abstract class BaseFile {

        protected final String filePath;
        protected final Charset charset;

        protected BaseFile(String filePath, Charset charset) {
            this.filePath = requireValidPath(filePath);
            this.charset = requireValidCharset(charset);
        }

        protected Path getPath() {
            return Paths.get(filePath);
        }

        private static String requireValidPath(String filePath) {
            if (filePath == null || filePath.isBlank())
                throw new IllegalArgumentException("File path must not be null or empty.");
            return filePath;
        }

        private static Charset requireValidCharset(Charset charset) {
            if (charset == null) {
                throw new IllegalArgumentException("Charset must not be null.");
            }
            return charset;
        }
    }

    /* =======================
       Read
       ======================= */

    /**
     * Reads lines from a file, either all at once or one at a time.
     *
     * <p>Lines are loaded from disk on first access and cached in memory
     * for subsequent calls, so repeated reads don't re-hit the filesystem.
     * Call {@link #refresh()} to discard the cache and re-read from disk
     * (e.g. if the file changed externally).
     *
     * <p>Not thread-safe — see class-level note on {@link FileX}.
     */

    public static class Read extends BaseFile {

        private int currentLine = 0;
        private List<String> cachedLines;
        private FileTime lastKnownModifiedTime;

        public Read(String filePath,  Charset charset) {
            super(filePath,  charset);
        }
        public Read(String filePath) { this(filePath, DEFAULT_CHARSET); }       //Creates a reader using the default charset (UTF-8)

        /** Loads lines from disk on first call, returns the cached copy afterward. */
        private List<String> lines() throws IOException {
            FileTime currentModified = Files.getLastModifiedTime(getPath());

            if (cachedLines == null || !currentModified.equals(lastKnownModifiedTime)) {
                    cachedLines =Files.readAllLines(getPath(), charset);
                    lastKnownModifiedTime = currentModified;
            }

            return cachedLines;
        }

        /**
         * Returns every line in the file as an immutable list.
         * Does not affect the sequential read cursor.
         */
        public List<String> readAllLines() throws IOException {
            return List.copyOf(lines());
        }

        /**
         * Resets sequential reading back to the first line.
         */
        public void resetReader() {
            currentLine = 0;
        }

        /**
         * Returns the current line number that will be read.
         *
         * First line = 1.
         */
        public int nextLineNumber() {
            return currentLine + 1;
        }

        /**
         * Returns true if another line exists.
         */
        public boolean hasNextLine() throws IOException {
            return currentLine < lines().size();
        }

        /**
         * Reads a specific line.
         *
         * First line = 1.
         *
         * @param n the line number to read (1-based)
         * @throws IndexOutOfBoundsException if the line does not exist
         */
        public String readLine(int n) throws IOException {
            List<String> lines = lines();
            if (n < 1 || n > lines.size()) throw new IndexOutOfBoundsException("Line " + n + " does not exist.");
            return lines.get(n - 1);
        }

        /**
         * Reads the next line sequentially.
         *
         * EOF Behavior:
         * @throws NoSuchElementException when no lines remain.
         */
        public String readLine() throws IOException {
            List<String> lines = lines();
            if (currentLine >= lines.size()) throw new NoSuchElementException("End of file reached.");
            return lines.get(currentLine++);
        }


        /**
         * Reloads the file from disk and resets the reader.
         *
         * FileX readers cache the contents they first read.
         * If the file changes after this reader was created
         * (for example through Write, Append, or an external program),
         * call refresh() to see the latest contents.
         *
         * @return this reader instance
         */
        public Read refresh() {
            cachedLines = null;
            lastKnownModifiedTime = null;
            currentLine = 0;
            return this;
        }
    }

    /* =======================
       Append
       ======================= */

    /**
     * Adds lines to the end of a file without touching existing content.
     * Creates the file if it doesn't already exist.
     */

    public static class Append extends BaseFile {

        public Append(String filePath,  Charset charset) {
            super(filePath,  charset);
        }
        public Append(String filePath) { this(filePath, DEFAULT_CHARSET); }         //Creates an appender using the default charset (UTF-8).

        /** Appends a single line to the end of the file. */
        public void append(String line) throws IOException {
            append(Collections.singletonList(line));
        }

        /** Appends multiple lines to the end of the file, in order. */
        public void append(List<String> lines) throws IOException {

            Files.write(getPath(), lines, charset,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.WRITE,
                        StandardOpenOption.APPEND
            );
        }
    }

    /* =======================
       Write
       ======================= */

    /**
     * Overwrites a file's entire contents. Creates the file if it doesn't
     * already exist; replaces existing content if it does.
     */

    public static class Write extends BaseFile {

        public Write(String filePath,  Charset charset) {
            super(filePath, charset);
        }
        public Write(String filePath) { this(filePath, DEFAULT_CHARSET); }          //Creates a writer using the default charset (UTF-8).

        /** Overwrites the file with a single line. */
        public void write(String line)throws IOException {
            write(Collections.singletonList(line));
        }

        /** Overwrites the file with multiple lines. */
        public void write(List<String> lines) throws IOException {

            Files.write(getPath(), lines, charset,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.WRITE,
                        StandardOpenOption.TRUNCATE_EXISTING
            );
        }
    }
}



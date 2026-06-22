package com.github.espresso;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * FileX — a beginner-friendly file I/O library for Java.
 *
 * <p>Wraps {@code java.nio.file} behind a small, readable, fluent API so
 * newcomers can read/write/append files without learning streams, buffers,
 * or try-with-resources boilerplate up front — while still using real
 * checked exceptions, so proper error handling is learned, not hidden.
 *
 * <p><b>Quick start:</b>
 * <pre>{@code
 * var reader   = FileX.read("notes.txt");
 * var writer   = FileX.write("notes.txt");
 * var appender = FileX.append("notes.txt");
 *
 * writer.write("Hello, world!");
 * appender.append("Second line.");
 * List<String> lines = reader.readAllLines();
 * }</pre>
 *
 * <p>Default charset is UTF-8. Overloads accepting a {@link Charset} are
 * available for cases that need something else.
 *
 * <p><b>Thread safety:</b> {@code Read} instances hold mutable cursor state
 * ({@code currentLine}) and a line cache. They are not safe to share across
 * threads. Create a separate {@code Read} per thread if needed.
 */
public class FileX {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    // ===========================================================
    //  Factory methods — entry points into the library
    // ===========================================================

    /**
     * Creates a reader for the file at {@code path}, using UTF-8.
     *
     * @throws IllegalArgumentException if {@code path} is null or blank
     */
    public static Read read(String path) {
        return new Read(path, DEFAULT_CHARSET);
    }

    /**
     * Creates a reader for the file at {@code path}, using a custom charset.
     *
     * @throws IllegalArgumentException if {@code path} is null/blank, or {@code charset} is null
     */
    public static Read read(String path, Charset charset) {
        return new Read(path, charset);
    }

    /**
     * Creates a writer (overwrite mode) for the file at {@code path}, using UTF-8.
     *
     * @throws IllegalArgumentException if {@code path} is null or blank
     */
    public static Write write(String path) {
        return new Write(path, DEFAULT_CHARSET);
    }

    /**
     * Creates a writer (overwrite mode) for the file at {@code path}, using a custom charset.
     *
     * @throws IllegalArgumentException if {@code path} is null/blank, or {@code charset} is null
     */
    public static Write write(String path, Charset charset) {
        return new Write(path, charset);
    }

    /**
     * Creates an appender for the file at {@code path}, using UTF-8.
     *
     * @throws IllegalArgumentException if {@code path} is null or blank
     */
    public static Append append(String path) {
        return new Append(path, DEFAULT_CHARSET);
    }

    /**
     * Creates an appender for the file at {@code path}, using a custom charset.
     *
     * @throws IllegalArgumentException if {@code path} is null/blank, or {@code charset} is null
     */
    public static Append append(String path, Charset charset) {
        return new Append(path, charset);
    }

    // ===========================================================
    //  Static utility methods — querying a file
    // ===========================================================

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
     * Returns the size of a file, in bytes.
     *
     * @param filePath path to the file
     * @return the file's size in bytes
     * @throws IOException if the file does not exist or cannot be read
     */
    public static long size(String filePath) throws IOException {
        return Files.size(Paths.get(filePath));
    }

    /**
     * Checks whether a file is empty (zero bytes).
     *
     * @param filePath path to the file
     * @return true if the file exists and has zero bytes
     * @throws IOException if the file does not exist or cannot be read
     */
    public static boolean isEmpty(String filePath) throws IOException {
        return size(filePath) == 0;
    }

    // ===========================================================
    //  Static utility methods — changing a file
    // ===========================================================

    /**
     * Creates a new, empty file.
     *
     * <p>The parent directory must already exist — use
     * {@link #createParentDirectories(String)} first if it might not, or
     * use {@link #createWithDirectories(String)} to do both in one call.
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
     * Convenience method that builds any missing parent directories and
     * then creates the file itself, in one call.
     *
     * <p>Equivalent to calling {@link #createParentDirectories(String)}
     * followed by {@link #create(String)}.
     *
     * @param filePath path of the file to create
     * @throws FileAlreadyExistsException if a file already exists at that path
     * @throws IOException if the directories or file could not be created
     */
    public static void createWithDirectories(String filePath) throws IOException {
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

    // ===========================================================
    //  BaseFile — shared state/behavior for Read, Write, Append
    // ===========================================================

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

    // ===========================================================
    //  Read
    // ===========================================================

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

        /**
         * Creates a reader using the default charset (UTF-8).
         *
         * @throws IllegalArgumentException if {@code filePath} is null or blank
         */
        public Read(String filePath) {
            this(filePath, DEFAULT_CHARSET);
        }

        /**
         * Creates a reader using a custom charset.
         *
         * @throws IllegalArgumentException if {@code filePath} is null/blank, or {@code charset} is null
         */
        public Read(String filePath, Charset charset) {
            super(filePath, charset);
        }

        /** Loads lines from disk on first call, returns the cached copy afterward. */
        private List<String> lines() throws IOException {
            if (cachedLines == null) {
                cachedLines = Files.readAllLines(getPath(), charset);
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
         * Returns how many lines the file currently has.
         * Does not affect the sequential read cursor.
         */
        public int lineCount() throws IOException {
            return lines().size();
        }

        /**
         * Returns true if the file contains no lines.
         * Does not affect the sequential read cursor.
         */
        public boolean isEmpty() throws IOException {
            return lineCount() == 0;
        }

        /**
         * Returns the first line of the file.
         * Does not affect the sequential read cursor.
         *
         * @throws IndexOutOfBoundsException if the file has no lines
         */
        public String firstLine() throws IOException {
            return readLine(1);
        }

        /**
         * Returns the last line of the file, or {@code null} if the file is empty.
         * Does not affect the sequential read cursor.
         */
        public String lastLine() throws IOException {
            List<String> lines = lines();
            return lines.isEmpty() ? null : lines.get(lines.size() - 1);
        }

        /**
         * Returns true if any line in the file contains {@code search}.
         * Does not affect the sequential read cursor.
         *
         * @throws IllegalArgumentException if {@code search} is null
         */
        public boolean contains(String search) throws IOException {
            if (search == null) {
                throw new IllegalArgumentException("Search text must not be null.");
            }
            for (String line : lines()) {
                if (line.contains(search)) {
                    return true;
                }
            }
            return false;
        }

        /** Resets the sequential read cursor back to the first line. */
        public void resetReader() {
            currentLine = 0;
        }

        /**
         * Returns the line number that the next call to {@link #readLine()}
         * will return. First line is 1.
         */
        public int nextLineNumber() {
            return currentLine + 1;
        }

        /** Returns true if {@link #readLine()} has at least one more line to give. */
        public boolean hasNextLine() throws IOException {
            return currentLine < lines().size();
        }

        /**
         * Reads a specific line by number, independent of the sequential cursor.
         * First line is 1.
         *
         * @param n the line number to read (1-based)
         * @throws IndexOutOfBoundsException if {@code n} is out of range
         */
        public String readLine(int n) throws IOException {
            List<String> lines = lines();
            if (n < 1 || n > lines.size()) {
                throw new IndexOutOfBoundsException("Line " + n + " does not exist. Total lines: " + lines.size());
            }
            return lines.get(n - 1);
        }

        /**
         * Reads the next line, advancing the sequential cursor.
         *
         * @throws NoSuchElementException if no lines remain — check
         *         {@link #hasNextLine()} first if you want to avoid this
         */
        public String readLine() throws IOException {
            List<String> lines = lines();
            if (currentLine >= lines.size()) {
                throw new NoSuchElementException("End of file reached at line " + currentLine);
            }
            return lines.get(currentLine++);
        }

        /**
         * Reloads the file from disk and resets the reader.
         *
         * <p>FileX readers cache the lines they first read, so repeated calls
         * don't keep hitting the filesystem. If the file changes after this
         * reader was created — through Write, Append, or an external program —
         * call this method to pick up the latest content.
         *
         * @return this reader, so calls can be chained
         *         (e.g. {@code reader.refresh().readAllLines()})
         */
        public Read refresh() {
            cachedLines = null;
            currentLine = 0;
            return this;
        }
    }

    // ===========================================================
    //  Append
    // ===========================================================

    /**
     * Adds lines to the end of a file without touching existing content.
     * Creates the file if it doesn't already exist.
     */
    public static class Append extends BaseFile {

        /**
         * Creates an appender using the default charset (UTF-8).
         *
         * @throws IllegalArgumentException if {@code filePath} is null or blank
         */
        public Append(String filePath) {
            this(filePath, DEFAULT_CHARSET);
        }

        /**
         * Creates an appender using a custom charset.
         *
         * @throws IllegalArgumentException if {@code filePath} is null/blank, or {@code charset} is null
         */
        public Append(String filePath, Charset charset) {
            super(filePath, charset);
        }

        /**
         * Appends a single line to the end of the file.
         *
         * @throws NullPointerException if {@code line} is null
         */
        public void append(String line) throws IOException {
            append(List.of(line));
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

    // ===========================================================
    //  Write
    // ===========================================================

    /**
     * Overwrites a file's entire contents. Creates the file if it doesn't
     * already exist; replaces existing content if it does.
     */
    public static class Write extends BaseFile {

        /**
         * Creates a writer using the default charset (UTF-8).
         *
         * @throws IllegalArgumentException if {@code filePath} is null or blank
         */
        public Write(String filePath) {
            this(filePath, DEFAULT_CHARSET);
        }

        /**
         * Creates a writer using a custom charset.
         *
         * @throws IllegalArgumentException if {@code filePath} is null/blank, or {@code charset} is null
         */
        public Write(String filePath, Charset charset) {
            super(filePath, charset);
        }

        /**
         * Overwrites the file with a single line.
         *
         * @throws NullPointerException if {@code line} is null
         */
        public void write(String line) throws IOException {
            write(List.of(line));
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
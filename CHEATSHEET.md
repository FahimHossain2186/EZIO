# 📋 FileX — Cheat Sheet

One-page lookup. For explanations and walkthroughs, see
[GUIDE.md](GUIDE.md). For error meanings, see
[TROUBLESHOOTING.md](TROUBLESHOOTING.md).

---

## Creating a Handle

| Goal | Code |
|---|---|
| Read, default charset | `FileX.read("path")` |
| Read, custom charset | `FileX.read("path", charset)` |
| Write, default charset | `FileX.write("path")` |
| Write, custom charset | `FileX.write("path", charset)` |
| Append, default charset | `FileX.append("path")` |
| Append, custom charset | `FileX.append("path", charset)` |

Constructor style works identically for all six rows above —
swap `FileX.read("path")` for `new FileX.Read("path")`, and so on.

---

## Read

| Method | Returns | Notes |
|---|---|---|
| `readLine()` | `String` | Reads the next line, advances cursor. Throws `NoSuchElementException` at EOF. |
| `readLine(int n)` | `String` | Reads line `n` directly (1-based). Doesn't move the cursor. Throws `IndexOutOfBoundsException` if out of range. |
| `readAllLines()` | `List<String>` | Every line, immutable list. Doesn't move the cursor. |
| `hasNextLine()` | `boolean` | True if `readLine()` has something left to give. |
| `nextLineNumber()` | `int` | The line number the next `readLine()` call will return. |
| `resetReader()` | `void` | Moves the cursor back to line 1. |
| `refresh()` | `Read` | Clears the cached lines, forces next read from disk. Returns `this`, so it's chainable (e.g. `reader.refresh().readAllLines()`). |
| `lineCount()` | `int` | How many lines the file currently has. Doesn't move the cursor. |
| `isEmpty()` | `boolean` | True if the file has no lines. Doesn't move the cursor. |
| `firstLine()` | `String` | The first line. Throws `IndexOutOfBoundsException` if the file is empty. |
| `lastLine()` | `String` | The last line, or `null` if the file is empty. |
| `contains(String search)` | `boolean` | True if any line contains `search`. Throws `IllegalArgumentException` if `search` is null. |

---

## Write

| Method | Notes |
|---|---|
| `write(String line)` | Replaces the entire file with this one line. |
| `write(List<String> lines)` | Replaces the entire file with these lines, in order. |

> Every `write()` call wipes out whatever was in the file before.

---

## Append

| Method | Notes |
|---|---|
| `append(String line)` | Adds one line to the end. Creates the file if missing. |
| `append(List<String> lines)` | Adds these lines to the end, in order. Creates the file if missing. |

> `append()` never erases existing content.

---

## Static Utilities (no handle needed)

| Method | Returns | Notes |
|---|---|---|
| `FileX.exists(path)` | `boolean` | Checks if a file is there. |
| `FileX.size(path)` | `long` | File size in bytes. Throws `IOException` if the file doesn't exist. |
| `FileX.isEmpty(path)` | `boolean` | True if the file exists and has zero bytes. Throws `IOException` if the file doesn't exist. |
| `FileX.create(path)` | `void` | Makes an empty file. Parent folder must already exist. Throws if file already exists. |
| `FileX.createParentDirectories(path)` | `void` | Builds any missing folders in the path. |
| `FileX.delete(path)` | `void` | Deletes the file. Throws if it wasn't there. |

---

## Exceptions Quick Reference

| Exception | Thrown by | Means |
|---|---|---|
| `IllegalArgumentException` | any handle creation | Path was null/blank, or charset was null. |
| `IOException` | most methods | Disk-level problem — see [TROUBLESHOOTING.md](TROUBLESHOOTING.md). |
| `NoSuchElementException` | `readLine()` | You tried to read past the last line. |
| `IndexOutOfBoundsException` | `readLine(n)` | The line number doesn't exist in the file. |

---

## Default Charset

UTF-8, unless you pass a `Charset` explicitly.

```java
import java.nio.charset.StandardCharsets;

var reader = FileX.read("notes.txt", StandardCharsets.UTF_16);
```
# 📂 FileX

> **File handling in Java shouldn't require a PhD.**

FileX is an ultra-simple, human-readable file handling library for Java designed specifically for beginners.

Instead of forcing new Java developers to learn `BufferedReader`, `Scanner`, `Path`, `Files`, loops, and complicated exception structures just to read a text file, FileX wraps those concepts into commands that read like plain English.

```java
var writer = FileX.write("notes.txt");
writer.write("Hello World");

var reader = FileX.read("notes.txt");
System.out.println(reader.readLine());
```

Simple.

Readable.

Beginner-friendly.

---

## Table of Contents

* [Why FileX Exists](#why-filex-exists)
* [Who Is FileX For?](#who-is-filex-for)
* [Our Philosophy](#our-philosophy)
* [Requirements](#requirements)
* [Two Ways to Create a File Handle](#two-ways-to-create-a-file-handle)
* [Features](#features)
  * [Reading](#reading)
  * [Writing](#writing)
  * [Appending](#appending)
  * [Custom Charsets](#custom-charsets)
  * [Utilities](#utilities)
* [Installation](#installation)
  * [Method 1: Download the JAR Manually (Beginner Friendly)](#method-1-download-the-jar-manually-beginner-friendly)
  * [Method 2: Terminal Installation](#method-2-terminal-installation)
  * [Method 3: JitPack — Maven / Gradle (Advanced)](#method-3-jitpack--maven--gradle-advanced)
* [Quick Start](#quick-start)
* [Design Principles](#design-principles)
* [Limitations](#limitations)
* [Contributing](#contributing)
* [License](#license)

---

## Why FileX Exists

Java's built-in file APIs are powerful.

They're also intimidating.

To perform something as simple as reading a file, beginners often encounter:

* `BufferedReader`
* `FileReader`
* `Scanner`
* loops
* `Path`
* `Files`
* nested exceptions
* multiple imports

—all at the same time.

FileX removes that overwhelming boilerplate so beginners can focus on understanding **what their code is doing** before worrying about how Java does it internally.

---

## Who Is FileX For?

FileX was built:

* ✅ by a beginner,
* ✅ for beginners,
* ✅ with beginners in mind.

It is ideal for:

* Students learning Java
* People transitioning from Python to Java
* First-year CSE students
* Self-taught programmers
* Junior developers who want cleaner file operations
* Educators teaching Java fundamentals

If you're already comfortable with Java NIO, Apache Commons, or Guava, FileX probably isn't for you.

And that's okay.

---

## Our Philosophy

FileX does **not** try to compete with:

* Apache Commons IO
* Google Guava
* Enterprise frameworks

We deliberately avoid:

* Massive utility classes
* Complex factory patterns
* Advanced optimizations
* Clever abstractions

Instead, FileX values:

* Simplicity
* Readability
* Safety
* Discoverability
* Beginner confidence

The API should read like a story.

```java
var writer = FileX.write("essay.txt");
writer.write("Introduction");

var appender = FileX.append("essay.txt");
appender.append("Conclusion");

var reader = FileX.read("essay.txt");
reader.readLine();
```

Even before typing it, you can imagine what the code does.

---

## Requirements

* **Java 11 or higher.**

FileX uses `var` (local variable type inference, Java 10+) and `String.isBlank()`
(Java 11+) internally. Make sure your project's JDK is set to 11 or above,
or you'll hit compile errors that have nothing to do with FileX itself.

---

## Two Ways to Create a File Handle

`Read`, `Write`, and `Append` can each be created two ways — pick whichever
feels more natural to you. Both produce the exact same object, run through
the exact same path/charset validation, and behave identically afterward.

**Constructor** — the classic `new ClassName(...)` pattern you already know
from `Scanner`, `ArrayList`, and `Random`:

```java
var reader = new FileX.Read("notes.txt");
var reader = new FileX.Read("notes.txt", StandardCharsets.UTF_8);
```
```java
FileX.Read reader = new FileX.Read("notes.txt");
FileX.Read reader = new FileX.Read("notes.txt", StandardCharsets.UTF_8);
```

**Factory method** — reads like a sentence, matches the rest of the FileX API:

```java
var reader = FileX.read("notes.txt");
var reader = FileX.read("notes.txt", StandardCharsets.UTF_8);
```

The same applies to `FileX.write(...)` / `new FileX.Write(...)` and
`FileX.append(...)` / `new FileX.Append(...)`. Use whichever you find
easier to read — neither is "more correct."

---

# Features

## Reading

```java
var reader = FileX.read("notes.txt");
```

### Read the next line

```java
reader.readLine();
```

### Read a specific line

```java
reader.readLine(3);
```

### Read all lines

```java
reader.readAllLines();
```

### Check if another line exists

```java
reader.hasNextLine();
```

### Reset sequential reading

```java
reader.resetReader();
```

### Get the next line number

```java
reader.nextLineNumber();
```

### Force a fresh read from disk

```java
reader.refresh();
```

> FileX caches lines in memory after the first read, so repeated calls
> don't keep hitting the filesystem. If the file changes on disk after
> you've started reading, call `refresh()` to pick up the new content.

### Count the lines

```java
reader.lineCount();
```

### Check if the file has any lines

```java
reader.isEmpty();
```

### Get the first line

```java
reader.firstLine();
```

### Get the last line

```java
reader.lastLine();
```

> Returns `null` if the file has no lines.

### Check if a line contains some text

```java
reader.contains("Math");
```

---

## Writing

```java
var writer = FileX.write("notes.txt");
```

### Overwrite with one line

```java
writer.write("Hello");
```

### Overwrite with multiple lines

```java
writer.write(List.of(
    "Math",
    "Physics",
    "Chemistry"
));
```

---

## Appending

```java
var appender = FileX.append("notes.txt");
```

### Append one line

```java
appender.append("Biology");
```

### Append multiple lines

```java
appender.append(List.of(
    "English",
    "History"
));
```

---

## Custom Charsets

Every entry point defaults to UTF-8, but accepts an optional `Charset` if
you need something else.

```java
var reader = FileX.read("notes.txt", StandardCharsets.ISO_8859_1);
var writer = FileX.write("notes.txt", StandardCharsets.UTF_16);
var appender = FileX.append("notes.txt", StandardCharsets.US_ASCII);
```

> Constructor style takes a charset the same way —
> see [Two Ways to Create a File Handle](#two-ways-to-create-a-file-handle).

---

## Utilities

### Check if a file exists

```java
FileX.exists("notes.txt");
```

### Check a file's size, in bytes

```java
FileX.size("notes.txt");
```

### Check if a file is empty

```java
FileX.isEmpty("notes.txt");
```

### Create a file

```java
FileX.create("notes.txt");
```

### Create parent directories

```java
FileX.createParentDirectories(
    "docs/projects/notes.txt"
);
```

### Delete a file

```java
FileX.delete("notes.txt");
```

---

# Installation

## Method 1: Download the JAR Manually (Beginner Friendly)

1. Go to GitHub Releases.

2. Download the latest:

```
FileX-x.x.x.jar
```

3. Add it to your Java project.

---

### IntelliJ IDEA

* File
* Project Structure
* Libraries
* +
* Java
* Select `FileX-x.x.x.jar`
* Apply

Done.

---

### Eclipse

* Right-click project
* Build Path
* Configure Build Path
* Libraries
* Add External JARs
* Select FileX JAR
* Apply and Close

Done.

---

### VS Code

Place the JAR inside:

```
project/lib/
```

Then add it to your Java dependencies.

Done.

---

## Method 2: Terminal Installation

If you prefer the terminal, follow the instructions below.

Replace:

```
VERSION
```

with the release version.

Example:

```
v1.0.0
```

---

### Windows (PowerShell)

Download:

```powershell
curl -L -o FileX.jar https://github.com/FahimHossain2186/FileX/releases/download/VERSION/FileX.jar
```

Compile:

```powershell
javac -cp FileX.jar Main.java
```

Run:

```powershell
java -cp ".;FileX.jar" Main
```

---

### macOS / Linux

Download:

```bash
curl -L -o FileX.jar https://github.com/FahimHossain2186/FileX/releases/download/VERSION/FileX.jar
```

Compile:

```bash
javac -cp FileX.jar Main.java
```

Run:

```bash
java -cp ".:FileX.jar" Main
```

---

## Method 3: JitPack — Maven / Gradle (Advanced)

If you're using a build tool, FileX is published via
[JitPack](https://jitpack.io), so you can add it as a normal dependency
without downloading anything manually.

### Maven

Add the JitPack repository:

```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>
```

Add the dependency:

```xml
<dependency>
  <groupId>com.github.FahimHossain2186</groupId>
  <artifactId>FileX</artifactId>
  <version>VERSION</version>
</dependency>
```

### Gradle (Groovy DSL)

```groovy
repositories {
  maven { url 'https://jitpack.io' }
}

dependencies {
  implementation 'com.github.FahimHossain2186:FileX:VERSION'
}
```

### Gradle (Kotlin DSL)

```kotlin
repositories {
  maven { url = uri("https://jitpack.io") }
}

dependencies {
  implementation("com.github.FahimHossain2186:FileX:VERSION")
}
```

Replace `VERSION` with the latest release tag, e.g. `v1.0.0`.

---

# Quick Start

```java
import com.github.espresso.FileX;

import java.util.List;

public class Main {

  public static void main(String[] args)
          throws Exception {

    FileX.create("notes.txt");

    var writer = FileX.write("notes.txt");

    writer.write(List.of(
            "Math",
            "Physics",
            "Chemistry"
    ));

    var appender = FileX.append("notes.txt");

    appender.append("Biology");

    var reader = FileX.read("notes.txt");

    while (reader.hasNextLine()) {
      System.out.println(reader.readLine());
    }
  }
}
```

Output:

```
Math
Physics
Chemistry
Biology
```

---

# Design Principles

FileX follows the Principle of Least Privilege.

A reader should read.

A writer should write.

An appender should append.

Instead of:

```java
file.doEverything();
```

FileX encourages:

```java
var reader = FileX.read(...);
var writer = FileX.write(...);
var appender = FileX.append(...);
```

Each object only has the responsibilities it needs.

This teaches good API design practices from the very beginning.

---

# Limitations

FileX intentionally avoids advanced functionality.

It is not designed for:

* Gigabyte-sized files
* High-performance streaming
* Concurrent file access (`Read` instances are not thread-safe)
* Enterprise applications

If you eventually outgrow FileX, you'll already understand the concepts needed to transition into Java's native file APIs.

And that was always the goal.

---

# Contributing

Suggestions, bug reports, and beginner feedback are always welcome.

If FileX helped make Java feel a little less scary, consider starring the repository ⭐.

Every beginner deserves tools built with empathy.

---

# License

Released under the MIT License.
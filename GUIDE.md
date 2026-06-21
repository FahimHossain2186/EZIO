# 🧭 FileX — Getting Started Guide

This guide walks through FileX step by step, using small real scenarios
instead of isolated method calls. If you've never written file-handling
code in Java before, start here. If you just need a quick lookup, see
[CHEATSHEET.md](CHEATSHEET.md) instead.

---

## 1. Your First File

Let's create a file and put one line in it.

```java
import com.github.espresso.FileX;

public class Main {
    public static void main(String[] args) throws Exception {
        var writer = FileX.write("diary.txt");
        writer.write("Today I learned file handling.");
    }
}
```

Run it once, and a file called `diary.txt` appears with that sentence
inside. Run it again, and the sentence is still there — just the same
one, not duplicated. That's because `Write` **replaces** the file's
contents every time, rather than adding to them.

> 👉 If you wanted the second run to add a *new* line underneath instead
> of replacing the first, you'd use `FileX.append(...)` — see Section 3.

---

## 2. Reading It Back

```java
var reader = FileX.read("diary.txt");
System.out.println(reader.readLine());
```

`readLine()` reads one line and remembers where it left off, so calling
it again would read the *next* line. If you just want everything at
once:

```java
var reader = FileX.read("diary.txt");
for (String line : reader.readAllLines()) {
    System.out.println(line);
}
```

---

## 3. Adding to a File Without Erasing It

This is the most common beginner mix-up: **Write replaces, Append adds.**

```java
var writer = FileX.write("diary.txt");
writer.write("Day 1: Started learning Java.");

var appender = FileX.append("diary.txt");
appender.append("Day 2: Learned about file handling.");
appender.append("Day 3: Built my first project.");
```

After this, `diary.txt` contains all three lines, in order. If you'd used
`write()` instead of `append()` for days 2 and 3, you'd only have day 3
left — each `write()` call wipes out everything before it.

---

## 4. Reading Line by Line, Safely

If you don't know how many lines a file has, don't guess — ask:

```java
var reader = FileX.read("diary.txt");

while (reader.hasNextLine()) {
    System.out.println(reader.readLine());
}
```

`hasNextLine()` tells you whether there's anything left before you try
to read it. Skipping this check and calling `readLine()` past the last
line throws a `NoSuchElementException` — see
[TROUBLESHOOTING.md](TROUBLESHOOTING.md) if you hit that.

---

## 5. Jumping to a Specific Line

Sometimes you don't want to read in order — you want, say, "line 3."

```java
var reader = FileX.read("diary.txt");
String thirdLine = reader.readLine(3); // 1-based: first line is 1, not 0
```

This doesn't disturb the sequential cursor used by plain `readLine()` —
they're independent ways of reading the same file.

---

## 6. A File That Might Not Exist Yet

`FileX.exists(...)` lets you check before you act:

```java
String path = "scores.txt";

if (!FileX.exists(path)) {
    FileX.create(path);
}

var writer = FileX.write(path);
writer.write("Level 1: 9000 points");
```

Note: `Append` already creates the file automatically if it's missing,
so you only need this `exists()`/`create()` check when using `Write` on
a file you're not sure exists yet, or when you specifically want to
detect "is this the first run."

---

## 7. Files Inside Folders

If your path includes folders that don't exist yet — say,
`logs/2026/june.txt` — Java won't create them for you automatically, and
`FileX.create(...)` will fail if `logs/2026/` doesn't exist. Build the
folders first:

```java
String path = "logs/2026/june.txt";
FileX.createParentDirectories(path);
FileX.create(path);
```

---

## 8. Writing or Reading Several Lines at Once

Both `Write` and `Append` accept a `List<String>` instead of one string
at a time:

```java
var writer = FileX.write("subjects.txt");
writer.write(List.of("Math", "Physics", "Chemistry"));
```

This produces the exact same result as calling `write(...)` three times
in a row, just in one call.

---

## 9. Two Valid Ways to Build a Handle

Everything above used the factory style (`FileX.read(...)`). FileX also
supports the classic constructor style, if that's more familiar from
`Scanner` or `ArrayList`:

```java
var reader = FileX.read("diary.txt");          // factory
var reader = new FileX.Read("diary.txt");       // constructor
```

Both produce an identical object. Pick whichever reads clearer to you —
see the README's
[Two Ways to Create a File Handle](README.md#two-ways-to-create-a-file-handle)
section for the full breakdown, including the charset-aware overloads.

---

## 10. What's Next

* Need a quick method lookup instead of a walkthrough? → [CHEATSHEET.md](CHEATSHEET.md)
* Got an exception you don't understand? → [TROUBLESHOOTING.md](TROUBLESHOOTING.md)
* Want the full feature list and installation instructions? → [README.md](README.md)

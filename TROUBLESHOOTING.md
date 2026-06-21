# 🛠️ FileX — Troubleshooting

If something threw an exception or didn't compile, find it below. Each
entry shows what you probably did, why FileX reacted that way, and how
to fix it.

---

## `IllegalArgumentException: File path must not be null or empty.`

**You probably wrote:**
```java
var reader = FileX.read(null);
// or
var reader = FileX.read("");
// or
var reader = FileX.read("   ");
```

**Why:** every `Read`/`Write`/`Append` checks its path before doing
anything else. A null, empty, or whitespace-only path can't point to a
real file, so FileX stops you immediately instead of letting a confusing
error happen later.

**Fix:** pass an actual path.
```java
var reader = FileX.read("notes.txt");
```

---

## `IllegalArgumentException: Charset must not be null.`

**You probably wrote:**
```java
var writer = FileX.write("notes.txt", null);
```

**Fix:** either drop the second argument (defaults to UTF-8) or pass a
real charset.
```java
var writer = FileX.write("notes.txt");
// or
var writer = FileX.write("notes.txt", StandardCharsets.UTF_8);
```

---

## `NoSuchElementException: End of file reached.`

**You probably wrote:**
```java
var reader = FileX.read("notes.txt");
while (true) {
    System.out.println(reader.readLine()); // no check before reading
}
```

**Why:** `readLine()` only knows how to give you the *next* line — it
doesn't know when to stop on its own.

**Fix:** check `hasNextLine()` before each read.
```java
while (reader.hasNextLine()) {
    System.out.println(reader.readLine());
}
```

---

## `IndexOutOfBoundsException: Line N does not exist.`

**You probably wrote:**
```java
var reader = FileX.read("notes.txt");
String line = reader.readLine(10); // file only has 3 lines
```

**Why:** `readLine(n)` is 1-based and bounded by however many lines the
file actually has.

**Fix:** check the line count first if you're not sure.
```java
var lines = reader.readAllLines();
if (n <= lines.size()) {
    String line = reader.readLine(n);
}
```

---

## `java.nio.file.NoSuchFileException`

**You probably wrote:**
```java
var reader = FileX.read("logs/today.txt"); // "logs/" folder doesn't exist
reader.readAllLines();
```

**Why:** this is a plain Java I/O error (not a FileX-specific one) — the
file genuinely isn't there yet, often because the parent folder is
missing too.

**Fix:** create the folders and file first, or check before reading.
```java
FileX.createParentDirectories("logs/today.txt");
FileX.create("logs/today.txt");
```

---

## `java.nio.file.FileAlreadyExistsException`

**You probably wrote:**
```java
FileX.create("notes.txt"); // already exists from a previous run
```

**Why:** `FileX.create(...)` is for making a *new* empty file. It refuses
to overwrite something that's already there, on purpose — that's what
`FileX.write(...)` is for.

**Fix:** check first, or just use `write()` if overwriting is fine.
```java
if (!FileX.exists("notes.txt")) {
    FileX.create("notes.txt");
}
```

---

## My file got wiped out and I don't know why

**You probably wrote:**
```java
var writer = FileX.write("diary.txt");
writer.write("Day 1");
// ...later, elsewhere in the code...
var writer2 = FileX.write("diary.txt");
writer2.write("Day 2"); // "Day 1" is now gone
```

**Why:** `Write` always replaces the entire file's contents. It's not
aware of anything you wrote earlier — every call starts from a blank
file.

**Fix:** use `FileX.append(...)` when you want to add on top of existing
content instead of replacing it.
```java
var appender = FileX.append("diary.txt");
appender.append("Day 2");
```

---

## My code won't compile: "cannot find symbol: method Read(String)"

**You probably wrote:**
```java
FileX.Read reader = FileX.Read("notes.txt"); // capital R, no `new`
```

**Why:** the **method** is `read` (lowercase) and the **class** is
`Read` (capitalized). FileX doesn't have a method literally named
`Read` — that capitalized name only exists as the class/constructor.

**Fix:** use one of these two forms instead.
```java
var reader = FileX.read("notes.txt");        // lowercase factory method
var reader = new FileX.Read("notes.txt");    // capitalized constructor
```

---

## I changed the file on disk but FileX still shows the old content

**You probably wrote:**
```java
var reader = FileX.read("notes.txt");
reader.readAllLines(); // reads and caches the file

// ...something else edits notes.txt on disk here...

reader.readAllLines(); // still shows the OLD content
```

**Why:** `Read` caches the file's lines in memory the first time you
read, so repeated calls don't keep hitting the disk. If the file changes
underneath it, the cache doesn't know.

**Fix:** call `refresh()` to force a fresh read.
```java
reader.refresh();
reader.readAllLines(); // now shows the updated content
```

---

## Still stuck?

Open an issue on the GitHub repository with:
1. The exact code that triggered it
2. The full exception message and stack trace
3. What you expected to happen instead

See [README.md](README.md) for installation help, or
[GUIDE.md](GUIDE.md) for a full walkthrough of common usage patterns.

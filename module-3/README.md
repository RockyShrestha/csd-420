# Module 3.2 Programming Assignment

**Author:** Rakesh Shrestha
**Course:** CSD420-T301 Advanced Java Programming
**Instructor:** Professor Tedi Pano
**Date:** September 27, 2026

## Description

`RemoveDuplicatesTest.java` fills an `ArrayList<Integer>` with 50 random values from 1 to 20 and calls the generic method:

```java
public static <E> ArrayList<E> removeDuplicates(ArrayList<E> list)
```

The method returns a new `ArrayList` holding each value once, in first-seen order. The original list is not changed.

The program runs three tests:

1. **Integer test:** 50 random values from 1 to 20, duplicates removed, and the result checked (original unchanged, no repeats, no values lost or added, all values in range).
2. **String test:** a short list of words, to show the method works with any type.
3. **Error handling test:** a null list, a reversed range, and a negative size are passed on purpose, and each one is caught and reported.

## Run

```
javac RemoveDuplicatesTest.java
java RemoveDuplicatesTest
```

## Error Handling

- A `null` list throws an `IllegalArgumentException` with a clear message.
- `createRandomList` rejects a negative size, a minimum larger than the maximum, and a missing `Random` object.
- Every caught exception is printed to the console and written to `removeDuplicates.log`.
- The log file is recreated on each run, so it matches the latest console output.
- If a verification check fails, an `IllegalStateException` is thrown, shown on the console, and logged with its stack trace.

## Files

| File | Purpose |
|------|---------|
| `RemoveDuplicatesTest.java` | Source code |
| `removeDuplicates.log` | Log file from the latest run |
| `Shrestha_Module3.2_Screenshots.docx` | Java/JavaFX setup, program output, log, and GitHub screenshots |
| `README.md` | This file |

/*
 * Rakesh Shrestha
 * CSD420-T301 Advanced Java Programming
 * Module 5.2 Programming Assignment
 * October 11, 2026
 *
 * Test program for WordSorter.
 *
 * Each test checks one part of WordSorter and prints PASS or FAIL to the
 * console. The tests also check that bad input, such as a missing file, an
 * empty file, or a null value, is handled correctly. The number of passed
 * and failed tests is printed at the end.
 *
 * Attribution:
 *   Liang, Y. D. (2024). Introduction to Java Programming and Data Structures
 *     (13th ed.). Pearson. Chapter 21, Sets and Maps.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

public class WordSorterTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {

        System.out.println("Running WordSorter tests...");
        System.out.println();

        testDuplicatesAreRemoved();
        testUpperAndLowerCase();
        testPunctuationIsIgnored();
        testAscendingOrder();
        testDescendingOrder();
        testWordFileIsRead();
        testMissingFile();
        testEmptyFile();
        testNullFile();
        testNullLine();

        System.out.println();
        System.out.println("Tests passed: " + passed);
        System.out.println("Tests failed: " + failed);
    }

    private static void testDuplicatesAreRemoved() {

        TreeSet<String> words = new TreeSet<>();
        WordSorter.addWords("apple banana apple banana cherry apple", words);

        check("Duplicate words are stored only once",
                Arrays.asList("apple", "banana", "cherry"), words);
    }

    private static void testUpperAndLowerCase() {

        TreeSet<String> words = new TreeSet<>();
        WordSorter.addWords("Java JAVA java", words);

        check("Upper and lower case count as the same word",
                Arrays.asList("java"), words);
    }

    private static void testPunctuationIsIgnored() {

        TreeSet<String> words = new TreeSet<>();
        WordSorter.addWords("Hello, world! Don't stop, 2026.", words);

        check("Punctuation and numbers are ignored",
                Arrays.asList("don't", "hello", "stop", "world"), words);
    }

    private static void testAscendingOrder() {

        TreeSet<String> words = new TreeSet<>();
        WordSorter.addWords("delta alpha charlie bravo", words);

        check("Words are in ascending order",
                Arrays.asList("alpha", "bravo", "charlie", "delta"), words);
    }

    private static void testDescendingOrder() {

        TreeSet<String> words = new TreeSet<>();
        WordSorter.addWords("delta alpha charlie bravo", words);

        check("Words are in descending order",
                Arrays.asList("delta", "charlie", "bravo", "alpha"), words.descendingSet());
    }

    private static void testWordFileIsRead() {

        String testName = "collection_of_words.txt is read and has no duplicates";

        try {
            TreeSet<String> words = WordSorter.readWords(new File(WordSorter.WORD_FILE));

            // "java" appears twice in the file but should be stored once.
            if (!words.isEmpty() && words.contains("java")) {
                pass(testName + " (" + words.size() + " unique words)");
            }
            else {
                fail(testName, "The expected words were not found.");
            }
        }
        catch (FileNotFoundException e) {
            fail(testName, "The file was not found.");
        }
    }

    private static void testMissingFile() {

        String testName = "A missing file throws FileNotFoundException";

        try {
            WordSorter.readWords(new File("missing_file.txt"));
            fail(testName, "No exception was thrown.");
        }
        catch (FileNotFoundException e) {
            pass(testName);
        }
    }

    private static void testEmptyFile() {

        String testName = "An empty file gives an empty set";
        File emptyFile = null;

        try {
            emptyFile = File.createTempFile("empty_words", ".txt");
            TreeSet<String> words = WordSorter.readWords(emptyFile);

            if (words.isEmpty()) {
                pass(testName);
            }
            else {
                fail(testName, "Expected no words but got " + words);
            }
        }
        catch (IOException e) {
            fail(testName, "The temporary file could not be created. " + e.getMessage());
        }
        finally {
            if (emptyFile != null && !emptyFile.delete()) {
                System.out.println("Warning: The temporary file could not be deleted.");
            }
        }
    }

    private static void testNullFile() {

        String testName = "A null file throws IllegalArgumentException";

        try {
            WordSorter.readWords(null);
            fail(testName, "No exception was thrown.");
        }
        catch (IllegalArgumentException e) {
            pass(testName);
        }
        catch (FileNotFoundException e) {
            fail(testName, "The wrong exception was thrown.");
        }
    }

    private static void testNullLine() {

        String testName = "A null line throws IllegalArgumentException";

        try {
            WordSorter.addWords(null, new TreeSet<>());
            fail(testName, "No exception was thrown.");
        }
        catch (IllegalArgumentException e) {
            pass(testName);
        }
    }

    // Compares the expected words with the actual words, in order.
    private static void check(String testName, List<String> expected, Iterable<String> actual) {

        List<String> actualList = new ArrayList<>();

        for (String word : actual) {
            actualList.add(word);
        }

        if (expected.equals(actualList)) {
            pass(testName);
        }
        else {
            fail(testName, "Expected " + expected + " but got " + actualList);
        }
    }

    private static void pass(String testName) {

        passed++;
        System.out.println("PASS: " + testName);
    }

    private static void fail(String testName, String reason) {

        failed++;
        System.out.println("FAIL: " + testName + " - " + reason);
    }
}

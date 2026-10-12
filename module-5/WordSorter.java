/*
 * Rakesh Shrestha
 * CSD420-T301 Advanced Java Programming
 * Module 5.2 Programming Assignment
 * October 11, 2026
 *
 * This program reads words from the text file collection_of_words.txt and
 * displays all non-duplicate words in ascending order and then in
 * descending order.
 *
 * How it works:
 *   - A Set contains no duplicate elements, so each word is stored once.
 *   - A TreeSet keeps its elements sorted, so the words are already in
 *     ascending order when they are printed.
 *   - descendingSet() returns the same words in descending order.
 *   - Words are changed to lower case so "Java" and "java" are the same word.
 *
 * Error handling:
 *   - Every caught exception is written to the console and to a Logger.
 *   - A missing file, an empty file, a file that cannot be read, and null
 *     values passed to the methods are all handled with clear messages.
 *
 * Attribution:
 *   Liang, Y. D. (2024). Introduction to Java Programming and Data Structures
 *     (13th ed.). Pearson. Chapter 12, Exception Handling and Text I/O;
 *     Chapter 21, Sets and Maps; and Chapter 23, Sorting.
 *   Payne, D. Set examples Example_01 through Example_05, Bellevue University.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WordSorter {

    // The word file is referenced here, so no command-line argument is needed.
    public static final String WORD_FILE = "collection_of_words.txt";

    // Number of words printed on each line of output.
    private static final int WORDS_PER_LINE = 10;

    // Logger used to record any exception the program catches.
    private static final Logger LOGGER = Logger.getLogger(WordSorter.class.getName());

    public static void main(String[] args) {

        try {
            TreeSet<String> words = readWords(new File(WORD_FILE));

            if (words.isEmpty()) {
                System.out.println("The file " + WORD_FILE + " does not contain any words.");
                return;
            }

            System.out.println("Number of non-duplicate words: " + words.size());
            System.out.println();

            displayWords("Non-duplicate words in ascending order:", words);
            displayWords("Non-duplicate words in descending order:", words.descendingSet());
        }
        catch (FileNotFoundException e) {
            reportError("The file " + WORD_FILE + " was not found. "
                    + "Place the file in the project folder and run the program again.", e);
        }
        catch (IllegalArgumentException | IllegalStateException e) {
            reportError("The word file could not be processed. " + e.getMessage(), e);
        }
        catch (Exception e) {
            reportError("An unexpected error occurred while reading the words.", e);
        }
    }

    /**
     * Reads the file one line at a time and adds every word to a TreeSet.
     *
     * @param file the text file to read
     * @return the unique words in ascending order
     * @throws FileNotFoundException    if the file does not exist
     * @throws IllegalArgumentException if the file is null
     */
    public static TreeSet<String> readWords(File file) throws FileNotFoundException {

        if (file == null) {
            throw new IllegalArgumentException("The file cannot be null.");
        }

        TreeSet<String> words = new TreeSet<>();

        try (Scanner input = new Scanner(file)) {
            while (input.hasNextLine()) {
                addWords(input.nextLine(), words);
            }

            // Scanner hides read errors, so check for one after reading.
            if (input.ioException() != null) {
                throw new IllegalStateException("The file could not be read completely.",
                        input.ioException());
            }
        }

        return words;
    }

    /**
     * Splits a line into words and adds them to the set. Anything that is not
     * a letter or an apostrophe separates two words, so punctuation and
     * numbers are skipped and words like "don't" stay whole. The set ignores
     * any word it already has.
     *
     * @param line  one line of text
     * @param words the set that stores the words
     * @throws IllegalArgumentException if the line or the set is null
     */
    public static void addWords(String line, Set<String> words) {

        if (line == null || words == null) {
            throw new IllegalArgumentException("The line and the set of words cannot be null.");
        }

        String[] tokens = line.toLowerCase().split("[^a-z']+");

        for (String word : tokens) {
            if (!word.isEmpty()) {
                words.add(word);
            }
        }
    }

    /**
     * Prints a heading and then the words in the order the set gives them.
     * This one method is used for both the ascending and descending lists.
     *
     * @param heading text printed above the words
     * @param words   the words to print
     * @throws IllegalArgumentException if the set of words is null
     */
    public static void displayWords(String heading, Set<String> words) {

        if (words == null) {
            throw new IllegalArgumentException("The set of words to display cannot be null.");
        }

        int count = 0;

        System.out.println(heading);

        for (String word : words) {
            System.out.print(word + " ");
            count++;

            if (count % WORDS_PER_LINE == 0) {
                System.out.println();
            }
        }

        System.out.println();
        System.out.println();
    }

    /**
     * Writes an error message to the console and records the exception in
     * the log. Using one method keeps every error reported the same way.
     *
     * @param message a clear description of what went wrong
     * @param e       the exception that was caught
     */
    private static void reportError(String message, Exception e) {

        System.out.println("Error: " + message);
        LOGGER.log(Level.SEVERE, message, e);
    }
}

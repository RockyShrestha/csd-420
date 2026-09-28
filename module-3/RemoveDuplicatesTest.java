/*
 * Rakesh Shrestha
 * CSD420-T301 Advanced Java Programming
 * Module 3.2 Programming Assignment
 * Professor Tedi Pano
 * September 27, 2026
 *
 * Purpose:
 *   Test program for a generic static method that removes duplicate values
 *   from an ArrayList. The original list is filled with 50 random integers
 *   from 1 to 20. The removeDuplicates method returns a NEW ArrayList that
 *   holds every distinct value from the original list, kept in the order in
 *   which each value first appeared. The original list is never changed.
 *
 * How to compile and run (from the folder that holds this file):
 *   javac RemoveDuplicatesTest.java
 *   java RemoveDuplicatesTest
 *
 * What the program does:
 *   1. Builds the random Integer list, removes the duplicates, prints both
 *      lists, and checks that the result is correct.
 *   2. Runs removeDuplicates on a small String list to show the method is
 *      generic and works with any element type.
 *   3. Passes bad input on purpose (a null list, a reversed range, and a
 *      negative size) to show that each problem is caught and reported.
 *
 * Error handling:
 *   Every caught exception is printed to the console with a plain message
 *   and is also written to the log file removeDuplicates.log. The log file
 *   is recreated on each run, so it always matches the latest console
 *   output. If the log file cannot be created, the program says so on the
 *   console and keeps running with console output only.
 *
 * References:
 *   Liang, Y. D. (2024). Introduction to Java programming and data structures
 *     (13th ed., Chapter 19: Generics). Pearson.
 *   Oracle. (n.d.-a). Java logging overview.
 *     https://docs.oracle.com/en/java/javase/21/core/java-logging-overview.html
 *   Oracle. (n.d.-b). The Java tutorials: Generics.
 *     https://docs.oracle.com/javase/tutorial/java/generics/index.html
 *   Payne, D. (n.d.). Module 3 generics examples (Example_01 through
 *     Example_13, GenericStack). Bellevue University.
 *
 * The generic static method follows the pattern shown in Example_13 and in
 * Chapter 19 of the textbook.
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class RemoveDuplicatesTest {

    /** Number of random values placed in the original list. */
    private static final int LIST_SIZE = 50;

    /** Smallest random value allowed (inclusive). */
    private static final int MIN_VALUE = 1;

    /** Largest random value allowed (inclusive). */
    private static final int MAX_VALUE = 20;

    /** How many values are printed on each line of output. */
    private static final int VALUES_PER_LINE = 10;

    /** Name of the log file that stores program events and errors. */
    private static final String LOG_FILE = "removeDuplicates.log";

    /** One-line log format: date, time, level, message, and stack trace. */
    private static final String LOG_FORMAT = "%1$tF %1$tT [%4$s] %5$s%6$s%n";

    /** Logger shared by the whole program. */
    private static final Logger LOGGER =
            Logger.getLogger(RemoveDuplicatesTest.class.getName());

    /** Handler that writes log records to LOG_FILE; null if it could not open. */
    private static FileHandler fileHandler;

    /**
     * Entry point. Runs the Integer test, the String test, and the error
     * handling tests, then closes the log file.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {

        setUpLogging();
        LOGGER.info("Program started.");

        try {
            runIntegerTest();
            runStringTest();
            runErrorHandlingTests();

            System.out.println("All tests finished. Details were saved to " + LOG_FILE + ".");
            LOGGER.info("Program finished successfully.");

        } catch (IllegalArgumentException | IllegalStateException ex) {
            reportError("The program could not finish: " + ex.getMessage(), ex);
        } catch (RuntimeException ex) {
            reportError("An unexpected error occurred: " + ex.getMessage(), ex);
        } finally {
            closeLogging();
        }
    }

    /**
     * Returns a new ArrayList that contains each value from the given list
     * only once, in the order the values first appear. The list passed in
     * is not modified.
     *
     * A LinkedHashSet is used because it rejects repeated values and keeps
     * insertion order, which lets the method run in O(n) time instead of
     * the O(n^2) time a nested contains() check would take.
     *
     * @param list the list to remove duplicates from; must not be null
     * @param <E>  the type of element stored in the list
     * @return a new ArrayList with no duplicate values
     * @throws IllegalArgumentException if list is null
     */
    public static <E> ArrayList<E> removeDuplicates(ArrayList<E> list) {

        if (list == null) {
            throw new IllegalArgumentException(
                    "The list passed to removeDuplicates cannot be null.");
        }

        return new ArrayList<>(new LinkedHashSet<>(list));
    }

    /**
     * Builds an ArrayList filled with random integers in the range
     * [min, max], both ends included.
     *
     * @param size   number of values to generate; must be zero or more
     * @param min    smallest value allowed
     * @param max    largest value allowed; must not be less than min
     * @param random the random number generator to use; must not be null
     * @return a new ArrayList of random integers
     * @throws IllegalArgumentException if any argument is invalid
     */
    public static ArrayList<Integer> createRandomList(int size, int min, int max,
                                                      Random random) {

        if (size < 0) {
            throw new IllegalArgumentException(
                    "The list size must be zero or greater, but it was " + size + ".");
        }
        if (min > max) {
            throw new IllegalArgumentException("The minimum value (" + min
                    + ") cannot be greater than the maximum value (" + max + ").");
        }
        if (random == null) {
            throw new IllegalArgumentException(
                    "A Random object is required to generate values.");
        }

        ArrayList<Integer> values = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            values.add(random.nextInt(max - min + 1) + min);
        }

        return values;
    }

    /**
     * Main test for the assignment. Fills a list with 50 random values from
     * 1 to 20, removes the duplicates, prints both lists, and verifies the
     * result.
     */
    private static void runIntegerTest() {

        System.out.println("Test 1: Integer list with " + LIST_SIZE
                + " random values from " + MIN_VALUE + " to " + MAX_VALUE);
        System.out.println();

        ArrayList<Integer> originalList =
                createRandomList(LIST_SIZE, MIN_VALUE, MAX_VALUE, new Random());

        // Copy taken before the call so we can prove the original list
        // was not modified by removeDuplicates.
        ArrayList<Integer> originalCopy = new ArrayList<>(originalList);

        ArrayList<Integer> uniqueList = removeDuplicates(originalList);

        printList("Original list", originalList);
        printList("List with duplicates removed", uniqueList);

        int duplicatesRemoved = originalList.size() - uniqueList.size();
        System.out.println("Duplicates removed: " + duplicatesRemoved);

        verifyResult(originalCopy, originalList, uniqueList);

        LOGGER.info("Integer test passed. " + uniqueList.size()
                + " distinct values kept and " + duplicatesRemoved + " duplicates removed.");
        System.out.println();
    }

    /**
     * Second test that uses Strings to show the method is truly generic and
     * not tied to Integer.
     */
    private static void runStringTest() {

        System.out.println("Test 2: String list (shows the method is generic)");
        System.out.println();

        ArrayList<String> words = new ArrayList<>(
                List.of("Java", "Python", "Java", "C#", "Python", "SQL"));
        ArrayList<String> uniqueWords = removeDuplicates(words);

        printList("Original list", words);
        printList("List with duplicates removed", uniqueWords);

        LOGGER.info("String test passed. Result: " + uniqueWords);
        System.out.println();
    }

    /**
     * Passes bad input on purpose so the error handling can be seen. Each
     * call should throw an IllegalArgumentException, which is caught and
     * reported instead of crashing the program.
     */
    private static void runErrorHandlingTests() {

        System.out.println("Test 3: Error handling with invalid input");
        System.out.println();

        expectInvalidInput("removeDuplicates(null)",
                () -> removeDuplicates(null));

        expectInvalidInput("createRandomList with the range reversed (20 to 1)",
                () -> createRandomList(LIST_SIZE, MAX_VALUE, MIN_VALUE, new Random()));

        expectInvalidInput("createRandomList with a size of -5",
                () -> createRandomList(-5, MIN_VALUE, MAX_VALUE, new Random()));

        System.out.println();
    }

    /**
     * Runs an action that should fail with an IllegalArgumentException.
     * The caught exception is printed to the console and written to the log.
     * If no exception is thrown, the input check is not working, so an
     * IllegalStateException is thrown to report the problem.
     *
     * @param description short text that names the call being tested
     * @param action      the call that should be rejected
     * @throws IllegalStateException if the action does not throw as expected
     */
    private static void expectInvalidInput(String description, Runnable action) {

        try {
            action.run();
        } catch (IllegalArgumentException ex) {
            System.out.println("Caught expected exception for " + description + ":");
            System.out.println("    " + ex.getMessage());
            LOGGER.log(Level.WARNING, "Handled invalid input for {0}: {1}",
                    new Object[] {description, ex.getMessage()});
            return;
        }

        throw new IllegalStateException("No exception was thrown for "
                + description + ", but one was expected.");
    }

    /**
     * Confirms that removeDuplicates did its job. The checks are: the
     * original list still has 50 values and was not changed, the result has
     * no repeated values, the result holds every value from the original
     * list and nothing extra, and every value lies in the allowed range.
     *
     * @param before     copy of the original list taken before the call
     * @param after      the original list after the call
     * @param uniqueList the list returned by removeDuplicates
     * @throws IllegalStateException if any check fails
     */
    private static void verifyResult(ArrayList<Integer> before,
                                     ArrayList<Integer> after,
                                     ArrayList<Integer> uniqueList) {

        check(after.size() == LIST_SIZE,
                "The original list should hold " + LIST_SIZE
                + " values, but it holds " + after.size() + ".");
        check(before.equals(after),
                "The original list was changed by removeDuplicates.");
        check(new LinkedHashSet<>(uniqueList).size() == uniqueList.size(),
                "The returned list still contains duplicate values.");
        check(uniqueList.containsAll(after),
                "The returned list is missing a value from the original list.");
        check(after.containsAll(uniqueList),
                "The returned list contains a value that is not in the original list.");

        for (int value : uniqueList) {
            check(value >= MIN_VALUE && value <= MAX_VALUE,
                    "The value " + value + " is outside the range "
                    + MIN_VALUE + " to " + MAX_VALUE + ".");
        }

        System.out.println("Verification passed: the original list is unchanged, "
                + "no duplicates remain, and no values were lost.");
    }

    /**
     * Throws an IllegalStateException with the given message when a
     * verification condition is false. Keeps verifyResult short and avoids
     * repeating the same if-throw block.
     *
     * @param condition      the condition that must be true
     * @param failureMessage the message to use if the condition is false
     * @throws IllegalStateException if condition is false
     */
    private static void check(boolean condition, String failureMessage) {
        if (!condition) {
            throw new IllegalStateException(failureMessage);
        }
    }

    /**
     * Prints a labeled list along with its size, showing a fixed number of
     * values on each line so a 50-value list is easy to read.
     *
     * @param label text shown before the list
     * @param list  the list to print
     * @param <E>   the type of element stored in the list
     */
    private static <E> void printList(String label, ArrayList<E> list) {

        System.out.println(label + " (" + list.size() + " values):");

        if (list.isEmpty()) {
            System.out.println("    (empty)");
            return;
        }

        for (int i = 0; i < list.size(); i++) {
            E value = list.get(i);

            // Numbers are padded so the columns line up; other types
            // such as Strings are printed as they are.
            String text = (value instanceof Number)
                    ? String.format("%3s", value)
                    : String.valueOf(value);

            boolean firstOnLine = i % VALUES_PER_LINE == 0;
            boolean lastOnLine = (i + 1) % VALUES_PER_LINE == 0
                    || i == list.size() - 1;

            System.out.print((firstOnLine ? "   " : " ") + text);
            System.out.print(lastOnLine ? System.lineSeparator() : ",");
        }
    }

    /**
     * Writes an error message to the console and to the log file, including
     * the stack trace in the log so the cause can be tracked down later.
     *
     * @param message a readable description of the problem
     * @param ex      the exception that caused the problem
     */
    private static void reportError(String message, Exception ex) {
        System.out.println("ERROR: " + message);
        LOGGER.log(Level.SEVERE, message, ex);
    }

    /**
     * Sends log records to LOG_FILE in a short one-line format. The file is
     * recreated on each run so it always matches the latest console output.
     * The logger's default console handler is turned off because the program
     * already prints its own readable messages to the console; this keeps
     * the same message from being shown twice. If the file cannot be opened,
     * a message is shown and the program keeps running.
     */
    private static void setUpLogging() {

        System.setProperty("java.util.logging.SimpleFormatter.format", LOG_FORMAT);
        LOGGER.setUseParentHandlers(false);
        LOGGER.setLevel(Level.INFO);

        try {
            fileHandler = new FileHandler(LOG_FILE, false);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
        } catch (IOException | SecurityException ex) {
            System.out.println("Warning: could not open the log file \"" + LOG_FILE
                    + "\" (" + ex.getMessage() + "). Messages will go to the console only.");
        }
    }

    /**
     * Flushes and closes the log file so every record is saved and the
     * file lock is released before the program ends.
     */
    private static void closeLogging() {
        if (fileHandler != null) {
            LOGGER.removeHandler(fileHandler);
            fileHandler.close();
        }
    }
}

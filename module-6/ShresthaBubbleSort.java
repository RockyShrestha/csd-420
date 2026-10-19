/*
 * Name:        Rakesh Shrestha
 * Course:      CSD420-T301 Advanced Java Programming
 * Instructor:  Professor Tedi Pano
 * Assignment:  Module 6.2 Programming Assignment
 * Date:        October 18, 2026
 *
 * Description:
 *   This program solves Programming Exercise 23.1 (Generic bubble sort)
 *   from the Liang textbook. It contains two generic methods that sort an
 *   array using the bubble sort algorithm.
 *     1. bubbleSort(E[] list) sorts elements by their natural order using
 *        the Comparable interface.
 *     2. bubbleSort(E[] list, Comparator<? super E> comparator) sorts
 *        elements in whatever order the given Comparator defines.
 *   The main method runs a set of tests that check both methods with
 *   Integers, Strings, Doubles, and a custom Student class. The tests also
 *   cover edge cases such as empty arrays, single elements, duplicates, and
 *   invalid input (null arrays, null elements, and a null comparator).
 *
 * How bubble sort works:
 *   Each pass walks through the array and compares neighboring elements.
 *   If two neighbors are out of order they are swapped, so the largest
 *   remaining value "bubbles" to the end of the array. If a full pass makes
 *   no swaps, the array is already sorted and the method stops early.
 *
 * Attribution:
 *   The basic bubble sort loop is based on Bubble_Sort.java, the Module 6
 *   example written by Professor Darrell Payne (Bellevue University), and on
 *   the bubble sort discussion in Liang, Y. D. (2024). Introduction to Java
 *   Programming and Data Structures (13th ed.), Chapter 23, Sorting. Pearson.
 *   The generic methods, helper methods, and test code were written by me.
 */

import java.util.Arrays;
import java.util.Comparator;

public class ShresthaBubbleSort {

    // Counters used by the test code to report a final summary
    private static int testsRun = 0;
    private static int testsPassed = 0;

    // =====================================================================
    // Generic sort methods
    // =====================================================================

    /**
     * Sorts an array in ascending natural order using bubble sort and the
     * Comparable interface.
     *
     * @param list the array to sort; it is sorted in place
     * @param <E>  any type that can compare itself to other values of its type
     * @throws IllegalArgumentException if the array or any element is null
     */
    public static <E extends Comparable<E>> void bubbleSort(E[] list) {
        validateArray(list);
        validateElements(list);

        boolean needNextPass = true;

        // After each pass, the largest unsorted value is in its final spot,
        // so every pass can stop one position earlier than the last one.
        for (int pass = 1; pass < list.length && needNextPass; pass++) {
            needNextPass = false;

            for (int i = 0; i < list.length - pass; i++) {
                // compareTo returns a positive number when list[i] is larger
                if (list[i].compareTo(list[i + 1]) > 0) {
                    swap(list, i, i + 1);
                    needNextPass = true;
                }
            }
        }
    }

    /**
     * Sorts an array using bubble sort and the order defined by a Comparator.
     *
     * @param list       the array to sort; it is sorted in place
     * @param comparator decides the order of any two elements
     * @param <E>        the type of the elements in the array
     * @throws IllegalArgumentException if the array or comparator is null
     */
    public static <E> void bubbleSort(E[] list, Comparator<? super E> comparator) {
        validateArray(list);

        if (comparator == null) {
            throw new IllegalArgumentException("The comparator cannot be null.");
        }

        boolean needNextPass = true;

        for (int pass = 1; pass < list.length && needNextPass; pass++) {
            needNextPass = false;

            for (int i = 0; i < list.length - pass; i++) {
                // The comparator decides if the two neighbors are out of order
                if (comparator.compare(list[i], list[i + 1]) > 0) {
                    swap(list, i, i + 1);
                    needNextPass = true;
                }
            }
        }
    }

    // =====================================================================
    // Helper methods shared by both sort methods
    // =====================================================================

    /** Swaps the two elements at the given positions. */
    private static <E> void swap(E[] list, int first, int second) {
        E temp = list[first];
        list[first] = list[second];
        list[second] = temp;
    }

    /** Makes sure the array itself is not null before sorting. */
    private static <E> void validateArray(E[] list) {
        if (list == null) {
            throw new IllegalArgumentException("The array to sort cannot be null.");
        }
    }

    /**
     * Makes sure no element is null. The Comparable version calls compareTo
     * on each element, so a null element would cause a NullPointerException.
     */
    private static <E> void validateElements(E[] list) {
        for (int i = 0; i < list.length; i++) {
            if (list[i] == null) {
                throw new IllegalArgumentException(
                        "The array contains a null element at index " + i + ".");
            }
        }
    }

    // =====================================================================
    // Test code
    // =====================================================================

    public static void main(String[] args) {
        System.out.println("Module 6.2 Generic Bubble Sort Tests");
        System.out.println("Rakesh Shrestha, CSD420-T301");
        System.out.println("====================================================");

        testComparableSort();
        testComparatorSort();
        testEdgeCases();
        testInvalidInput();

        System.out.println("====================================================");
        System.out.printf("Results: %d of %d tests passed.%n", testsPassed, testsRun);

        if (testsPassed == testsRun) {
            System.out.println("All tests passed. Both sort methods work correctly.");
        } else {
            System.out.println("Some tests failed. Please review the output above.");
        }
    }

    /** Tests the Comparable version with several element types. */
    private static void testComparableSort() {
        System.out.println("\n--- Comparable interface (natural order) ---");

        Integer[] numbers = {5, 3, 4, 9, 0, 1, 2, 7, 6, 8};
        runComparableTest("Integers in random order", numbers);

        Integer[] reversed = {9, 8, 7, 6, 5, 4, 3, 2, 1, 0};
        runComparableTest("Integers in reverse order", reversed);

        String[] cities = {"Omaha", "Kathmandu", "Bellevue", "Dallas", "Austin"};
        runComparableTest("Strings in alphabetical order", cities);

        Double[] prices = {19.99, 4.50, 100.0, -2.75, 0.0};
        runComparableTest("Doubles including a negative value", prices);

        Student[] students = createStudents();
        runComparableTest("Students by GPA (custom Comparable class)", students);
    }

    /** Tests the Comparator version with several different orderings. */
    private static void testComparatorSort() {
        System.out.println("\n--- Comparator interface (custom order) ---");

        Integer[] numbers = {5, 3, 4, 9, 0, 1, 2, 7, 6, 8};
        runComparatorTest("Integers in descending order",
                numbers, Comparator.reverseOrder());

        String[] words = {"banana", "Fig", "kiwi", "Apple", "cherry"};
        runComparatorTest("Strings ignoring case",
                words, String.CASE_INSENSITIVE_ORDER);

        String[] animals = {"elephant", "cat", "giraffe", "ox", "zebra"};
        runComparatorTest("Strings by length",
                animals, Comparator.comparingInt(String::length));

        Student[] byName = createStudents();
        runComparatorTest("Students by name",
                byName, Comparator.comparing(Student::getName));

        Student[] byGpaDesc = createStudents();
        runComparatorTest("Students by GPA, highest first",
                byGpaDesc, Comparator.comparingDouble(Student::getGpa).reversed());
    }

    /** Tests small or unusual arrays that often break sorting code. */
    private static void testEdgeCases() {
        System.out.println("\n--- Edge cases ---");

        runComparableTest("Empty array", new Integer[0]);
        runComparableTest("Single element", new Integer[] {42});
        runComparableTest("Already sorted", new Integer[] {1, 2, 3, 4, 5});
        runComparableTest("Duplicate values", new Integer[] {3, 1, 3, 2, 1, 3});
        runComparatorTest("Two elements with a comparator",
                new String[] {"b", "a"}, Comparator.naturalOrder());
    }

    /**
     * Checks that bad input is rejected with a clear message instead of
     * crashing the program. Each caught exception is printed to the console.
     */
    private static void testInvalidInput() {
        System.out.println("\n--- Invalid input (exceptions expected) ---");

        // A null array should be rejected by the Comparable version
        try {
            Integer[] nullArray = null;
            bubbleSort(nullArray);
            recordResult("Null array (Comparable)", false);
        } catch (IllegalArgumentException ex) {
            System.out.println("  Caught exception: " + ex.getMessage());
            recordResult("Null array (Comparable)", true);
        }

        // A null element should be rejected by the Comparable version
        try {
            Integer[] withNull = {4, null, 2};
            bubbleSort(withNull);
            recordResult("Null element (Comparable)", false);
        } catch (IllegalArgumentException ex) {
            System.out.println("  Caught exception: " + ex.getMessage());
            recordResult("Null element (Comparable)", true);
        }

        // A null comparator should be rejected by the Comparator version
        try {
            Integer[] numbers = {3, 2, 1};
            bubbleSort(numbers, null);
            recordResult("Null comparator", false);
        } catch (IllegalArgumentException ex) {
            System.out.println("  Caught exception: " + ex.getMessage());
            recordResult("Null comparator", true);
        }
    }

    // =====================================================================
    // Test helpers (kept separate so each test above stays short)
    // =====================================================================

    /**
     * Sorts a copy of the array with Arrays.sort as the expected answer,
     * then sorts the original with my bubble sort and compares the two.
     */
    private static <E extends Comparable<E>> void runComparableTest(
            String testName, E[] list) {
        E[] expected = Arrays.copyOf(list, list.length);
        Arrays.sort(expected);

        System.out.println("  Before: " + Arrays.toString(list));
        bubbleSort(list);
        System.out.println("  After:  " + Arrays.toString(list));

        recordResult(testName, Arrays.equals(list, expected));
    }

    /** Same idea as runComparableTest, but for the Comparator version. */
    private static <E> void runComparatorTest(
            String testName, E[] list, Comparator<? super E> comparator) {
        E[] expected = Arrays.copyOf(list, list.length);
        Arrays.sort(expected, comparator);

        System.out.println("  Before: " + Arrays.toString(list));
        bubbleSort(list, comparator);
        System.out.println("  After:  " + Arrays.toString(list));

        recordResult(testName, Arrays.equals(list, expected));
    }

    /** Prints PASS or FAIL for a test and updates the counters. */
    private static void recordResult(String testName, boolean passed) {
        testsRun++;
        if (passed) {
            testsPassed++;
        }
        System.out.println((passed ? "[PASS] " : "[FAIL] ") + testName + "\n");
    }

    /** Builds a fresh array of students so each test starts unsorted. */
    private static Student[] createStudents() {
        return new Student[] {
            new Student("Maya", 3.45),
            new Student("Aarav", 3.90),
            new Student("Liam", 2.80),
            new Student("Sofia", 3.10),
            new Student("Noah", 3.65)
        };
    }

    // =====================================================================
    // Custom class used to test sorting of user-defined objects
    // =====================================================================

    /**
     * A simple student record. Its natural order (compareTo) is by GPA from
     * lowest to highest, so it can be used with the Comparable version.
     */
    static class Student implements Comparable<Student> {
        private final String name;
        private final double gpa;

        Student(String name, double gpa) {
            this.name = name;
            this.gpa = gpa;
        }

        public String getName() {
            return name;
        }

        public double getGpa() {
            return gpa;
        }

        @Override
        public int compareTo(Student other) {
            return Double.compare(this.gpa, other.gpa);
        }

        @Override
        public String toString() {
            return name + " (" + gpa + ")";
        }
    }
}

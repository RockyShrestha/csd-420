/*
 * Rakesh Shrestha
 * CSD-420 Advanced Java Programming
 * Module 4.2 Programming Assignment
 * October 4, 2026
 *
 * Test Code for LinkedListTraversalTimer
 *
 * These tests make sure LinkedListTraversalTimer works correctly:
 *   - buildList() creates the right number of elements in the right order
 *   - sumWithIterator() and sumWithGetIndex() both return the correct sum
 *   - both methods give the same answer for 50,000 and 500,000 integers
 *   - a negative size is rejected with an IllegalArgumentException
 *
 * Each test prints PASS or FAIL to the console, and a summary is printed
 * at the end.
 *
 * Plain Java is used (no JUnit), so the tests can be run from IntelliJ or
 * from the command line with:
 *     javac LinkedListTraversalTimer.java LinkedListTraversalTimerTest.java
 *     java LinkedListTraversalTimerTest
 *
 * Note: the 500,000 test calls get(index) on a large LinkedList, so it
 * takes a few minutes to finish. That delay is expected (see Section
 * 20.5.2 of the textbook).
 */

import java.util.LinkedList;

public class LinkedListTraversalTimerTest {

    // Counters for the summary at the end
    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {

        System.out.println("Running tests for LinkedListTraversalTimer");
        System.out.println();

        testBuildListSize();
        testBuildListOrder();
        testEmptyList();
        testNegativeSizeThrowsException();
        testIteratorSum();
        testGetIndexSum();
        testBothMethodsMatch(50000);
        testBothMethodsMatch(500000);

        System.out.println();
        System.out.println("Tests passed: " + testsPassed);
        System.out.println("Tests failed: " + testsFailed);

        // Exit with an error code if any test failed
        if (testsFailed > 0) {
            System.exit(1);
        }
    }

    // The list should have exactly the number of elements asked for
    private static void testBuildListSize() {
        LinkedList<Integer> list = LinkedListTraversalTimer.buildList(50000);
        check("buildList creates 50,000 elements", list.size() == 50000);
    }

    // The list should hold 0, 1, 2, ... 9 in order
    private static void testBuildListOrder() {
        LinkedList<Integer> list = LinkedListTraversalTimer.buildList(10);
        boolean inOrder = true;
        int expected = 0;

        // foreach loop (Section 20.4)
        for (int value : list) {
            if (value != expected) {
                inOrder = false;
                break;
            }
            expected++;
        }
        check("buildList stores values 0 through 9 in order", inOrder);
    }

    // An empty list should not cause an error and should sum to 0
    private static void testEmptyList() {
        LinkedList<Integer> list = LinkedListTraversalTimer.buildList(0);
        check("Empty list sums to 0 with Iterator",
                LinkedListTraversalTimer.sumWithIterator(list) == 0);
        check("Empty list sums to 0 with get(index)",
                LinkedListTraversalTimer.sumWithGetIndex(list) == 0);
    }

    // A negative size should throw an IllegalArgumentException
    private static void testNegativeSizeThrowsException() {
        try {
            LinkedListTraversalTimer.buildList(-5);
            // If we get here, no exception was thrown, so the test fails
            check("Negative size throws IllegalArgumentException", false);
        }
        catch (IllegalArgumentException e) {
            System.out.println("    Caught expected exception: "
                    + e.getMessage());
            check("Negative size throws IllegalArgumentException", true);
        }
    }

    // 0 + 1 + ... + 99 = 4,950
    private static void testIteratorSum() {
        LinkedList<Integer> list = LinkedListTraversalTimer.buildList(100);
        check("Iterator sum of 0..99 equals 4,950",
                LinkedListTraversalTimer.sumWithIterator(list) == 4950);
    }

    // 0 + 1 + ... + 99 = 4,950
    private static void testGetIndexSum() {
        LinkedList<Integer> list = LinkedListTraversalTimer.buildList(100);
        check("get(index) sum of 0..99 equals 4,950",
                LinkedListTraversalTimer.sumWithGetIndex(list) == 4950);
    }

    // Both methods should return the same sum, and it should match the
    // formula n(n - 1) / 2 for the numbers 0 to n - 1
    private static void testBothMethodsMatch(int size) {
        LinkedList<Integer> list = LinkedListTraversalTimer.buildList(size);
        long expectedSum = (long) size * (size - 1) / 2;
        long iteratorSum = LinkedListTraversalTimer.sumWithIterator(list);
        long getIndexSum = LinkedListTraversalTimer.sumWithGetIndex(list);

        check(String.format("Both methods return %,d for %,d elements",
                expectedSum, size),
                iteratorSum == expectedSum && getIndexSum == expectedSum);
    }

    // Prints PASS or FAIL for one test and updates the counters
    private static void check(String description, boolean passed) {
        if (passed) {
            testsPassed++;
            System.out.println("  PASS: " + description);
        }
        else {
            testsFailed++;
            System.out.println("  FAIL: " + description);
        }
    }
}

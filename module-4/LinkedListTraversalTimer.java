/*
 * Rakesh Shrestha
 * CSD-420 Advanced Java Programming
 * Module 4.2 Programming Assignment
 * October 4, 2026
 *
 * LinkedList Traversal: Iterator vs. get(index)
 *
 * This program stores integers in a LinkedList and times how long it takes
 * to go through the whole list two different ways:
 *   1. Using an Iterator (hasNext() and next())
 *   2. Using a for loop that calls get(index) for every position
 *
 * The program runs the test with 50,000 integers and then with 500,000
 * integers so the two sizes can be compared.
 *
 * References:
 *   Liang, Introduction to Java Programming and Data Structures, 13th ed.
 *     Section 20.3  Iterators (LiveExample 20.2 TestIterator.java)
 *     Section 20.5.2  The ArrayList and LinkedList Classes, which warns
 *       that get(i) on a linked list is "a time-consuming operation" and
 *       that a for loop calling list.get(i) is "very inefficient"
 *   Professor Darrell Payne, Module 4 examples:
 *     Example_13.java (Iterator with a while loop)
 *     Example_15.java (ArrayList vs. LinkedList)
 *
 * ---------------------------------------------------------------------------
 * RESULTS AND DISCUSSION
 * ---------------------------------------------------------------------------
 * These are the times from my run in IntelliJ (times will be a little
 * different on another computer, but the pattern is the same):
 *
 *   Size        Iterator      get(index)                  Ratio
 *   50,000      4.5 ms        980 ms    (about 1 second)    ~216x slower
 *   500,000     5.9 ms        117,019 ms (about 2 minutes)  ~19,900x slower
 *
 * Iterator:
 *   Section 20.3 explains that an Iterator walks through a collection one
 *   element at a time. For a LinkedList, the Iterator remembers which node
 *   it is on, so each call to next() just moves to the next node. That
 *   means going through the whole list takes time proportional to n, or
 *   O(n). Both runs finished in under 10 milliseconds. The 500,000 run was
 *   not 10 times slower than the 50,000 run because the first run also
 *   included the JVM warming up (the just-in-time compiler had not sped up
 *   the loop yet). Either way, the Iterator stayed fast even when the list
 *   got 10 times bigger.
 *
 * get(index):
 *   Section 20.5.2 explains that a LinkedList stores its elements in nodes
 *   instead of an array, so get(index) cannot jump straight to a position.
 *   Every call starts at the front or back of the list (whichever is
 *   closer) and moves one node at a time until it reaches the index. One
 *   call is O(n), and calling it for every index in a loop makes the whole
 *   traversal O(n^2).
 *
 *   My results match this. With 50,000 integers the get(index) loop took
 *   about 1 second, which was already about 216 times slower than the
 *   Iterator. With 500,000 integers the list was only 10 times bigger, but
 *   the get(index) loop took about 117 seconds, about 119 times longer than
 *   at 50,000. That is close to the 100 times (10 squared) that O(n^2)
 *   predicts. The difference between the two methods also grew a lot, from
 *   about 216 times slower to about 19,900 times slower, because the
 *   Iterator time grows with n while the get(index) time grows with n
 *   squared.
 *
 * Conclusion:
 *   This shows why the textbook says not to use get(i) to traverse a
 *   LinkedList. An Iterator or a foreach loop (Section 20.4) should be used
 *   instead. get(index) works well on an ArrayList because it uses an
 *   array, but on a LinkedList it turns a fast linear pass into a very
 *   slow one as the list grows.
 * ---------------------------------------------------------------------------
 */

import java.util.Iterator;
import java.util.LinkedList;

public class LinkedListTraversalTimer {

    // The two list sizes the assignment asks to test
    private static final int[] TEST_SIZES = {50000, 500000};

    // Used to convert nanoseconds into milliseconds
    private static final double NANOS_PER_MILLISECOND = 1000000.0;

    public static void main(String[] args) {

        System.out.println("CSD-420 Module 4.2: LinkedList Traversal Timing");
        System.out.println("Iterator vs. get(index)");
        System.out.println();

        // Run the same timing test for each list size
        for (int size : TEST_SIZES) {
            try {
                runTimingTest(size);
            }
            catch (IllegalArgumentException e) {
                System.err.println("Invalid list size: " + e.getMessage());
            }
            catch (OutOfMemoryError e) {
                System.err.println("Not enough memory to build a list of "
                        + size + " integers.");
            }
        }
    }

    // Builds a list of the given size, times both traversals,
    // and prints the results
    public static void runTimingTest(int size) {

        LinkedList<Integer> list = buildList(size);

        System.out.printf("List size: %,d integers%n", size);

        // Time the Iterator traversal
        long startTime = System.nanoTime();
        long iteratorSum = sumWithIterator(list);
        long iteratorTime = System.nanoTime() - startTime;

        System.out.printf("  Iterator traversal:   %,12.3f ms%n",
                iteratorTime / NANOS_PER_MILLISECOND);

        // Let the user know the big list will take a while
        if (size > 100000) {
            System.out.println("  (get(index) is slow on large lists, "
                    + "so this may take a few minutes...)");
        }

        // Time the get(index) traversal
        startTime = System.nanoTime();
        long getIndexSum = sumWithGetIndex(list);
        long getIndexTime = System.nanoTime() - startTime;

        System.out.printf("  get(index) traversal: %,12.3f ms%n",
                getIndexTime / NANOS_PER_MILLISECOND);

        // Both loops visit the same numbers, so the sums should be equal
        System.out.println("  Sums match: " + (iteratorSum == getIndexSum)
                + " (sum = " + String.format("%,d", iteratorSum) + ")");

        // Show how many times slower get(index) was
        if (iteratorTime > 0) {
            System.out.printf("  get(index) was about %,.0f times slower%n",
                    (double) getIndexTime / iteratorTime);
        }
        System.out.println();
    }

    // Creates a LinkedList that holds the integers 0 to size - 1
    public static LinkedList<Integer> buildList(int size) {

        // A list cannot have a negative size
        if (size < 0) {
            throw new IllegalArgumentException(
                    "The size cannot be negative (received " + size + ").");
        }

        LinkedList<Integer> list = new LinkedList<>();

        for (int i = 0; i < size; i++) {
            list.add(i);
        }
        return list;
    }

    // Adds up every element using an Iterator (Section 20.3, Example_13)
    // Each next() call moves one node forward, so this is O(n)
    public static long sumWithIterator(LinkedList<Integer> list) {

        long sum = 0;

        // Create the iterator after the list is filled (see Example_13)
        Iterator<Integer> iterator = list.iterator();

        while (iterator.hasNext()) {
            sum += iterator.next();
        }
        return sum;
    }

    // Adds up every element using get(index) (Section 20.5.2)
    // Each get(i) call walks from the closest end of the list,
    // so this whole loop is O(n^2)
    public static long sumWithGetIndex(LinkedList<Integer> list) {

        long sum = 0;

        for (int i = 0; i < list.size(); i++) {
            sum += list.get(i);
        }
        return sum;
    }
}

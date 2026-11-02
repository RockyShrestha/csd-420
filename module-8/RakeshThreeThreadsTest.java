/*
 * Rakesh Shrestha
 * CSD420-T301 Advanced Java Programming
 * Module 8.2 Programming Assignment
 * November 1, 2026
 *
 * Purpose:
 *   Test code for RakeshThreeThreads. These tests check the methods of the
 *   CharacterGenerator and CharacterBuffer classes without opening the
 *   JavaFX window, so they run quickly from the console.
 *
 *   Tests:
 *     1. nextRandomCharacter() only returns characters from its own set.
 *     2. One generator produces exactly 10,000 characters and reports that it finished.
 *     3. Three threads together produce 30,000 characters, 10,000 of each type.
 *     4. The output is mixed together and not grouped by thread.
 *     5. stopRunning() stops a thread before it finishes.
 *     6. The constructor rejects bad input with IllegalArgumentException.
 *     7. drain() returns the characters and leaves the buffer empty.
 *
 *   Each test prints PASS or FAIL. A summary prints at the end.
 *
 * Attribution:
 *   Thread start() and join() usage is based on Professor Darrell Payne's
 *   Module 8 examples (Example_12.java and Joiner1.java), Bellevue University.
 */
public class RakeshThreeThreadsTest {

  private static int passed = 0;
  private static int failed = 0;

  public static void main(String[] args) {

    System.out.println("Running tests for RakeshThreeThreads\n");

    testRandomCharacterStaysInSet();
    testSingleGeneratorCount();
    testThreeThreadsTotals();
    testOutputIsMixed();
    testStopRunning();
    testInvalidInput();
    testBufferDrain();

    System.out.println("\nTests passed: " + passed);
    System.out.println("Tests failed: " + failed);

    if (failed > 0) {
      System.exit(1);
    }
  }

  // Test 1
  private static void testRandomCharacterStaysInSet() {

    boolean allValid = true;
    String[] sets = {RakeshThreeThreads.LETTERS, RakeshThreeThreads.DIGITS, RakeshThreeThreads.SYMBOLS};

    for (String set : sets) {
      RakeshThreeThreads.CharacterGenerator generator = newGenerator(set, 1, 0, new RakeshThreeThreads.CharacterBuffer());

      for (int i = 0; i < 5_000; i++) {
        if (set.indexOf(generator.nextRandomCharacter()) < 0) {
          allValid = false;
        }
      }
    }

    check(allValid, "nextRandomCharacter() only returns characters from its own set");
  }

  // Test 2
  private static void testSingleGeneratorCount() {

    RakeshThreeThreads.CharacterBuffer buffer = new RakeshThreeThreads.CharacterBuffer();
    RakeshThreeThreads.CharacterGenerator generator =
        newGenerator(RakeshThreeThreads.LETTERS, RakeshThreeThreads.CHARACTERS_PER_THREAD, 0, buffer);

    generator.run();   // run on this thread, just to test the logic
    String output = buffer.drain();

    check(output.length() == RakeshThreeThreads.CHARACTERS_PER_THREAD,
        String.format("Single generator produced %,d characters (expected 10,000)", output.length()));
    check(generator.getGeneratedCount() == RakeshThreeThreads.CHARACTERS_PER_THREAD,
        "getGeneratedCount() returns 10,000");
    check(generator.isFinished(), "isFinished() returns true after the generator is done");
  }

  // Test 3
  private static void testThreeThreadsTotals() {

    String output = runThreeThreads(RakeshThreeThreads.CHARACTERS_PER_THREAD, 0);

    int letters = 0;
    int digits = 0;
    int symbols = 0;
    int unknown = 0;

    for (char character : output.toCharArray()) {
      switch (typeOf(character)) {
        case 'L': letters++; break;
        case 'D': digits++; break;
        case 'S': symbols++; break;
        default: unknown++;
      }
    }

    check(output.length() == 3 * RakeshThreeThreads.CHARACTERS_PER_THREAD,
        String.format("Three threads produced %,d characters in total (expected 30,000)", output.length()));
    check(letters == RakeshThreeThreads.CHARACTERS_PER_THREAD, String.format("Letter count is %,d (expected 10,000)", letters));
    check(digits == RakeshThreeThreads.CHARACTERS_PER_THREAD, String.format("Digit count is %,d (expected 10,000)", digits));
    check(symbols == RakeshThreeThreads.CHARACTERS_PER_THREAD, String.format("Symbol count is %,d (expected 10,000)", symbols));
    check(unknown == 0, "No unexpected characters were found");
  }

  // Test 4
  private static void testOutputIsMixed() {

    // A small pause makes the threads take turns, the same way the GUI runs
    String output = runThreeThreads(300, 1);

    // If each thread's output were grouped together, the type would only
    // change twice (letters to digits to symbols). Count the changes.
    int typeChanges = 0;
    for (int i = 1; i < output.length(); i++) {
      if (typeOf(output.charAt(i)) != typeOf(output.charAt(i - 1))) {
        typeChanges++;
      }
    }

    System.out.println("       Sample of mixed output: "
        + output.substring(0, Math.min(40, output.length())));
    check(typeChanges > 2, "Output is mixed between threads (" + typeChanges + " type changes found)");
  }

  // Test 5
  private static void testStopRunning() {

    RakeshThreeThreads.CharacterGenerator generator =
        newGenerator(RakeshThreeThreads.DIGITS, 1_000_000, 1, new RakeshThreeThreads.CharacterBuffer());
    Thread thread = new Thread(generator);
    thread.start();

    try {
      Thread.sleep(100);
      generator.stopRunning();
      thread.join(2_000);
    }
    catch (InterruptedException exception) {
      System.err.println("The stopRunning() test was interrupted: " + exception.getMessage());
      Thread.currentThread().interrupt();
    }

    check(!thread.isAlive(), "stopRunning() ends the thread");
    check(!generator.isFinished(), "Generator stopped early at " + generator.getGeneratedCount() + " characters");
  }

  // Test 6
  private static void testInvalidInput() {

    RakeshThreeThreads.CharacterBuffer buffer = new RakeshThreeThreads.CharacterBuffer();

    check(throwsIllegalArgument(() -> newGenerator("", 10, 0, buffer)), "Empty character set is rejected");
    check(throwsIllegalArgument(() -> newGenerator(null, 10, 0, buffer)), "Null character set is rejected");
    check(throwsIllegalArgument(() -> newGenerator("abc", 0, 0, buffer)), "A count of zero is rejected");
    check(throwsIllegalArgument(() -> newGenerator("abc", 10, -1, buffer)), "A negative pause is rejected");
    check(throwsIllegalArgument(() -> newGenerator("abc", 10, 0, null)), "A null buffer is rejected");
  }

  // Test 7
  private static void testBufferDrain() {

    RakeshThreeThreads.CharacterBuffer buffer = new RakeshThreeThreads.CharacterBuffer();
    buffer.add('a');
    buffer.add('7');
    buffer.add('#');

    check(buffer.drain().equals("a7#"), "drain() returns the characters in the order they were added");
    check(buffer.drain().isEmpty(), "drain() leaves the buffer empty");
  }

  // ---------- Helper methods ----------

  /*
   * Starts letter, digit, and symbol threads that share one buffer, waits for
   * all of them with join(), and returns everything they wrote.
   */
  private static String runThreeThreads(int countPerThread, int pauseMilliseconds) {

    RakeshThreeThreads.CharacterBuffer buffer = new RakeshThreeThreads.CharacterBuffer();
    StringBuilder allOutput = new StringBuilder();

    Thread[] threads = {
      new Thread(newGenerator(RakeshThreeThreads.LETTERS, countPerThread, pauseMilliseconds, buffer)),
      new Thread(newGenerator(RakeshThreeThreads.DIGITS, countPerThread, pauseMilliseconds, buffer)),
      new Thread(newGenerator(RakeshThreeThreads.SYMBOLS, countPerThread, pauseMilliseconds, buffer))
    };

    for (Thread thread : threads) {
      thread.start();
    }

    try {
      // Keep draining while the threads run, like the GUI does
      while (anyAlive(threads)) {
        allOutput.append(buffer.drain());
        Thread.sleep(5);
      }
      for (Thread thread : threads) {
        thread.join();
      }
    }
    catch (InterruptedException exception) {
      System.err.println("Waiting for the test threads was interrupted: " + exception.getMessage());
      Thread.currentThread().interrupt();
    }

    allOutput.append(buffer.drain());
    return allOutput.toString();
  }

  private static RakeshThreeThreads.CharacterGenerator newGenerator(
      String set, int count, int pause, RakeshThreeThreads.CharacterBuffer buffer) {
    return new RakeshThreeThreads.CharacterGenerator("Test", set, count, pause, buffer);
  }

  private static boolean anyAlive(Thread[] threads) {
    for (Thread thread : threads) {
      if (thread.isAlive()) {
        return true;
      }
    }
    return false;
  }

  /*
   * Returns L for a letter, D for a digit, S for a symbol, or ? for anything else.
   */
  private static char typeOf(char character) {
    if (RakeshThreeThreads.LETTERS.indexOf(character) >= 0) {
      return 'L';
    }
    if (RakeshThreeThreads.DIGITS.indexOf(character) >= 0) {
      return 'D';
    }
    if (RakeshThreeThreads.SYMBOLS.indexOf(character) >= 0) {
      return 'S';
    }
    return '?';
  }

  private static boolean throwsIllegalArgument(Runnable code) {
    try {
      code.run();
      return false;
    }
    catch (IllegalArgumentException expected) {
      return true;
    }
  }

  private static void check(boolean condition, String description) {
    if (condition) {
      passed++;
      System.out.println("PASS - " + description);
    }
    else {
      failed++;
      System.out.println("FAIL - " + description);
    }
  }
}

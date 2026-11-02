/*
 * Rakesh Shrestha
 * CSD420-T301 Advanced Java Programming
 * Module 8.2 Programming Assignment
 * November 1, 2026
 *
 * Purpose:
 *   This program uses three threads to write three kinds of random characters
 *   into a JavaFX TextArea.
 *     Thread 1 writes random lowercase letters (a, b, c, ...).
 *     Thread 2 writes random digits (0 through 9).
 *     Thread 3 writes random symbols (!, @, #, $, %, ^, &, *).
 *   Each thread generates 10,000 characters. Every character is added to the
 *   display the moment it is generated, so the output from the three threads
 *   ends up mixed together, for example: 179$^fq*fg1k1k2^%1m4
 *
 * How it works:
 *   One Runnable class (CharacterGenerator) is reused for all three threads.
 *   Only the character set changes, so the same code is not written three times.
 *   All three threads write into one shared CharacterBuffer. The add and drain
 *   methods of the buffer are synchronized so two threads can never change it
 *   at the same time. JavaFX controls can only be changed on the JavaFX
 *   Application Thread, so an AnimationTimer runs on that thread, takes
 *   whatever characters are waiting in the buffer, and appends them to the
 *   TextArea once per screen frame.
 *
 * Attribution:
 *   Thread and Runnable structure, the timeToStop / stopRunning() pattern,
 *   Thread.sleep() inside try/catch, and synchronized methods are based on the
 *   Module 8 course examples by Professor Darrell Payne, Bellevue University
 *   (Thread_Examples.zip, Runnable_Interface_Examples.zip, and
 *   Join_Threads_Example.zip).
 *   Oracle Java SE 21 documentation for Thread, Runnable, and
 *   Thread.UncaughtExceptionHandler:
 *     https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Thread.html
 *   OpenJFX documentation for Platform and AnimationTimer (JavaFX controls must
 *   be updated on the JavaFX Application Thread):
 *     https://openjfx.io/javadoc/21/javafx.graphics/javafx/animation/AnimationTimer.html
 *
 * Test code for this class is in RakeshThreeThreadsTest.java.
 */
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Random;

public class RakeshThreeThreads extends Application {

  // Character sets used by the three threads
  public static final String LETTERS = "abcdefghijklmnopqrstuvwxyz";
  public static final String DIGITS = "0123456789";
  public static final String SYMBOLS = "!@#$%^&*";

  // Each thread must display at least 10,000 characters
  public static final int CHARACTERS_PER_THREAD = 10_000;

  // Short pause after each character so the three threads take turns
  // and the user can watch the characters arrive
  private static final int PAUSE_MILLISECONDS = 1;

  private final TextArea textArea = new TextArea();
  private final Label statusLabel = new Label("Starting threads...");
  private final CharacterBuffer sharedBuffer = new CharacterBuffer();

  private CharacterGenerator[] generators;
  private AnimationTimer screenUpdater;

  @Override
  public void start(Stage primaryStage) {

    textArea.setEditable(false);
    textArea.setWrapText(true);
    textArea.setFont(Font.font("Monospaced", 14));

    BorderPane root = new BorderPane();
    root.setPadding(new Insets(10));
    root.setCenter(textArea);
    root.setBottom(statusLabel);
    BorderPane.setMargin(statusLabel, new Insets(8, 0, 0, 0));

    primaryStage.setTitle("Rakesh Shrestha - Module 8.2 Three Threads");
    primaryStage.setScene(new Scene(root, 820, 520));
    primaryStage.show();

    startThreads();
    startScreenUpdater();
  }

  /*
   * Creates one CharacterGenerator for each character set and starts each one
   * in its own thread.
   */
  private void startThreads() {

    generators = new CharacterGenerator[] {
      new CharacterGenerator("Letters", LETTERS, CHARACTERS_PER_THREAD, PAUSE_MILLISECONDS, sharedBuffer),
      new CharacterGenerator("Digits", DIGITS, CHARACTERS_PER_THREAD, PAUSE_MILLISECONDS, sharedBuffer),
      new CharacterGenerator("Symbols", SYMBOLS, CHARACTERS_PER_THREAD, PAUSE_MILLISECONDS, sharedBuffer)
    };

    for (CharacterGenerator generator : generators) {
      Thread thread = new Thread(generator);
      thread.setName(generator.getName() + " Thread");

      // Any exception that is not caught inside run() is written to the console
      thread.setUncaughtExceptionHandler((failedThread, exception) ->
          System.err.println("Unexpected error in " + failedThread.getName() + ": " + exception.getMessage()));

      thread.start();
    }
  }

  /*
   * Runs on the JavaFX Application Thread about 60 times per second.
   * It moves the newest characters from the shared buffer into the TextArea
   * and updates the status line. When all three threads are done and the
   * buffer is empty, the timer stops itself.
   */
  private void startScreenUpdater() {

    screenUpdater = new AnimationTimer() {
      @Override
      public void handle(long now) {

        // Check this before draining so no late characters are missed
        boolean allFinished = allGeneratorsFinished();

        String newCharacters = sharedBuffer.drain();
        if (!newCharacters.isEmpty()) {
          textArea.appendText(newCharacters);
        }

        if (allFinished) {
          statusLabel.setText(buildStatusText() + "\nAll three threads have finished. "
              + String.format("%,d", textArea.getLength()) + " characters displayed.");
          stop();
        }
        else {
          statusLabel.setText(buildStatusText());
        }
      }
    };

    screenUpdater.start();
  }

  private boolean allGeneratorsFinished() {

    for (CharacterGenerator generator : generators) {
      if (!generator.isFinished()) {
        return false;
      }
    }
    return true;
  }

  private String buildStatusText() {

    StringBuilder status = new StringBuilder();
    for (CharacterGenerator generator : generators) {
      status.append(String.format("%s: %,d / %,d     ",
          generator.getName(), generator.getGeneratedCount(), generator.getTotalToGenerate()));
    }
    return status.toString().trim();
  }

  /*
   * Called by JavaFX when the window is closed. Stops the worker threads
   * the graceful way (by setting their stop flag) instead of killing them.
   */
  @Override
  public void stop() {

    if (screenUpdater != null) {
      screenUpdater.stop();
    }

    if (generators != null) {
      for (CharacterGenerator generator : generators) {
        generator.stopRunning();
      }
    }
  }

  public static void main(String[] args) {
    launch(args);
  }

  /*
   * A Runnable that generates random characters from one character set and
   * sends each character to the shared buffer as soon as it is created.
   * The same class is used for letters, digits, and symbols.
   */
  static class CharacterGenerator implements Runnable {

    private final String name;
    private final String characterSet;
    private final int totalToGenerate;
    private final int pauseMilliseconds;
    private final CharacterBuffer output;
    private final Random random = new Random();

    // volatile so the change is seen right away by the other threads
    private volatile boolean timeToStop = false;
    private volatile int generatedCount = 0;

    CharacterGenerator(String name, String characterSet, int totalToGenerate,
                       int pauseMilliseconds, CharacterBuffer output) {

      if (characterSet == null || characterSet.isEmpty()) {
        throw new IllegalArgumentException("The character set cannot be null or empty.");
      }
      if (totalToGenerate <= 0) {
        throw new IllegalArgumentException("The number of characters to generate must be greater than zero.");
      }
      if (pauseMilliseconds < 0) {
        throw new IllegalArgumentException("The pause time cannot be negative.");
      }
      if (output == null) {
        throw new IllegalArgumentException("The output buffer cannot be null.");
      }

      this.name = name;
      this.characterSet = characterSet;
      this.totalToGenerate = totalToGenerate;
      this.pauseMilliseconds = pauseMilliseconds;
      this.output = output;
    }

    /*
     * Returns one random character from this generator's character set.
     */
    char nextRandomCharacter() {
      return characterSet.charAt(random.nextInt(characterSet.length()));
    }

    @Override
    public void run() {

      try {
        while (!timeToStop && generatedCount < totalToGenerate) {

          output.add(nextRandomCharacter());
          generatedCount++;

          if (pauseMilliseconds > 0) {
            Thread.sleep(pauseMilliseconds);
          }
        }
      }
      catch (InterruptedException exception) {
        System.err.println("The " + name + " thread was interrupted after "
            + generatedCount + " characters: " + exception.getMessage());

        // Restore the interrupt flag so the rest of the program can see it
        Thread.currentThread().interrupt();
      }
    }

    void stopRunning() {
      timeToStop = true;
    }

    boolean isFinished() {
      return generatedCount >= totalToGenerate;
    }

    int getGeneratedCount() {
      return generatedCount;
    }

    int getTotalToGenerate() {
      return totalToGenerate;
    }

    String getName() {
      return name;
    }
  }

  /*
   * Shared storage that all three threads write into.
   * Both methods are synchronized, so only one thread can use the buffer at
   * a time. This keeps characters from being lost when two threads add at
   * the same moment.
   */
  static class CharacterBuffer {

    private final StringBuilder pending = new StringBuilder();

    synchronized void add(char character) {
      pending.append(character);
    }

    /*
     * Returns every character added since the last call and empties the buffer.
     */
    synchronized String drain() {
      String characters = pending.toString();
      pending.setLength(0);
      return characters;
    }
  }
}

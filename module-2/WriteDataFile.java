/**
 * Course: CSD-420 - Java 2
 * Module: 2.2 Programming Assignment
 * Author: Rakesh Shrestha
 * Date: September 20, 2026
 *
 * Purpose: This program builds two arrays, one holding five random
 * integers and one holding five random double values, then saves both
 * arrays to a binary data file named "shresthadatafile.dat" using a
 * RandomAccessFile. If the file does not exist yet, Java creates it
 * automatically. If the file already exists, the file pointer is moved
 * to the end before writing, so new data is appended instead of
 * overwriting whatever was already saved there.
 */
import java.io.IOException;
import java.io.RandomAccessFile;

public class WriteDataFile {

  public static void main(String[] args) throws IOException {

    // Named constant instead of a plain "5" so the array size is easy
    // to read and easy to change later if needed.
    final int ARRAY_SIZE = 5;

    int[] intValues = new int[ARRAY_SIZE];
    double[] doubleValues = new double[ARRAY_SIZE];

    // Fill the int array with random whole numbers from 0 to 99
    for (int i = 0; i < ARRAY_SIZE; i++) {
      intValues[i] = (int) (Math.random() * 100);
    }

    // Fill the double array with random decimal values from 0.0 to 100.0
    for (int i = 0; i < ARRAY_SIZE; i++) {
      doubleValues[i] = Math.random() * 100;
    }

    // "rw" opens the file for both reading and writing.
    // RandomAccessFile creates the file on its own if it does not exist.
    RandomAccessFile file = new RandomAccessFile("shresthadatafile.dat", "rw");

    // Move the file pointer to the current end of the file so this
    // run's data is appended after anything already saved, rather
    // than overwriting it.
    file.seek(file.length());

    // Write the five integers first
    for (int i = 0; i < ARRAY_SIZE; i++) {
      file.writeInt(intValues[i]);
    }

    // Write the five doubles second
    for (int i = 0; i < ARRAY_SIZE; i++) {
      file.writeDouble(doubleValues[i]);
    }

    file.close();

    // Print what was written so it can be compared against what the
    // read program prints back later
    System.out.println("Data written to shresthadatafile.dat");

    System.out.print("Random integers: ");
    for (int value : intValues) {
      System.out.print(value + " ");
    }
    System.out.println();

    System.out.print("Random doubles: ");
    for (double value : doubleValues) {
      System.out.printf("%.2f ", value);
    }
    System.out.println();
  }
}

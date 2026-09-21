/**
 * Course: CSD-420 - Java 2
 * Module: 2.2 Programming Assignment
 * Author: Rakesh Shrestha
 * Date: September 20, 2026
 *
 * Purpose: This program opens the binary data file created by
 * WriteDataFile.java and reads its contents back. Every time
 * WriteDataFile runs, it appends one more set of five ints followed
 * by five doubles, so this program reads the file in that same
 * repeating pattern until it reaches the end, printing each set as
 * its own "run" of saved data.
 */
import java.io.IOException;
import java.io.RandomAccessFile;

public class ReadDataFile {

  public static void main(String[] args) throws IOException {

    final int ARRAY_SIZE = 5;

    RandomAccessFile file = new RandomAccessFile("shresthadatafile.dat", "r");

    // Start reading from the very beginning of the file
    file.seek(0);

    int runNumber = 1;

    // Keep reading one set of five ints and five doubles at a time
    // until the file pointer reaches the end of the file
    while (file.getFilePointer() < file.length()) {

      System.out.println("Run " + runNumber + ":");

      System.out.print("  Integers: ");
      for (int i = 0; i < ARRAY_SIZE; i++) {
        System.out.print(file.readInt() + " ");
      }
      System.out.println();

      System.out.print("  Doubles: ");
      for (int i = 0; i < ARRAY_SIZE; i++) {
        System.out.printf("%.2f ", file.readDouble());
      }
      System.out.println();

      runNumber++;
    }

    file.close();
  }
}

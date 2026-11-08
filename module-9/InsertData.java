/*
 * Rakesh Shrestha
 * CSD420-T301 Advanced Java Programming
 * Module 9.2 Programming Assignment
 * Date: 11/07/2026
 *
 * Purpose: Inserts sample address records into the address table in
 * "databasedb". Run CreateTable first so the table exists.
 *
 * The original example repeated one executeUpdate line per record. Here
 * the records are kept in an array and a single PreparedStatement is
 * reused in a loop, which keeps the code shorter and avoids building SQL
 * strings by hand.
 *
 * Attribution: Adapted from InsertData.java by Professor Darrell Payne,
 * Bellevue University.
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InsertData {

    private static final String INSERT_SQL =
            "INSERT INTO " + DatabaseConfig.TABLE
            + " VALUES (?, ?, ?, ?, ?, ?, ?)";

    // ID, last name, first name, street, city, state, ZIP
    private static final String[][] ADDRESSES = {
        {"1", "Fine",    "Ruth",    "1111 Redwing Circle",    "Bellevue", "NE", "68123"},
        {"2", "Howard",  "Curly",   "1000 Galvin Road South", "Bellevue", "NE", "68005"},
        {"3", "Howard",  "Will",    "2919 Redwing Circle",    "Bellevue", "NE", "68123"},
        {"4", "Wilson",  "Larry",   "1121 Redwing Circle",    "Bellevue", "NE", "68124"},
        {"5", "Johnson", "George",  "1300 Galvin Road South", "Bellevue", "NE", "68006"},
        {"6", "Long",    "Matthew", "2419 Redwing Circle",    "Bellevue", "NE", "68127"}
    };

    public static void main(String[] args) {

        int inserted = 0;

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement pstmt = con.prepareStatement(INSERT_SQL)) {

            System.out.println("Connected to databasedb as "
                    + DatabaseConfig.USER + ".");

            for (String[] row : ADDRESSES) {
                inserted += insertRow(pstmt, row);
            }
        }
        catch (ClassNotFoundException | SQLException e) {
            DatabaseConfig.logError("Connecting to databasedb", e);
            return;
        }

        System.out.println(inserted + " of " + ADDRESSES.length
                + " rows inserted.");
        System.out.println("Database connection closed.");
    }

    /**
     * Inserts one address record. A failure on one row (for example a
     * duplicate ID from running the program twice) is reported, and the
     * remaining rows are still attempted.
     *
     * @param pstmt the prepared INSERT statement
     * @param row   the seven column values for one record
     * @return the number of rows inserted (1 on success, 0 on failure)
     */
    private static int insertRow(PreparedStatement pstmt, String[] row) {

        try {
            pstmt.setInt(1, Integer.parseInt(row[0]));

            for (int col = 1; col < row.length; col++) {
                pstmt.setString(col + 1, row[col]);
            }

            int count = pstmt.executeUpdate();
            System.out.println("ID " + row[0] + " (" + row[2] + " "
                    + row[1] + "): " + count + " row inserted.");
            return count;
        }
        catch (SQLException e) {
            DatabaseConfig.logError("Inserting ID " + row[0], e);
            return 0;
        }
    }
}

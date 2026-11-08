/*
 * Rakesh Shrestha
 * CSD420-T301 Advanced Java Programming
 * Module 9.2 Programming Assignment
 * Date: 11/07/2026
 *
 * Purpose: Holds the connection settings for the "databasedb" MySQL
 * database in one place so CreateTable, InsertData, and Select5 do not
 * each repeat the URL, user name, and password.
 *
 * Attribution: Adapted from the Module 9 example code written by
 * Professor Darrell Payne, Bellevue University.
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConfig {

    /** MySQL Connector/J driver class (Connector/J 8.x or newer). */
    public static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    /**
     * JDBC URL for the local MySQL 5.7 server.
     * useSSL=false avoids SSL warnings on a local install, and
     * serverTimezone=UTC prevents the "server time zone value" error
     * that Connector/J 8 can throw against MySQL 5.7.
     */
    public static final String URL =
            "jdbc:mysql://localhost:3306/databasedb"
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    public static final String USER = "student1";
    public static final String PASSWORD = "pass";

    /** Name of the table used by all three test programs. */
    public static final String TABLE = "address";

    // Utility class, so no objects should be created from it.
    private DatabaseConfig() {
    }

    /**
     * Loads the JDBC driver and opens a connection to databasedb.
     *
     * @return an open Connection that the caller is responsible for closing
     * @throws ClassNotFoundException if the Connector/J jar is not on the classpath
     * @throws SQLException if MySQL refuses the connection
     */
    public static Connection getConnection()
            throws ClassNotFoundException, SQLException {

        Class.forName(DRIVER);
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Prints a readable description of a database error to the console.
     * Used by every program so error output looks the same everywhere.
     *
     * @param action short description of what the program was trying to do
     * @param e      the exception that was thrown
     */
    public static void logError(String action, Exception e) {

        System.err.println("ERROR: " + action + " failed.");

        if (e instanceof ClassNotFoundException) {
            System.err.println("  The MySQL JDBC driver was not found. "
                    + "Add the Connector/J jar to the project libraries.");
        }
        else if (e instanceof SQLException) {
            SQLException sqlEx = (SQLException) e;
            System.err.println("  SQL state:  " + sqlEx.getSQLState());
            System.err.println("  Error code: " + sqlEx.getErrorCode());
        }

        System.err.println("  Message:    " + e.getMessage());
    }
}

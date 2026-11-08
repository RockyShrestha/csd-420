/*
 * Rakesh Shrestha
 * CSD420-T301 Advanced Java Programming
 * Module 9.2 Programming Assignment
 * Date: 11/07/2026
 *
 * Purpose: Connects to the "databasedb" database as student1, drops the
 * address table if it already exists, and then creates it again. This is
 * the first program to run when testing the MySQL and JDBC setup.
 *
 * Attribution: Adapted from CreateTable.java by Professor Darrell Payne,
 * Bellevue University.
 */
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateTable {

    private static final String DROP_SQL =
            "DROP TABLE IF EXISTS " + DatabaseConfig.TABLE;

    private static final String CREATE_SQL =
            "CREATE TABLE " + DatabaseConfig.TABLE + " ("
            + "ID int PRIMARY KEY, "
            + "LASTNAME varchar(40), "
            + "FIRSTNAME varchar(40), "
            + "STREET varchar(40), "
            + "CITY varchar(40), "
            + "STATE varchar(40), "
            + "ZIP varchar(40))";

    public static void main(String[] args) {

        // try-with-resources closes the Statement and Connection for us,
        // even if one of the SQL commands fails.
        try (Connection con = DatabaseConfig.getConnection();
             Statement stmt = con.createStatement()) {

            System.out.println("Connected to databasedb as "
                    + DatabaseConfig.USER + ".");

            stmt.executeUpdate(DROP_SQL);
            System.out.println("Table " + DatabaseConfig.TABLE
                    + " dropped (if it existed).");

            stmt.executeUpdate(CREATE_SQL);
            System.out.println("Table " + DatabaseConfig.TABLE + " created.");
        }
        catch (ClassNotFoundException | SQLException e) {
            DatabaseConfig.logError("Creating the " + DatabaseConfig.TABLE
                    + " table", e);
            return;
        }

        System.out.println("Database connection closed.");
    }
}

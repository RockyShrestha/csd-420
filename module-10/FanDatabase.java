/*
 * Rakesh Shrestha
 * CSD420-T301 Advanced Java Programming
 * Module 10.2 Programming Assignment
 * November 15, 2026
 *
 * Purpose: FanDatabase does all of the JDBC work for the "fans" table in
 * the "databasedb" database. It can look up one fan by ID and save changes
 * to an existing fan. It never creates or drops the table, because the
 * assignment says the table will already be there.
 *
 * References:
 * Payne, D. SelectData.java and InsertData.java, from the
 * Swing_GUI_MySQL_Examples and Another_JDBC_App_Examples course files,
 * Bellevue University. The driver name, connection details, and the idea
 * of one class that owns the connection come from these examples.
 * Liang, Y. D. Introduction to Java Programming and Data Structures
 * (13th ed.), Chapter 34. Pearson. Used for PreparedStatement.
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FanDatabase {

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/databasedb";
    private static final String USER = "student1";
    private static final String PASSWORD = "pass";

    private static final String SELECT_FAN =
            "SELECT ID, firstname, lastname, favoriteteam FROM fans WHERE ID = ?";

    private static final String UPDATE_FAN =
            "UPDATE fans SET firstname = ?, lastname = ?, favoriteteam = ? WHERE ID = ?";

    private Connection connection;

    /**
     * Loads the MySQL driver and opens the connection to databasedb.
     *
     * @throws SQLException if the driver is missing or the login fails
     */
    public FanDatabase() throws SQLException {
        try {
            Class.forName(DRIVER);
        }
        catch (ClassNotFoundException e) {
            throw new SQLException("The MySQL JDBC driver was not found on the classpath.", e);
        }

        connection = DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Looks up one fan by ID.
     *
     * @param id the ID to search for
     * @return the matching fan, or null when no row has that ID
     * @throws SQLException if the query cannot be run
     */
    public Fan getFan(int id) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_FAN)) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }

                return new Fan(resultSet.getInt("ID"),
                               resultSet.getString("firstname"),
                               resultSet.getString("lastname"),
                               resultSet.getString("favoriteteam"));
            }
        }
    }

    /**
     * Saves the first name, last name, and favorite team of an existing fan.
     * The ID is only used to find the row, so it is never changed.
     *
     * @param fan the fan with the new values
     * @return true when a row was updated, false when the ID does not exist
     * @throws SQLException if the update cannot be run
     */
    public boolean updateFan(Fan fan) throws SQLException {
        // Checking first gives a clear answer about a missing ID no matter
        // how the driver counts rows that did not really change.
        if (getFan(fan.getId()) == null) {
            return false;
        }

        try (PreparedStatement statement = connection.prepareStatement(UPDATE_FAN)) {
            statement.setString(1, fan.getFirstName());
            statement.setString(2, fan.getLastName());
            statement.setString(3, fan.getFavoriteTeam());
            statement.setInt(4, fan.getId());
            statement.executeUpdate();
        }

        return true;
    }

    /**
     * Reports whether the connection is still open.
     */
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        }
        catch (SQLException e) {
            System.err.println("The connection status could not be checked: " + e.getMessage());
            return false;
        }
    }

    /**
     * Closes the connection. It is safe to call this more than once.
     */
    public void close() {
        try {
            if (isConnected()) {
                connection.close();
            }
        }
        catch (SQLException e) {
            System.err.println("The database connection did not close cleanly: " + e.getMessage());
        }
    }
}

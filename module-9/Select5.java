/*
 * Rakesh Shrestha
 * CSD420-T301 Advanced Java Programming
 * Module 9.2 Programming Assignment
 * Date: 11/07/2026
 *
 * Purpose: Selects every record from the address table in "databasedb"
 * and prints the results as a table. The column headings come from the
 * ResultSet metadata and each column is sized to its longest value, so
 * the program still works if columns are added or changed.
 * Run CreateTable and InsertData first.
 *
 * Attribution: Adapted from Select5.java by Professor Darrell Payne,
 * Bellevue University.
 */
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Select5 {

    private static final String SELECT_SQL =
            "SELECT * FROM " + DatabaseConfig.TABLE + " ORDER BY ID";

    // Spaces printed between columns.
    private static final String COLUMN_GAP = "  ";

    public static void main(String[] args) {

        try (Connection con = DatabaseConfig.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_SQL)) {

            System.out.println("Connection established - now executing a select.");
            System.out.println("Received results:");
            System.out.println();

            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            // Read the column names and every record first, so each column
            // can be sized to fit its longest value.
            List<String[]> rows = new ArrayList<>();
            String[] headings = new String[columnCount];
            for (int col = 1; col <= columnCount; col++) {
                headings[col - 1] = meta.getColumnName(col);
            }

            while (rs.next()) {
                String[] record = new String[columnCount];
                for (int col = 1; col <= columnCount; col++) {
                    String value = rs.getString(col);
                    record[col - 1] = (value == null) ? "NULL" : value;
                }
                rows.add(record);
            }

            int[] widths = columnWidths(headings, rows);

            String headerLine = formatRow(headings, widths);
            System.out.println(headerLine);
            System.out.println("-".repeat(headerLine.length()));

            for (String[] record : rows) {
                System.out.println(formatRow(record, widths));
            }

            System.out.println();
            System.out.println(rows.size() + " row(s) returned.");
        }
        catch (ClassNotFoundException | SQLException e) {
            DatabaseConfig.logError("Selecting from the "
                    + DatabaseConfig.TABLE + " table", e);
        }
    }

    /**
     * Finds the widest value in each column, counting the heading too.
     *
     * @param headings the column names
     * @param rows     every record returned by the query
     * @return the width of each column
     */
    private static int[] columnWidths(String[] headings, List<String[]> rows) {

        int[] widths = new int[headings.length];

        for (int col = 0; col < headings.length; col++) {
            widths[col] = headings[col].length();
            for (String[] record : rows) {
                widths[col] = Math.max(widths[col], record[col].length());
            }
        }

        return widths;
    }

    /**
     * Joins one row of values into a single line, padding each value to its
     * column width and separating columns with a gap.
     *
     * @param values the values for one row
     * @param widths the width of each column
     * @return the formatted line
     */
    private static String formatRow(String[] values, int[] widths) {

        StringBuilder line = new StringBuilder();

        for (int col = 0; col < values.length; col++) {
            line.append(String.format("%-" + widths[col] + "s", values[col]));
            if (col < values.length - 1) {
                line.append(COLUMN_GAP);
            }
        }

        return line.toString();
    }
}

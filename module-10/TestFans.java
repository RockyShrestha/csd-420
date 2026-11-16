/*
 * Rakesh Shrestha
 * CSD420-T301 Advanced Java Programming
 * Module 10.2 Programming Assignment
 * November 15, 2026
 *
 * Purpose: TestFans checks every method in Fan and FanDatabase, and then
 * drives the FanViewer window by filling in the fields and clicking the
 * Display and Update buttons from code. Each check prints PASS or FAIL and
 * a total is printed at the end.
 *
 * The tests use a record that is already in the fans table. Any row that
 * gets changed is put back the way it was, and the table itself is never
 * created or deleted.
 *
 * Run it the same way as FanViewer, with the MySQL Connector/J jar on the
 * classpath:
 *   java -cp ".;mysql-connector-j-x.x.x.jar" TestFans
 */
import java.sql.SQLException;
import javax.swing.SwingUtilities;

public class TestFans {

    // The highest ID the tests will try while looking for an existing record.
    private static final int LAST_ID_TO_SEARCH = 500;

    private static final String TEST_FIRST_NAME = "TestFirst";
    private static final String TEST_LAST_NAME = "TestLast";
    private static final String TEST_TEAM = "TestTeam";

    private static int passed = 0;
    private static int failed = 0;

    /**
     * A block of test code that is allowed to throw, so the window tests
     * can call the database without a try and catch around every line.
     */
    private interface TestSteps {
        void run() throws Exception;
    }

    public static void main(String[] args) {
        testFanClass();

        FanDatabase database = openDatabase();

        if (database != null) {
            runDatabaseAndWindowTests(database);
            testClose(database);
        }

        System.out.println();
        System.out.println("Tests passed: " + passed);
        System.out.println("Tests failed: " + failed);

        // The Swing thread is still alive after the window tests, so end the program here.
        System.exit(failed == 0 ? 0 : 1);
    }

    /**
     * Records one check and prints the result.
     */
    private static void check(String description, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("PASS: " + description);
        }
        else {
            failed++;
            System.out.println("FAIL: " + description);
        }
    }

    /**
     * Tests the constructor, getters, setters, and toString() in Fan.
     */
    private static void testFanClass() {
        System.out.println("Fan class tests");

        Fan fan = new Fan(7, "Maya", "Lopez", "Storm Chasers");

        check("Constructor stores the ID", fan.getId() == 7);
        check("Constructor stores the first name", fan.getFirstName().equals("Maya"));
        check("Constructor stores the last name", fan.getLastName().equals("Lopez"));
        check("Constructor stores the favorite team", fan.getFavoriteTeam().equals("Storm Chasers"));
        check("toString() lists every field", fan.toString().equals("7, Maya, Lopez, Storm Chasers"));

        fan.setId(8);
        fan.setFirstName("Owen");
        fan.setLastName("Reed");
        fan.setFavoriteTeam("Mavericks");

        check("setId() changes the ID", fan.getId() == 8);
        check("setFirstName() changes the first name", fan.getFirstName().equals("Owen"));
        check("setLastName() changes the last name", fan.getLastName().equals("Reed"));
        check("setFavoriteTeam() changes the favorite team", fan.getFavoriteTeam().equals("Mavericks"));
    }

    /**
     * Tests the FanDatabase constructor by opening the connection.
     *
     * @return the open database, or null when the connection failed
     */
    private static FanDatabase openDatabase() {
        System.out.println();
        System.out.println("FanDatabase tests");

        try {
            FanDatabase database = new FanDatabase();

            check("Constructor connects to databasedb", database.isConnected());
            return database;
        }
        catch (SQLException e) {
            check("Constructor connects to databasedb", false);
            System.err.println("The remaining tests were skipped because the connection failed: "
                    + e.getMessage());
            return null;
        }
    }

    /**
     * Finds a record to test with, runs the database and window tests, and
     * then puts that record back the way it was found.
     */
    private static void runDatabaseAndWindowTests(FanDatabase database) {
        Fan original = null;

        try {
            original = findExistingFan(database);

            if (original == null) {
                check("The fans table has a record to test with", false);
                System.err.println("No fan was found with an ID from 1 to " + LAST_ID_TO_SEARCH
                        + ", so the remaining tests were skipped.");
                return;
            }

            int missingId = findMissingId(database);

            System.out.println("Testing with this record: " + original);

            testDatabaseMethods(database, original, missingId);
            testWindow(database, original, missingId);
        }
        catch (SQLException e) {
            check("Database tests ran without an SQL error", false);
            System.err.println("A database test stopped early: " + e.getMessage());
        }
        finally {
            restore(database, original);
        }
    }

    /**
     * Returns the first fan found by trying the IDs from 1 upward.
     */
    private static Fan findExistingFan(FanDatabase database) throws SQLException {
        for (int id = 1; id <= LAST_ID_TO_SEARCH; id++) {
            Fan fan = database.getFan(id);

            if (fan != null) {
                return fan;
            }
        }

        return null;
    }

    /**
     * Returns an ID that is not in the table, starting from -1 and counting down.
     */
    private static int findMissingId(FanDatabase database) throws SQLException {
        int id = -1;

        while (database.getFan(id) != null) {
            id--;
        }

        return id;
    }

    /**
     * Tests getFan() and updateFan() directly.
     */
    private static void testDatabaseMethods(FanDatabase database, Fan original, int missingId)
            throws SQLException {
        int id = original.getId();

        check("getFan() returns the record with the requested ID", database.getFan(id).getId() == id);
        check("getFan() returns null for an ID that does not exist", database.getFan(missingId) == null);

        Fan changed = new Fan(id, TEST_FIRST_NAME, TEST_LAST_NAME, TEST_TEAM);

        check("updateFan() returns true for an existing ID", database.updateFan(changed));
        check("updateFan() saves the new values", sameValues(database.getFan(id), changed));

        check("updateFan() can put the original values back", database.updateFan(original));
        check("The record matches the original again", sameValues(database.getFan(id), original));

        Fan missing = new Fan(missingId, TEST_FIRST_NAME, TEST_LAST_NAME, TEST_TEAM);

        check("updateFan() returns false for an ID that does not exist", !database.updateFan(missing));
        check("updateFan() does not add a new row", database.getFan(missingId) == null);
    }

    /**
     * Tests the window by setting the fields and clicking the buttons.
     * Swing components are only supposed to be touched on the event
     * thread, so the steps are handed to invokeAndWait().
     */
    private static void testWindow(FanDatabase database, Fan original, int missingId) {
        System.out.println();
        System.out.println("FanViewer window tests");

        runOnEventThread(() -> {
            FanViewer viewer = new FanViewer(database);
            int id = original.getId();

            viewer.setVisible(true);

            try {
                check("Window opens with both buttons enabled",
                        viewer.getDisplayButton().isEnabled() && viewer.getUpdateButton().isEnabled());

                // Display button
                clickDisplay(viewer, "");
                check("Display asks for an ID when the ID field is blank",
                        viewer.getStatusText().equals("Please enter a fan ID."));

                clickDisplay(viewer, "abc");
                check("Display rejects an ID that is not a number",
                        viewer.getStatusText().equals("The fan ID must be a whole number."));

                clickDisplay(viewer, String.valueOf(missingId));
                check("Display reports an ID that does not exist",
                        viewer.getStatusText().startsWith("No fan was found"));
                check("Display clears the fields when nothing is found",
                        viewer.getFirstNameField().getText().isEmpty()
                        && viewer.getLastNameField().getText().isEmpty()
                        && viewer.getFavoriteTeamField().getText().isEmpty());

                clickDisplay(viewer, String.valueOf(id));
                check("Display shows the first name", viewer.getFirstNameField().getText().equals(original.getFirstName()));
                check("Display shows the last name", viewer.getLastNameField().getText().equals(original.getLastName()));
                check("Display shows the favorite team", viewer.getFavoriteTeamField().getText().equals(original.getFavoriteTeam()));

                // Update button
                clickUpdate(viewer, new Fan(id, "", TEST_LAST_NAME, TEST_TEAM));
                check("Update rejects a blank first name",
                        viewer.getStatusText().equals("First name cannot be blank."));

                clickUpdate(viewer, new Fan(id, TEST_FIRST_NAME, TEST_LAST_NAME, "A team name that is too long"));
                check("Update rejects text longer than 25 characters",
                        viewer.getStatusText().startsWith("Favorite team cannot be longer"));
                check("Rejected updates do not change the record", sameValues(database.getFan(id), original));

                Fan changed = new Fan(id, TEST_FIRST_NAME, TEST_LAST_NAME, TEST_TEAM);

                clickUpdate(viewer, changed);
                check("Update reports success", viewer.getStatusText().equals("Fan ID " + id + " was updated."));
                check("Update saves the changes to the database", sameValues(database.getFan(id), changed));

                clickDisplay(viewer, String.valueOf(id));
                check("Display shows the updated record",
                        viewer.getFirstNameField().getText().equals(TEST_FIRST_NAME));

                clickUpdate(viewer, new Fan(missingId, TEST_FIRST_NAME, TEST_LAST_NAME, TEST_TEAM));
                check("Update reports an ID that does not exist",
                        viewer.getStatusText().endsWith("so nothing was updated."));

                clickUpdate(viewer, original);
                check("Update puts the original record back", sameValues(database.getFan(id), original));
            }
            finally {
                viewer.dispose();
            }
        });
    }

    /**
     * Types an ID into the window and clicks Display.
     */
    private static void clickDisplay(FanViewer viewer, String idText) {
        viewer.getIdField().setText(idText);
        viewer.getDisplayButton().doClick();
    }

    /**
     * Fills in every field from the given fan and clicks Update.
     */
    private static void clickUpdate(FanViewer viewer, Fan fan) {
        viewer.getIdField().setText(String.valueOf(fan.getId()));
        viewer.getFirstNameField().setText(fan.getFirstName());
        viewer.getLastNameField().setText(fan.getLastName());
        viewer.getFavoriteTeamField().setText(fan.getFavoriteTeam());
        viewer.getUpdateButton().doClick();
    }

    /**
     * Runs the steps on the Swing event thread and waits for them to finish.
     */
    private static void runOnEventThread(TestSteps steps) {
        try {
            SwingUtilities.invokeAndWait(() -> {
                try {
                    steps.run();
                }
                catch (Exception e) {
                    check("Window tests ran without an error", false);
                    System.err.println("A window test stopped early: " + e.getMessage());
                }
            });
        }
        catch (Exception e) {
            check("Window tests could be started", false);
            System.err.println("The window tests could not be run: " + e.getMessage());
        }
    }

    /**
     * Compares the three text values of two fans.
     */
    private static boolean sameValues(Fan first, Fan second) {
        return first != null
                && second != null
                && first.getFirstName().equals(second.getFirstName())
                && first.getLastName().equals(second.getLastName())
                && first.getFavoriteTeam().equals(second.getFavoriteTeam());
    }

    /**
     * Writes the original record back in case a test stopped part way through.
     */
    private static void restore(FanDatabase database, Fan original) {
        if (original == null) {
            return;
        }

        try {
            database.updateFan(original);
        }
        catch (SQLException e) {
            System.err.println("The test record could not be restored: " + e.getMessage());
        }
    }

    /**
     * Tests close() and isConnected() once everything else is done.
     */
    private static void testClose(FanDatabase database) {
        System.out.println();
        System.out.println("Closing tests");

        database.close();
        check("close() closes the connection", !database.isConnected());

        database.close();
        check("close() can be called a second time without an error", !database.isConnected());
    }
}

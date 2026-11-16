/*
 * Rakesh Shrestha
 * CSD420-T301 Advanced Java Programming
 * Module 10.2 Programming Assignment
 * November 15, 2026
 *
 * Purpose: FanViewer is the window for this assignment. The user types a
 * fan ID and clicks Display to load that record from the "fans" table.
 * After changing the first name, last name, or favorite team, clicking
 * Update saves the changes back to the database.
 *
 * This is the class to run. The MySQL Connector/J jar has to be on the
 * classpath, for example:
 *   javac *.java
 *   java -cp ".;mysql-connector-j-x.x.x.jar" FanViewer
 *
 * References:
 * Payne, D. Null_Layout_8.java, from the Swing_GUI_MySQL_Examples course
 * files, Bellevue University. The idea of a Swing window that sits on top
 * of a separate database class comes from this example.
 * Liang, Y. D. Introduction to Java Programming and Data Structures
 * (13th ed.), Chapters 34 and 35. Pearson.
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class FanViewer extends JFrame {

    // The three text columns in the fans table are all varchar(25).
    private static final int MAX_TEXT_LENGTH = 25;

    private static final Color ERROR_COLOR = new Color(170, 0, 0);
    private static final Color SUCCESS_COLOR = new Color(0, 110, 0);

    private final FanDatabase database;

    private final JTextField txtId = new JTextField(18);
    private final JTextField txtFirstName = new JTextField(18);
    private final JTextField txtLastName = new JTextField(18);
    private final JTextField txtFavoriteTeam = new JTextField(18);

    private final JButton btnDisplay = new JButton("Display");
    private final JButton btnUpdate = new JButton("Update");

    private final JLabel lblStatus = new JLabel("Enter a fan ID and click Display.");

    /**
     * Builds the window around an open database connection.
     *
     * @param database the database object used for every lookup and update
     */
    public FanViewer(FanDatabase database) {
        super("Fan Records");

        this.database = database;

        buildLayout();

        btnDisplay.addActionListener(event -> displayFan());
        btnUpdate.addActionListener(event -> updateFan());

        // Closing the window goes through shutDown() so the connection is closed first.
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                shutDown();
            }
        });

        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Puts the labels, text fields, buttons, and status line in the window.
     */
    private void buildLayout() {
        JPanel fieldPanel = new JPanel(new GridLayout(4, 2, 8, 8));
        fieldPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        addRow(fieldPanel, "ID", txtId);
        addRow(fieldPanel, "First Name", txtFirstName);
        addRow(fieldPanel, "Last Name", txtLastName);
        addRow(fieldPanel, "Favorite Team", txtFavoriteTeam);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnDisplay);
        buttonPanel.add(btnUpdate);

        lblStatus.setBorder(BorderFactory.createEmptyBorder(5, 15, 12, 15));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.NORTH);
        bottomPanel.add(lblStatus, BorderLayout.SOUTH);

        getContentPane().add(fieldPanel, BorderLayout.CENTER);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Adds one label and its text field as a row of the field panel.
     */
    private void addRow(JPanel panel, String labelText, JTextField field) {
        panel.add(new JLabel(labelText));
        panel.add(field);
    }

    /**
     * Runs when Display is clicked. Loads the fan whose ID is in the ID field.
     */
    private void displayFan() {
        Integer id = readId();

        if (id == null) {
            return;
        }

        try {
            Fan fan = database.getFan(id);

            if (fan == null) {
                clearNameFields();
                showError("No fan was found with ID " + id + ".");
                return;
            }

            txtFirstName.setText(fan.getFirstName());
            txtLastName.setText(fan.getLastName());
            txtFavoriteTeam.setText(fan.getFavoriteTeam());
            showSuccess("Showing fan ID " + id + ".");
        }
        catch (SQLException e) {
            reportDatabaseError("The fan record could not be read from the database.", e);
        }
    }

    /**
     * Runs when Update is clicked. Saves the values in the window to the
     * record whose ID is in the ID field.
     */
    private void updateFan() {
        Integer id = readId();

        if (id == null) {
            return;
        }

        String firstName = readText(txtFirstName, "First name");
        String lastName = readText(txtLastName, "Last name");
        String favoriteTeam = readText(txtFavoriteTeam, "Favorite team");

        if (firstName == null || lastName == null || favoriteTeam == null) {
            return;
        }

        try {
            Fan fan = new Fan(id, firstName, lastName, favoriteTeam);

            if (database.updateFan(fan)) {
                showSuccess("Fan ID " + id + " was updated.");
            }
            else {
                showError("No fan was found with ID " + id + ", so nothing was updated.");
            }
        }
        catch (SQLException e) {
            reportDatabaseError("The fan record could not be updated in the database.", e);
        }
    }

    /**
     * Reads the ID field as a whole number.
     *
     * @return the ID, or null after showing a message when the entry is not valid
     */
    private Integer readId() {
        String text = txtId.getText().trim();

        if (text.isEmpty()) {
            showError("Please enter a fan ID.");
            return null;
        }

        try {
            return Integer.valueOf(text);
        }
        catch (NumberFormatException e) {
            showError("The fan ID must be a whole number.");
            return null;
        }
    }

    /**
     * Reads one of the text fields and checks it against the column size.
     *
     * @param field     the text field to read
     * @param fieldName the name to use in a message, such as "First name"
     * @return the trimmed text, or null after showing a message when it is not valid
     */
    private String readText(JTextField field, String fieldName) {
        String text = field.getText().trim();

        if (text.isEmpty()) {
            showError(fieldName + " cannot be blank.");
            return null;
        }

        if (text.length() > MAX_TEXT_LENGTH) {
            showError(fieldName + " cannot be longer than " + MAX_TEXT_LENGTH + " characters.");
            return null;
        }

        return text;
    }

    private void clearNameFields() {
        txtFirstName.setText("");
        txtLastName.setText("");
        txtFavoriteTeam.setText("");
    }

    private void showSuccess(String message) {
        showStatus(message, SUCCESS_COLOR);
    }

    private void showError(String message) {
        showStatus(message, ERROR_COLOR);
    }

    private void showStatus(String message, Color color) {
        lblStatus.setForeground(color);
        lblStatus.setText(message);
    }

    /**
     * Shows a short message in the window and writes the details to the console.
     */
    private void reportDatabaseError(String message, SQLException e) {
        showError(message);
        System.err.println(message + " Details: " + e.getMessage());
    }

    /**
     * Closes the database connection and ends the program.
     */
    private void shutDown() {
        database.close();
        dispose();
        System.exit(0);
    }

    /*
     * The methods below give TestFans a way to fill in the fields, click
     * the buttons, and read the result without a person at the keyboard.
     */

    JTextField getIdField() {
        return txtId;
    }

    JTextField getFirstNameField() {
        return txtFirstName;
    }

    JTextField getLastNameField() {
        return txtLastName;
    }

    JTextField getFavoriteTeamField() {
        return txtFavoriteTeam;
    }

    JButton getDisplayButton() {
        return btnDisplay;
    }

    JButton getUpdateButton() {
        return btnUpdate;
    }

    String getStatusText() {
        return lblStatus.getText();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                FanViewer viewer = new FanViewer(new FanDatabase());
                viewer.setVisible(true);
            }
            catch (SQLException e) {
                String message = "The program could not connect to databasedb.";

                System.err.println(message + " Details: " + e.getMessage());
                JOptionPane.showMessageDialog(null, message + "\n" + e.getMessage(),
                        "Connection Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}

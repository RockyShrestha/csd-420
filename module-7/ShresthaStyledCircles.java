/*
 * Name:        Rakesh Shrestha
 * Course:      CSD420-T301 Advanced Java Programming
 * Instructor:  Professor Tedi Pano
 * Assignment:  Module 7.2 Programming Assignment
 * Date:        October 25, 2026
 *
 * Description:
 *   This program solves Programming Exercise 31.1 from the Liang textbook.
 *   It displays four circles that get their colors from an external style
 *   sheet (mystyle.css) instead of from Java code.
 *     1. A white circle with a black stroke inside a pane that has a thick
 *        black border (style classes "plaincircle" and "border").
 *     2. A white circle with a black stroke (style class "plaincircle").
 *     3. A red circle (style class "plaincircle" and style ID "redcircle").
 *     4. A green circle (style class "plaincircle" and style ID
 *        "greencircle").
 *   Circles 3 and 4 have both a class and an ID to show that the ID
 *   overrides the class, since an ID selector is more specific.
 *
 *   mystyle.css must be in the same folder as this class. If it cannot be
 *   found, an error is printed to the console and the circles are shown
 *   with JavaFX's default colors.
 *
 *   ShresthaStyledCirclesTest checks that the styles are applied correctly.
 *
 * Attribution:
 *   The layout and style sheet loading are based on StyleSheetDemo.java and
 *   mystyle.css, the Module 7 examples by Professor Darrell Payne (Bellevue
 *   University), and on Liang, Y. D. (2024). Introduction to Java
 *   Programming and Data Structures (13th ed.), Chapter 31, JavaFX CSS.
 *   Pearson.
 */

import java.io.File;
import java.net.URL;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class ShresthaStyledCircles extends Application {

    // Style sheet file name and the names used inside it
    public static final String STYLE_SHEET = "mystyle.css";
    public static final String PLAIN_CLASS = "plaincircle";
    public static final String BORDER_CLASS = "border";
    public static final String RED_ID = "redcircle";
    public static final String GREEN_ID = "greencircle";

    // Size settings for the window and circles
    public static final double RADIUS = 30;
    private static final double SPACING = 5;
    private static final double WIDTH = 300;
    private static final double HEIGHT = 250;

    @Override
    public void start(Stage primaryStage) {

        try {
            primaryStage.setTitle("Exercise31_01");
            primaryStage.setScene(createScene());
            primaryStage.show();
        }
        catch (Exception e) {
            System.out.println("Error: The window could not be created. "
                    + e.getMessage());
        }
    }

    /**
     * Builds the scene with the four circles and loads mystyle.css.
     * The test class calls this method too, so the program and the tests
     * always use the same layout.
     *
     * @return the finished scene
     */
    public static Scene createScene() {

        HBox hBox = new HBox(SPACING);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(SPACING));

        // Circle 1 sits in its own pane so the pane can show the border
        StackPane borderPane = new StackPane(createCircle(null));
        borderPane.getStyleClass().add(BORDER_CLASS);

        hBox.getChildren().addAll(
                borderPane,
                createCircle(null),
                createCircle(RED_ID),
                createCircle(GREEN_ID));

        Scene scene = new Scene(hBox, WIDTH, HEIGHT);
        loadStyleSheet(scene);

        return scene;
    }

    /**
     * Creates one circle with the "plaincircle" style class. If an ID is
     * given, it is set too, and its colors replace the class colors.
     *
     * @param styleId the style ID to set, or null for a plain circle
     * @return the new circle
     */
    public static Circle createCircle(String styleId) {

        Circle circle = new Circle(RADIUS);
        circle.getStyleClass().add(PLAIN_CLASS);

        if (styleId != null) {
            circle.setId(styleId);
        }

        return circle;
    }

    /**
     * Adds mystyle.css to the scene. It first looks next to the compiled
     * class (how IntelliJ runs it), then in the current folder (how the
     * command line runs it).
     *
     * @param scene the scene to style
     * @return true if the style sheet was found and added
     */
    public static boolean loadStyleSheet(Scene scene) {

        try {
            URL url = ShresthaStyledCircles.class.getResource(STYLE_SHEET);

            if (url == null) {
                File file = new File(STYLE_SHEET);

                if (file.exists()) {
                    url = file.toURI().toURL();
                }
            }

            if (url == null) {
                System.out.println("Error: Could not find " + STYLE_SHEET
                        + ". The circles will use default colors.");
                return false;
            }

            scene.getStylesheets().add(url.toExternalForm());
            return true;
        }
        catch (Exception e) {
            System.out.println("Error: Could not load " + STYLE_SHEET + ". "
                    + e.getMessage());
            return false;
        }
    }

    /**
     * Launches the program from the command line
     */
    public static void main(String[] args) {

        launch(args);
    }
}

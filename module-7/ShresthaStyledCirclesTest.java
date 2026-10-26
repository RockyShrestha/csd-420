/*
 * Name:        Rakesh Shrestha
 * Course:      CSD420-T301 Advanced Java Programming
 * Instructor:  Professor Tedi Pano
 * Assignment:  Module 7.2 Programming Assignment
 * Date:        October 25, 2026
 *
 * Test program for ShresthaStyledCircles.
 *
 * It builds the same scene the program shows, applies mystyle.css, and then
 * checks the real colors JavaFX gave each circle. Each test prints PASS or
 * FAIL to the console, and the totals are printed at the end. The tests
 * cover the style class, both style IDs, the border pane, the ID winning
 * over the class, and what happens when the style sheet is missing.
 *
 * Attribution:
 *   Liang, Y. D. (2024). Introduction to Java Programming and Data
 *     Structures (13th ed.). Pearson. Chapter 31, JavaFX CSS.
 */

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class ShresthaStyledCirclesTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {

        System.out.println("Running ShresthaStyledCircles tests...");
        System.out.println();

        CountDownLatch done = new CountDownLatch(1);

        try {
            // JavaFX objects must be created on the JavaFX thread
            Platform.startup(() -> {

                try {
                    runTests();
                }
                catch (Exception e) {
                    System.out.println("Unexpected error: " + e.getMessage());
                    failed++;
                }
                finally {
                    done.countDown();
                }
            });

            if (!done.await(30, TimeUnit.SECONDS)) {
                System.out.println("Error: The tests did not finish in time.");
                failed++;
            }
        }
        catch (Exception e) {
            System.out.println("Error: JavaFX could not start. " + e.getMessage());
            failed++;
        }
        finally {
            Platform.exit();
        }

        System.out.println();
        System.out.println("Tests passed: " + passed);
        System.out.println("Tests failed: " + failed);
    }

    /**
     * Builds the scene once and runs every check against it
     */
    private static void runTests() {

        Scene scene = ShresthaStyledCircles.createScene();
        scene.getRoot().applyCss();

        HBox root = (HBox) scene.getRoot();
        StackPane borderPane = (StackPane) root.getChildren().get(0);

        Circle circle1 = (Circle) borderPane.getChildren().get(0);
        Circle circle2 = (Circle) root.getChildren().get(1);
        Circle circle3 = (Circle) root.getChildren().get(2);
        Circle circle4 = (Circle) root.getChildren().get(3);

        testStyleSheetLoaded(scene);
        testCircleCount(root, borderPane);
        testRadius(circle1, circle2, circle3, circle4);
        testPlainCircle("Circle 1 (in the border pane)", circle1);
        testPlainCircle("Circle 2", circle2);
        testColoredCircle("Circle 3", circle3, ShresthaStyledCircles.RED_ID, Color.RED);
        testColoredCircle("Circle 4", circle4, ShresthaStyledCircles.GREEN_ID, Color.GREEN);
        testBorderPane(borderPane);
        testMissingStyleSheet();
    }

    private static void testStyleSheetLoaded(Scene scene) {

        check("mystyle.css is added to the scene",
                scene.getStylesheets().size() == 1
                && scene.getStylesheets().get(0).endsWith(ShresthaStyledCircles.STYLE_SHEET));
    }

    private static void testCircleCount(HBox root, StackPane borderPane) {

        long circles = root.getChildren().stream()
                .filter(node -> node instanceof Circle).count()
                + borderPane.getChildren().size();

        check("The window shows four circles", circles == 4);
    }

    private static void testRadius(Circle... circles) {

        boolean allMatch = true;

        for (Circle circle : circles) {
            if (circle.getRadius() != ShresthaStyledCircles.RADIUS) {
                allMatch = false;
            }
        }

        check("Every circle has a radius of " + ShresthaStyledCircles.RADIUS, allMatch);
    }

    /**
     * A plain circle should have the style class, no ID, a white fill, and
     * a black stroke
     */
    private static void testPlainCircle(String name, Circle circle) {

        check(name + " has the plaincircle style class",
                hasPlainClass(circle) && circle.getId() == null);
        check(name + " is filled white", sameColor(circle.getFill(), Color.WHITE));
        check(name + " has a black stroke", sameColor(circle.getStroke(), Color.BLACK));
    }

    /**
     * A colored circle has the style class and an ID. Its fill and stroke
     * should both come from the ID, which shows the ID beat the class.
     */
    private static void testColoredCircle(String name, Circle circle, String id, Color color) {

        check(name + " has the style ID " + id,
                id.equals(circle.getId()) && hasPlainClass(circle));
        check(name + " fill comes from the ID, not the class",
                sameColor(circle.getFill(), color));
        check(name + " stroke comes from the ID, not the class",
                sameColor(circle.getStroke(), color));
    }

    private static void testBorderPane(Region borderPane) {

        boolean hasClass = borderPane.getStyleClass()
                .contains(ShresthaStyledCircles.BORDER_CLASS);
        boolean hasBorder = borderPane.getBorder() != null
                && !borderPane.getBorder().getStrokes().isEmpty();

        check("Circle 1's pane has the border style class", hasClass);
        check("Circle 1's pane shows a border", hasBorder);
    }

    /**
     * Without the style sheet, the circles should fall back to JavaFX's
     * default black fill instead of crashing
     */
    private static void testMissingStyleSheet() {

        Circle circle = ShresthaStyledCircles.createCircle(null);
        Scene scene = new Scene(new HBox(circle));
        scene.getRoot().applyCss();

        check("Without mystyle.css a circle uses the default black fill",
                sameColor(circle.getFill(), Color.BLACK));
    }

    private static boolean hasPlainClass(Node node) {

        return node.getStyleClass().contains(ShresthaStyledCircles.PLAIN_CLASS);
    }

    private static boolean sameColor(Paint paint, Color expected) {

        return expected.equals(paint);
    }

    private static void check(String description, boolean result) {

        if (result) {
            System.out.println("PASS: " + description);
            passed++;
        }
        else {
            System.out.println("FAIL: " + description);
            failed++;
        }
    }
}

/*
 *
 * Rakesh Shrestha
 * Bellevue University
 * CSD420 - Advanced Java Programming
 * Module 1.3 Programming Assignment
 * September 13, 2026
 *
 * Displays four playing cards chosen at random from a 52 card deck.
 * A Refresh button below the cards draws four new cards.
 * The Refresh button's event handler is written as a Lambda Expression.
 *
 * Card images (1.png - 52.png) are expected in a "cards" subdirectory
 * next to this class so the relative Image path below resolves correctly.
 *
 */
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CardGame extends Application {

  // HBox holds the four card ImageViews
  private HBox cardBox = new HBox(10);

  // One ImageView per card slot
  private ImageView[] cardViews = new ImageView[4];

  @Override
  public void start(Stage primaryStage) {

    // Set up the row that will hold the four cards
    cardBox.setAlignment(Pos.CENTER);
    cardBox.setPadding(new Insets(15, 15, 15, 15));

    // Create the four ImageViews and add them to the HBox
    for (int i = 0; i < cardViews.length; i++) {
      cardViews[i] = new ImageView();
      cardViews[i].setFitWidth(100);
      cardViews[i].setFitHeight(140);
      cardViews[i].setPreserveRatio(true);
      cardBox.getChildren().add(cardViews[i]);
    }

    // Draw the first hand of cards
    drawCards();

    // Create the Refresh button
    Button btRefresh = new Button("Refresh");

    // Lambda Expression used as the event handler
    btRefresh.setOnAction(e -> drawCards());

    HBox buttonBox = new HBox(btRefresh);
    buttonBox.setAlignment(Pos.CENTER);
    buttonBox.setPadding(new Insets(0, 15, 15, 15));

    // Cards on top, Refresh button on bottom
    BorderPane pane = new BorderPane();
    pane.setCenter(cardBox);
    pane.setBottom(buttonBox);

    // Create Scene, place in Stage
    Scene scene = new Scene(pane);
    primaryStage.setTitle("ShowRandomCards");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  // Randomly picks four different cards out of the deck and shows them
  private void drawCards() {

    // Build the deck (1-52), shuffle it, then take the top four
    List<Integer> deck = new ArrayList<>();
    for (int i = 1; i <= 52; i++) {
      deck.add(i);
    }
    Collections.shuffle(deck);

    for (int i = 0; i < cardViews.length; i++) {
      int cardNumber = deck.get(i);
      Image image = new Image("cards/" + cardNumber + ".png");
      cardViews[i].setImage(image);
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
}

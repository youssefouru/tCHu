package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.Ticket;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.List;

/**
 * DecksViewCreator : this class creates the view of the deck
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public final class DecksViewCreator {
    private DecksViewCreator() {
    }


    /**
     * this method creates the view of the cards
     *
     * @param gameState   (ObservableGameState) : the observer of the gameState
     * @param ticketsHP   (ObjectProperty< ActionHandlers.DrawTicketsHandler > ) : the handler of the tickets
     * @param drawCardsHP (ObjectProperty< ActionHandlers.DrawCardHandler >) : the handler of the cards
     * @return (Node) : the view of the cards
     */
    public static Node createCardsView(ObservableGameState gameState, ObjectProperty<ActionHandlers.DrawTicketsHandler> ticketsHP, ObjectProperty<ActionHandlers.DrawCardHandler> drawCardsHP) {
        VBox cardPaneBox = new VBox();
        cardPaneBox.getStylesheets().addAll("decks.css", "colors.css");
        cardPaneBox.setId("card-pane");
        Button ticketButton = createButtons(gameState.ticketPercentage(), StringsFr.TICKETS,ticketsHP.isNull());
        Button cardButton = createButtons(gameState.cardPercentage(), StringsFr.CARDS,drawCardsHP.isNull());
        ticketButton.setOnMouseClicked(event -> ticketsHP.get().onDrawTickets());
        cardButton.setOnMouseClicked((event -> drawCardsHP.get().onDrawCard(-1)));
        cardPaneBox.getChildren().add(ticketButton);
        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            ReadOnlyObjectProperty<Card> card = gameState.faceUpCard(slot);
            StackPane cardPane = new StackPane();
            card.addListener((o, oV, nV)-> cardPane.getStyleClass().set(0,nV == Card.LOCOMOTIVE ? "NEUTRAL" : nV.color().name()));
            cardPane.getStyleClass().add("");
            cardPane.getStyleClass().add("card");
            for (Rectangle rectangle : cardRectangle()) {
                cardPane.getChildren().add(rectangle);
            }
            cardPaneBox.getChildren().add(cardPane);
            cardPane.setOnMouseClicked(event -> drawCardsHP.get().onDrawCard(slot));
        }
        cardPaneBox.getChildren().add(cardButton);
        return cardPaneBox;
    }


    private static Button createButtons(ReadOnlyIntegerProperty pctProperty, String text, BooleanBinding booleanProperty) {
        Button mainButton = new Button();
        mainButton.getStyleClass().add("gauged");
        mainButton.setText(text);
        Group graphicGroup = new Group();
        Rectangle backgroundRectangle = new Rectangle(50, 5);
        backgroundRectangle.getStyleClass().add("background");
        Rectangle foregroundRectangle = new Rectangle(50, 5);
        foregroundRectangle.widthProperty().bind(pctProperty.multiply(50).divide(100));
        foregroundRectangle.getStyleClass().add("foreground");
        graphicGroup.getChildren().addAll(backgroundRectangle, foregroundRectangle);
        mainButton.setGraphic(graphicGroup);
        mainButton.disableProperty().bind(booleanProperty);
        return mainButton;
    }

    private static List<Rectangle> cardRectangle() {
        Rectangle outSideRectangle = new Rectangle(60, 90);
        outSideRectangle.getStyleClass().add("outside");
        Rectangle insideRectangle = new Rectangle(40, 70);
        insideRectangle.getStyleClass().addAll("filled", "inside");
        Rectangle trainRectangle = new Rectangle(40, 70);
        trainRectangle.getStyleClass().add("train-image");

        return List.of(outSideRectangle, insideRectangle, trainRectangle);
    }

    /**
     * this method creates the view of the hand of the player
     *
     * @param gameState (ObservableGameState) : the observer of the gameState
     * @return (Node) : the view of the hand of the player
     */
    public static Node createHandView(ObservableGameState gameState) {
        HBox deckBox = new HBox();
        deckBox.getStylesheets().addAll("decks.css", "colors.css");
        ListView<Ticket> ticketListView = new ListView<>(gameState.playerTickets());
        ticketListView.setId("tickets");
        HBox handPaneBox = new HBox();
        handPaneBox.setId("hand-pane");
        for (Card card : Card.ALL) {
            ReadOnlyIntegerProperty count = gameState.cardsTypeNumber(card);
            StackPane cardPane = new StackPane();
            cardPane.visibleProperty().bind(Bindings.greaterThan(count, 0));
            cardPane.getStyleClass().addAll(card == Card.LOCOMOTIVE ? "NEUTRAL" : card.name(),
                    "card");
            Text countText = new Text();
            countText.getStyleClass().add("count");
            countText.visibleProperty().bind(Bindings.greaterThan(count, 1));
            countText.textProperty().bind(Bindings.convert(count));
            for (Rectangle rectangle : cardRectangle()) {
                cardPane.getChildren().add(rectangle);
            }
            cardPane.getChildren().add(countText);
            handPaneBox.getChildren().add(cardPane);
        }
        deckBox.getChildren().addAll(ticketListView, handPaneBox);
        return deckBox;
    }
}

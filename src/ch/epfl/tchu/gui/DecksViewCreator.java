package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.Ticket;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
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
    private static final String DECK_CLASS = "decks.css";
    private static final String COLORS_CLASS = "colors.css";
    private static final String DECKS_CLASS = "decks.css";
    private static final String CARD_PANE_NAME = "card-pane";
    private static final String TICKETS_NAME = "tickets";
    private static final String HAND_PANE_NAME = "hand-pane";
    private static final String FOREGROUND_NAME = "foreground";
    private static final String BACKGROUND_NAME = "background";
    private static final String GAUGED_NAME = "gauged";
    private static final String NEUTRAL_NAME = "NEUTRAL";
    private static final String CARD_NAME = "card";
    private static final String COUNT_NAME = "count";
    private static final String FILLED_NAME = "filled";
    private static final String INSIDE_NAME = "inside";
    private static final String OUTSIDE_NAME = "outside";
    private static final String TRAIN_IMAGE_NAME = "train-image";
    private static final int BUTTON_RECTANGLE_WIDTH = 50;
    private static final int BUTTON_RECTANGLE_HEIGHT = 5;
    private static final int RECTANGLE_OUTSIDE_WIDTH = 60;
    private static final int RECTANGLE_OUTSIDE_HEIGHT = 90;
    private static final int RECTANGLE_WIDTH = 40;
    private static final int RECTANGLE_HEIGHT = 70;

    private DecksViewCreator() {
    }


    /**
     * This method creates the view of the cards.
     *
     * @param gameState   (ObservableGameState) : The observer of the gameState.
     * @param ticketsHP   (ObjectProperty< DrawTicketsHandler > ) : The handler of the tickets.
     * @param drawCardsHP (ObjectProperty< DrawCardHandler >) : The handler of the cards.
     * @return (Node) : The view of the cards.
     */
    public static Node createCardsView(ObservableGameState gameState, ObjectProperty<ActionHandlers.DrawTicketsHandler> ticketsHP, ObjectProperty<ActionHandlers.DrawCardHandler> drawCardsHP, Node chatBox) {
        VBox cardPaneBox = new VBox();
        cardPaneBox.getStylesheets().addAll(DECKS_CLASS, COLORS_CLASS);
        cardPaneBox.setId(CARD_PANE_NAME);

        Button ticketButton = createButtons(gameState.ticketPercentage(), StringsFr.TICKETS, ticketsHP.isNull());

        Button cardButton = createButtons(gameState.cardPercentage(), StringsFr.CARDS, drawCardsHP.isNull());

        ticketButton.setOnMouseClicked(event -> ticketsHP.get().onDrawTickets());

        ticketButton.disableProperty().bind(ticketsHP.isNull());

        cardButton.setOnMouseClicked((event -> drawCardsHP.get().onDrawCard(Constants.DECK_SLOT)));

        cardButton.disableProperty().bind(drawCardsHP.isNull());

        cardPaneBox.getChildren().add(ticketButton);

        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            ReadOnlyObjectProperty<Card> card = gameState.faceUpCard(slot);
            StackPane cardPane = new StackPane();

            card.addListener((o, oV, nV) -> cardPane.getStyleClass().set(0, nV == Card.LOCOMOTIVE ? NEUTRAL_NAME : nV.color().name()));

            cardPane.getStyleClass().add("");

            cardPane.getStyleClass().add(CARD_NAME);

            for (Rectangle rectangle : cardRectangle()) {
                cardPane.getChildren().add(rectangle);
            }
            cardPaneBox.getChildren().add(cardPane);
            cardPane.disableProperty().bind(drawCardsHP.isNull());
            cardPane.setOnMouseClicked(event -> drawCardsHP.get().onDrawCard(slot));
        }
        cardPaneBox.getChildren().add(cardButton);
        return new HBox(chatBox, cardPaneBox);
    }

    /**
     * This method creates the view of the hand of the player.
     *
     * @param gameState (ObservableGameState) : The observer of the gameState.
     * @return (Node) : The view of the hand of the player.
     */
    public static Node createHandView(ObservableGameState gameState) {
        HBox deckBox = new HBox();

        deckBox.getStylesheets().addAll(DECK_CLASS, COLORS_CLASS);

        ListView<Ticket> ticketListView = new ListView<>(gameState.playerTickets());

        ticketListView.setId(TICKETS_NAME);

        HBox handPaneBox = new HBox();

        handPaneBox.setId(HAND_PANE_NAME);

        for (Card card : Card.ALL) {
            ReadOnlyIntegerProperty count = gameState.cardsTypeNumber(card);

            StackPane cardPane = new StackPane();

            cardPane.visibleProperty().bind(Bindings.greaterThan(count, 0));

            cardPane.getStyleClass().addAll(card == Card.LOCOMOTIVE ? NEUTRAL_NAME : card.name(),
                    CARD_NAME);

            Text countText = new Text();

            countText.getStyleClass().add(COUNT_NAME);

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

    private static Button createButtons(ReadOnlyIntegerProperty pctProperty, String text, BooleanBinding booleanProperty) {
        Button mainButton = new Button();

        mainButton.getStyleClass().add(GAUGED_NAME);

        mainButton.setText(text);

        Group graphicGroup = new Group();

        Rectangle backgroundRectangle = new Rectangle(BUTTON_RECTANGLE_WIDTH, BUTTON_RECTANGLE_HEIGHT);

        backgroundRectangle.getStyleClass().add(BACKGROUND_NAME);

        Rectangle foregroundRectangle = new Rectangle(BUTTON_RECTANGLE_WIDTH, BUTTON_RECTANGLE_HEIGHT);

        foregroundRectangle.widthProperty().bind(pctProperty.multiply(BUTTON_RECTANGLE_WIDTH).divide(100));

        foregroundRectangle.getStyleClass().add(FOREGROUND_NAME);

        graphicGroup.getChildren().addAll(backgroundRectangle, foregroundRectangle);

        mainButton.setGraphic(graphicGroup);

        mainButton.disableProperty().bind(booleanProperty);

        return mainButton;
    }

    private static List<Rectangle> cardRectangle() {
        Rectangle outSideRectangle = new Rectangle(RECTANGLE_OUTSIDE_WIDTH, RECTANGLE_OUTSIDE_HEIGHT);

        outSideRectangle.getStyleClass().add(OUTSIDE_NAME);

        Rectangle insideRectangle = new Rectangle(RECTANGLE_WIDTH, RECTANGLE_HEIGHT);

        insideRectangle.getStyleClass().addAll(FILLED_NAME, INSIDE_NAME);
        Rectangle trainRectangle = new Rectangle(RECTANGLE_WIDTH, RECTANGLE_HEIGHT);

        trainRectangle.getStyleClass().add(TRAIN_IMAGE_NAME);

        return List.of(outSideRectangle, insideRectangle, trainRectangle);
    }
}

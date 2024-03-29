package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.gui.CardBagStringConverter;
import ch.epfl.tchu.gui.ObservableGameState;
import ch.epfl.tchu.gui.StringsFr;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.List;
import java.util.Map;

import static javafx.application.Platform.isFxApplicationThread;

/**
 * GraphicalPlayer : This class represents the graphical interface of the main player.
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public final class GraphicalPlayer {
    private final static int MAX_INFO_NUMBER = 5;
    private final static String CHOOSER_CLASS = "chooser.css";
    private static final String CHOOSE_THE_PLAYER = "Choose The  Player";
    private static final String SEND_NAME = "SEND";
    private final ObservableGameState gameState;
    private final ObservableList<Text> messages;
    private final ObjectProperty<ActionHandlers.DrawCardHandler> drawCardHP;
    private final ObjectProperty<ActionHandlers.ClaimRouteHandler> claimRouteHP;
    private final ObjectProperty<ActionHandlers.DrawTicketsHandler> drawTicketHP;
    private final Stage mainStage;
    private final PlayerId id;
    private final Map<PlayerId, String> playerNames;

    /**
     * Constructor of the GraphicalPlayer.
     *
     * @param playerId    (PlayerId) : the id of the player.
     * @param playerNames (Map< PlayerId, String >) : the names of the players.
     */
    public GraphicalPlayer(PlayerId playerId, Map<PlayerId, String> playerNames, ActionHandlers.MessageSender messageSender, ObservableList<Text> chatList) {
        assert isFxApplicationThread();
        messages = FXCollections.observableArrayList();
        id = playerId;
        this.playerNames = playerNames;
        gameState = new ObservableGameState(playerId);
        drawCardHP = new SimpleObjectProperty<>();
        claimRouteHP = new SimpleObjectProperty<>();
        drawTicketHP = new SimpleObjectProperty<>();
        mainStage = new Stage();
        mainStage.setTitle("tCHu \u2014 " + playerNames.get(playerId));
        BorderPane mainPain = new BorderPane(MapViewCreator.createMapView(gameState, claimRouteHP,this::chooseClaimCards),
                null,
                DecksViewCreator.createCardsView(gameState, drawTicketHP, drawCardHP, chatBoxCreator(chatList, playerId, playerNames,messageSender)),
                DecksViewCreator.createHandView(gameState),
                InfoViewCreator.createInfoView(playerId, playerNames, gameState, messages));
        mainStage.setScene(new Scene(mainPain));
        mainStage.show();
    }

    private static Node chatBoxCreator(ObservableList<Text> chatMessage, PlayerId currentId, Map<PlayerId, String> playerNames, ActionHandlers.MessageSender messageSender) {
        ListView<Text> chats = new ListView<>(chatMessage);
        TextField messageField = new TextField();
        MenuItem[] items = new MenuItem[PlayerId.COUNT];
        for (PlayerId playerId : PlayerId.ALL) {
            if (playerId == currentId) continue;
            MenuItem menuItem = new MenuItem(playerNames.get(playerId));
            menuItem.setOnAction((event) -> {
                if(!messageField.getText().isEmpty()) {
                    messageSender.onSentMessage(messageField.getText().trim(), currentId, playerId);
                    messageField.clear();
                }
            });
            int index = playerId.ordinal()>currentId.ordinal()?playerId.ordinal()-1:playerId.ordinal();
            items[index] = menuItem;
        }
        MenuItem allItem = new MenuItem("ALL");
        allItem.setOnAction((event -> {
            if(!messageField.getText().trim().isEmpty()) {
                messageSender.onSentMessage(messageField.getText(), currentId, null);
                messageField.clear();
            }
        }));
        items[PlayerId.COUNT -1] = allItem;

        Button sendButton = new Button();
        sendButton.setText(SEND_NAME);
        MenuButton idChooser = new MenuButton(CHOOSE_THE_PLAYER, sendButton, items);
        return new VBox(chats, idChooser,messageField);
    }

    private void handlerSetter() {
        drawTicketHP.set(null);
        drawCardHP.set(null);
        claimRouteHP.set(null);
    }

    /**
     * This method calls the method setState of the observable GameState.
     *
     * @param publicGameState (PublicGameState) : the new publicGameState of the observableGameState.
     * @param playerState     (PlayerState) : the new playerState of the observableGameState.
     */
    public void setState(PublicGameState publicGameState, PlayerState playerState) {
        assert isFxApplicationThread();
        gameState.setState(publicGameState, playerState);
    }



    /**
     * this method is Called in the start of each turn
     *
     * @param drawTicketsHandler (DrawTicketsHandler) : the ticketHandler the player will use to draw tickets
     * @param drawCardHandler    (DrawCardHandler) : the drawCardHandler the player will use to draw cards
     * @param claimRouteHandler  (ClaimRouteHandler) : the claimRouteHandler the player will use to clain a route
     */
    public void startTurn(ActionHandlers.DrawTicketsHandler drawTicketsHandler, ActionHandlers.DrawCardHandler drawCardHandler, ActionHandlers.ClaimRouteHandler claimRouteHandler) {
        assert isFxApplicationThread();
        claimRouteHP.set((route, cards) -> {
            handlerSetter();
            claimRouteHandler.onClaimRoute(route, cards);
        });
        if (gameState.canDrawTickets()) {
            drawTicketHP.set(() -> {
                handlerSetter();
                drawTicketsHandler.onDrawTickets();
            });
        }
        if (gameState.canDrawCards()) {
            drawCardHP.set((i) -> {
                handlerSetter();
                drawCardHandler.onDrawCard(i);
            });
        }
    }

    /**
     * This method is used to receive the infos.
     *
     * @param string (String) : the info we want to receive
     */
    public void receiveInfo(String string) {
        assert isFxApplicationThread();
        while (messages.size() >= MAX_INFO_NUMBER) {
            messages.remove(0);
        }
        messages.add(new Text(string));

    }


    /**
     * this method creates the window where the player will choose the tickets
     *
     * @param tickets        (SortedBag< Ticket >) : the tickets he has to choose
     * @param ticketsHandler (ActionHandlers.ChooseTicketsHandler) : the ticketHandler the player will use to draw tickets
     */
    public void chooseTickets(SortedBag<Ticket> tickets, ActionHandlers.ChooseTicketsHandler ticketsHandler) {
        assert isFxApplicationThread();
        VBox mainBox = new VBox();
        Stage chooserStage = stageCreator(StringsFr.TICKETS_CHOICE, mainBox);
        int minimalNumberOfCards = tickets.size() - Constants.DISCARDABLE_TICKETS_COUNT;
        TextFlow textFlow = new TextFlow(new Text(String.format(StringsFr.CHOOSE_TICKETS, minimalNumberOfCards, StringsFr.plural(minimalNumberOfCards))));
        ListView<Ticket> listView = new ListView<>(FXCollections.observableArrayList(tickets.toList()));
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        Button chooseButton = new Button();
        chooseButton.setText(StringsFr.CHOOSE);


        chooseButton.disableProperty().bind(Bindings.lessThan(Bindings.size(listView.getSelectionModel().getSelectedItems()),
                minimalNumberOfCards));
        chooseButton.setOnMouseClicked(e -> {
            ticketsHandler.onChooseTickets(SortedBag.of(listView.getSelectionModel().getSelectedItems()));
            chooserStage.hide();
        });
        mainBox.getChildren().addAll(textFlow, listView, chooseButton);
        chooserStage.show();

    }

    private Stage stageCreator(String title, VBox box) {
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.initOwner(mainStage);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle(title);
        stage.setOnCloseRequest(Event::consume);
        Scene scene = new Scene(box);
        stage.setScene(scene);
        scene.getStylesheets().add(CHOOSER_CLASS);
        return stage;

    }


    /**
     * This method is called when the player want to draw the second card
     *
     * @param drawCardHandler (DrawCardHandler) : the handler we will draw cards with
     */
    public void drawCard(ActionHandlers.DrawCardHandler drawCardHandler) {
        assert isFxApplicationThread();
        drawCardHP.set((c) -> {
            drawTicketHP.set(null);
            claimRouteHP.set(null);
            drawCardHP.set(null);
            drawCardHandler.onDrawCard(c);
        });


    }

    /**
     * This method is used to create the window to choose the additional Cards
     *
     * @param possibleAdditionalCards (List< SortedBag< Card > >) : the possible additional cards the player he can play to claim the underground root
     * @param chooseCardsHandler      (ChooseCardsHandler) : the handler to choose the player will use to choose it's cards
     */
    public void chooseAdditionalCards(List<SortedBag<Card>> possibleAdditionalCards, ActionHandlers.ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();
        VBox mainBox = new VBox();
        Stage additionalCardsStage = stageCreator(StringsFr.CHOOSE_ADDITIONAL_CARDS, mainBox);
        TextFlow textFlow = new TextFlow(new Text(StringsFr.CHOOSE_CARDS));
        ListView<SortedBag<Card>> additionalCardsView = cardBagView(possibleAdditionalCards);
        Button chooseButton = new Button();
        chooseButton.setText(StringsFr.CHOOSE);
        chooseButton.setOnMouseClicked(event -> {
            MultipleSelectionModel<SortedBag<Card>> sortedBagSelectionModel = additionalCardsView.getSelectionModel();
            SortedBag<Card> cardsChosen = sortedBagSelectionModel.getSelectedItem();
            chooseCardsHandler.onChooseCards(cardsChosen == null ? SortedBag.of() : cardsChosen);
            additionalCardsStage.hide();
        });
        mainBox.getChildren().addAll(textFlow, additionalCardsView, chooseButton);
        additionalCardsStage.show();
    }

    private ListView<SortedBag<Card>> cardBagView(List<SortedBag<Card>> possibleBags) {
        ListView<SortedBag<Card>> possibleView = new ListView<>(FXCollections.observableArrayList(possibleBags));
        possibleView.setCellFactory((v) -> new TextFieldListCell<>(new CardBagStringConverter()));
        possibleView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        return possibleView;
    }


    /**
     * this method creates the window we will use to choose the cards
     *
     * @param claimCards         (List<SortedBag<Card>>) : all the possible claimCards the player can play to claim the route
     * @param chooseCardsHandler (ChooseCardsHandler) : the handler that the player will use to choose which cards he wants to play
     */
    public void chooseClaimCards(List<SortedBag<Card>> claimCards, ActionHandlers.ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();
        VBox mainBox = new VBox();
        Stage claimCardsStage = stageCreator(StringsFr.CHOOSE_CARDS, mainBox);

        TextFlow textFlow = new TextFlow(new Text(StringsFr.CHOOSE_CARDS));
        Button chooseButton = new Button();
        chooseButton.setText(StringsFr.CHOOSE);
        ListView<SortedBag<Card>> possibleClaimCardView = cardBagView(claimCards);
        chooseButton.disableProperty().bind(possibleClaimCardView.getSelectionModel().selectedItemProperty().isNull());

        chooseButton.setOnAction(event -> {
            chooseCardsHandler.onChooseCards(possibleClaimCardView.getSelectionModel().getSelectedItem());
            claimCardsStage.hide();
        });

        mainBox.getChildren().addAll(textFlow, possibleClaimCardView, chooseButton);
        claimCardsStage.show();

    }

}
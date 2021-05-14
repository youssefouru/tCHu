package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
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

import static ch.epfl.tchu.gui.ActionHandlers.*;
import static javafx.application.Platform.isFxApplicationThread;

public final class GraphicalPlayer {
    private final static int MAX_INFO_NUMBER = 5;
    private final ObservableGameState gameState;
    private final ObservableList<Text> messages;
    private final ObjectProperty<DrawCardHandler> drawCardHP;
    private final ObjectProperty<ClaimRouteHandler> claimRouteHP;
    private final ObjectProperty<DrawTicketsHandler> drawTicketHP;
    private final Stage mainStage;

    /**
     * Constructor of the GraphicalPlayer.
     *
     * @param playerId    (PlayerId) : the id of the player
     * @param playerNames (Map< PlayerId, String >) : the names of the players
     */
    public GraphicalPlayer(PlayerId playerId, Map<PlayerId, String> playerNames) {
        assert isFxApplicationThread();
        messages = FXCollections.observableArrayList();
        gameState = new ObservableGameState(playerId);
        drawCardHP = new SimpleObjectProperty<>();
        claimRouteHP = new SimpleObjectProperty<>();
        drawTicketHP = new SimpleObjectProperty<>();
        mainStage = new Stage(StageStyle.UTILITY);
        mainStage.setTitle("tCHu \u2014 " + playerNames.get(playerId));
        BorderPane mainPain = new BorderPane(MapViewCreator.createMapView(gameState, claimRouteHP, this::chooseClaimCards),
                                            null,
                                            DecksViewCreator.createCardsView(gameState, drawTicketHP, drawCardHP),
                                            DecksViewCreator.createHandView(gameState),
                                            InfoViewCreator.createInfoView(playerId, playerNames, gameState, messages));
        mainStage.setScene(new Scene(mainPain));
        mainStage.show();
    }

    private void handlerSetter(){
        drawTicketHP.set(null);
        drawCardHP.set(null);
        claimRouteHP.set(null);
    }

    /**
     * this method calls the method setState of the observable GameState
     *
     * @param publicGameState (PublicGameState) : the new publicGameState of the observableGameState
     * @param playerState     (PlayerState) : the new playerState of the observableGameState
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
    public void startTurn(DrawTicketsHandler drawTicketsHandler, DrawCardHandler drawCardHandler, ClaimRouteHandler claimRouteHandler) {
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
    public void chooseTickets(SortedBag<Ticket> tickets, ChooseTicketsHandler ticketsHandler) {
        assert isFxApplicationThread();
        Preconditions.checkArgument(tickets.size() == Constants.IN_GAME_TICKETS_COUNT || tickets.size() == Constants.INITIAL_TICKETS_COUNT);
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
        scene.getStylesheets().add("chooser.css");
        return stage;

    }


    /**
     * This method is called when the player want to draw the second card
     *
     * @param drawCardHandler (DrawCardHandler) : the handler we will draw cards with
     */
    public void drawCard(DrawCardHandler drawCardHandler) {
        assert isFxApplicationThread();
        drawCardHP.set((c)->{
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
    public void chooseAdditionalCards(List<SortedBag<Card>> possibleAdditionalCards, ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();
        VBox mainBox = new VBox();
        Stage additionalCardsStage = stageCreator(StringsFr.CHOOSE_ADDITIONAL_CARDS, mainBox);
        TextFlow textFlow = new TextFlow(new Text(StringsFr.CHOOSE_CARDS));
        ListView<SortedBag<Card>> additionalCardsView = cardBagView(possibleAdditionalCards);
        Button chooseButton = new Button();
        chooseButton.setOnMouseClicked(event -> {
            if (additionalCardsView.getSelectionModel().selectionModeProperty().isNull().get()) {
                chooseCardsHandler.onChooseCards(SortedBag.of());
            } else {
                chooseCardsHandler.onChooseCards(additionalCardsView.getSelectionModel().getSelectedItem());
            }
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
    public void chooseClaimCards(List<SortedBag<Card>> claimCards, ChooseCardsHandler chooseCardsHandler) {
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
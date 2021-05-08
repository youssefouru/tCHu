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
    private final Map<PlayerId, String> playerNames;
    private final ObservableGameState gameState;
    private final ObservableList<Text> messages;
    private final ObjectProperty<DrawCardHandler> drawCardHP;
    private final ObjectProperty<ClaimRouteHandler> claimRouteHP;
    private final ObjectProperty<DrawTicketsHandler> drawTicketHP;
    private final ObjectProperty<ChooseCardsHandler> chooseCardsHP;
    private final Stage mainStage;

    /**
     * Constructor of the GraphicalPlayer
     *
     * @param playerId    (PlayerId) : the id of the player
     * @param playerNames (Map< PlayerId, String >) : the names of the players
     */
    public GraphicalPlayer(PlayerId playerId, Map<PlayerId, String> playerNames) {
        assert isFxApplicationThread();
        this.playerNames = playerNames;
        messages = FXCollections.observableArrayList();
        gameState = new ObservableGameState(playerId);
        drawCardHP = new SimpleObjectProperty<>();
        claimRouteHP = new SimpleObjectProperty<>();
        drawTicketHP = new SimpleObjectProperty<>();
        chooseCardsHP = new SimpleObjectProperty<>();
        MapViewCreator.CardChooser chooser = (cards, c) -> chooseClaimCards(cards, chooseCardsHP.get());
        mainStage = new Stage(StageStyle.UTILITY);
        mainStage.setTitle("tCHu - " + playerNames.get(playerId));
        BorderPane mainPain = new BorderPane(MapViewCreator.createMapView(gameState, claimRouteHP, chooser),
                null,
                DecksViewCreator.createCardsView(gameState, drawTicketHP, drawCardHP), DecksViewCreator.createHandView(gameState)
                , InfoViewCreator.createInfoView(playerId, playerNames, gameState, messages));

        Scene mainScene = new Scene(mainPain);
        mainStage.setScene(mainScene);
        mainStage.show();
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
     * @param drawTicketsHandler (DrawTicketsHandler) : the ticket handler
     * @param drawCardHandler    (DrawCardHandler) :
     * @param claimRouteHandler  (ClaimRouteHandler) :
     */
    public void startTurn(DrawTicketsHandler drawTicketsHandler, DrawCardHandler drawCardHandler, ClaimRouteHandler claimRouteHandler) {
        assert isFxApplicationThread();

        claimRouteHP.set((route, cards) -> {
            drawTicketHP.set(null);
            drawCardHP.set(null);
            claimRouteHandler.onClaimRoute(route, cards);
            claimRouteHP.set(null);
        });
        if (gameState.canDrawTickets()) {
            drawTicketHP.set(() -> {
                drawCardHP.set(null);
                claimRouteHP.set(null);
                drawTicketsHandler.onDrawTickets();
                drawTicketHP.set(null);
            });
        }
        if (gameState.canDrawCards()) {
            drawCardHP.set((i) -> {
                claimRouteHP.set(null);
                drawTicketHP.set(null);
                drawCardHandler.onDrawCard(i);
                drawCardHP.set(null);
            });
        }

    }

    /**
     * this method receive the info
     *
     * @param string (String) : the info we want to receive
     */
    public void receiveInfo(String string) {
        assert isFxApplicationThread();
        if (messages.size() == MAX_INFO_NUMBER) {
            messages.remove(0);
        }
        messages.add(new Text(string));

    }


    /**
     * this method creates the window where the player will choose the tickets
     *
     * @param tickets        (SortedBag< Ticket >) : the tickets he has to choose
     * @param ticketsHandler (ActionHandlers.ChooseTicketsHandler) : the ticket
     */
    public void chooseTickets(SortedBag<Ticket> tickets, ChooseTicketsHandler ticketsHandler) {
        assert isFxApplicationThread();
        Preconditions.checkArgument(tickets.size() == Constants.IN_GAME_TICKETS_COUNT || tickets.size() == Constants.INITIAL_TICKETS_COUNT);
        VBox mainBox = new VBox();
        Stage chooserStage = stageCreator(StringsFr.TICKETS_CHOICE, mainBox);
        chooserStage.show();
        int minimalNumberOfCards = tickets.size() - Constants.DISCARDABLE_TICKETS_COUNT;
        TextFlow textFlow = new TextFlow(new Text(String.format(StringsFr.CHOOSE_TICKETS, minimalNumberOfCards, StringsFr.plural(minimalNumberOfCards))));
        ListView<Ticket> listView = new ListView<>(FXCollections.observableArrayList(tickets.toList()));
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        Button chooseButton = new Button();
        chooseButton.setText(StringsFr.CHOOSE);
        ReadOnlyObjectProperty<Ticket> ticketObjectProperty = listView.getSelectionModel().selectedItemProperty();

        mainBox.getChildren().addAll(textFlow, listView, chooseButton);
        SortedBag<Ticket> chosenTickets = SortedBag.of(listView.getSelectionModel().getSelectedItems());
        chooseButton.disableProperty().bind(ticketObjectProperty.isNull());
        chooseButton.setOnMouseClicked(e -> {
            ticketsHandler.onChooseTickets(chosenTickets);
            chooserStage.hide();
        });
        mainBox.getChildren().addAll(textFlow, listView, chooseButton);

    }

    private Stage stageCreator(String title, VBox box) {
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(mainStage);
        stage.setTitle(title);
        BorderPane mainPane = new BorderPane(box);
        Scene scene = new Scene(mainPane);
        scene.getStylesheets().add("chooser.css");
        stage.setScene(scene);
        return stage;

    }


    /**
     * This method is called when the player want to draw the second card
     *
     * @param drawCardHandler (DrawCardHandler) : the handler we will draw cards with
     */
    public void drawCard(DrawCardHandler drawCardHandler) {
        assert isFxApplicationThread();
        drawCardHP.set(drawCardHandler);
        drawTicketHP.set(null);
        claimRouteHP.set(null);
        chooseCardsHP.set(null);

    }

    /**
     * This method is used to create the window to choose the additional Cards
     *
     * @param possibleAdditionalCards (List< SortedBag< Card > >)
     * @param chooseCardsHandler      (ChooseCardsHandler) :
     */
    public void chooseAdditionalCards(List<SortedBag<Card>> possibleAdditionalCards, ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();
        VBox mainBox = new VBox();
        Stage additionalCards = stageCreator(StringsFr.CHOOSE_ADDITIONAL_CARDS, mainBox);
        TextFlow textFlow = new TextFlow(new Text(StringsFr.CHOOSE_ADDITIONAL_CARDS));
        ListView<SortedBag<Card>> additionalCardsView = cardBagView(possibleAdditionalCards);
        Button chooseButton = new Button();
        chooseButton.setOnMouseClicked(event -> {
            if (additionalCardsView.getSelectionModel().selectionModeProperty().isNull().get()) {
                chooseCardsHandler.onChooseCards(SortedBag.of());
            } else {
                chooseCardsHandler.onChooseCards(additionalCardsView.getSelectionModel().getSelectedItem());
            }
            additionalCards.hide();
        });
        mainBox.getChildren().addAll(textFlow, additionalCardsView, chooseButton);
    }

    private ListView<SortedBag<Card>> cardBagView(List<SortedBag<Card>> possibleBags) {
        ListView<SortedBag<Card>> possibleView = new ListView<>(FXCollections.observableArrayList(possibleBags));
        possibleView.setCellFactory((v) -> new TextFieldListCell<>(new CardBagStringConverter()));
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
        claimCardsStage.show();
        TextFlow textFlow = new TextFlow(new Text(StringsFr.CHOOSE_CARDS));
        Button chooseButton = new Button();
        chooseButton.setText(StringsFr.CHOOSE);
        ListView<SortedBag<Card>> possibleClaimCardView = cardBagView(claimCards);
        chooseButton.disableProperty().bind(possibleClaimCardView.getSelectionModel().selectedItemProperty().isNull());
        chooseButton.setOnMouseClicked(event -> {
            chooseCardsHandler.onChooseCards(possibleClaimCardView.getSelectionModel().getSelectedItem());
            claimCardsStage.hide();
        });

        mainBox.getChildren().addAll(textFlow, possibleClaimCardView, chooseButton);

    }

}
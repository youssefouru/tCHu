package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Map;

import static ch.epfl.tchu.game.PlayerId.ALL;

/**
 * InfoViewCreator : this class represents the creator of the info view
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
final class InfoViewCreator {
    private static final int CIRCLE_RADIUS = 5;
    private final static String PLAYER_STATS_STRING = "player-stats";
    private final static String FILLED_STRING = "filled";
    private final static String GAME_INFO_STRING = "game-info";
    private final static String INFO_CLASS = "info.css";
    private final static String COLOR_CLASS = "colors.css";

    private InfoViewCreator() {

    }

    /**
     * This method creates the view of the info.
     *
     * @param playerId    (PlayerId) : The id of the player who has the info.
     * @param playerNames (Map< PlayerId, String >) : The map that gives us all the names of each player.
     * @param gameState   (ObservableGameState) : The observer of the gameState.
     * @param texts       (ObservableList<Text>) : The list of info we want to show.
     * @return (Node) : The view of the info.
     */
    public static Node createInfoView(PlayerId playerId, Map<PlayerId, String> playerNames, ObservableGameState gameState, ObservableList<Text> texts) {
        VBox statBox = new VBox();

        statBox.setId(PLAYER_STATS_STRING);

        for (PlayerId id : ALL) {

            Text statsText = new Text();

            statsText.textProperty().bind(Bindings.format(StringsFr.PLAYER_STATS, playerNames.get(id), gameState.ticketCount(id), gameState.cardCount(id), gameState.carCount(id), gameState.claimPoints(id)));

            Circle infoCircle = new Circle(CIRCLE_RADIUS);

            infoCircle.getStyleClass().add(FILLED_STRING);

            TextFlow idFlow = new TextFlow(infoCircle, statsText);

            idFlow.getStyleClass().add(id.name());

            statBox.getChildren().add(idFlow);
        }

        FXCollections.rotate(statBox.getChildren(), -playerId.ordinal());

        TextFlow messageFlow = new TextFlow();

        messageFlow.setId(GAME_INFO_STRING);

        Bindings.bindContent(messageFlow.getChildren(), texts);

        VBox infoBox = new VBox(statBox, new Separator(), messageFlow);

        infoBox.getStylesheets().addAll(INFO_CLASS, COLOR_CLASS);

        return infoBox;
    }
}

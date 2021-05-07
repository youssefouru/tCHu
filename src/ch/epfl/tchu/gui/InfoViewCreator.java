package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Map;
import static ch.epfl.tchu.game.PlayerId.ALL;
public final class InfoViewCreator {
    private static final int CIRCLE_RADIUS = 5;
    /**
     * @param playerId
     * @param playerNames
     * @param gameState
     * @param texts
     * @return
     */
    public static Node createInfoView(PlayerId playerId, Map<PlayerId, String> playerNames, ObservableGameState gameState, ObservableList<Text> texts) {
        VBox statBox = new VBox();
        statBox.setId("player-stats");
        for (PlayerId id : ALL) {
            Text statsText = new Text();
            statsText.textProperty().bind(Bindings.format(StringsFr.PLAYER_STATS, playerNames.get(id), gameState.ticketCount(id), gameState.cardCount(id), gameState.carCount(id), gameState.claimPoints(id)));
            Circle infoCircle = new Circle(CIRCLE_RADIUS);
            infoCircle.getStyleClass().add("filled");
            TextFlow idFlow = new TextFlow(infoCircle,statsText);
            idFlow.getStyleClass().add(id.name());
            statBox.getChildren().add(idFlow);
        }
        TextFlow messageFlow = new TextFlow();
        messageFlow.setId("game-info");
        Bindings.bindContent(messageFlow.getChildren(), texts);
        VBox infoBox = new VBox(statBox, new Separator(), messageFlow);
        infoBox.getStylesheets().addAll("info.css", "colors.css");
        return infoBox;
    }
}

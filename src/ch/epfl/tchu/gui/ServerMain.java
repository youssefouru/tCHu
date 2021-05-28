package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Game;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.stage.Stage;

import java.net.ServerSocket;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static ch.epfl.tchu.game.ChMap.tickets;

/**
 * ServerMain : This class represents the main server that  will host the game.
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public final class ServerMain extends Application {
    private final static int PORT_NUMBER = 5108;
    private final static List<String> DEFAULT_NAMES = List.of("Ada","Charles");
    /**
     * This method will launch the arguments of the program
     *
     * @param args (String[]) : the arguments of the programme
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * This method launch the server connects the other player to the game and launch the game
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages.
     * @throws Exception if something goes wrong
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        List<String> parameters = getParameters().getRaw();
        ServerSocket serverSocket = new ServerSocket(PORT_NUMBER);
        int i = 0;

        Map<PlayerId, Player> players = new EnumMap<>(PlayerId.class);
        Map<PlayerId, String> playerNames = new EnumMap<>(PlayerId.class);
        for(PlayerId playerId : PlayerId.ALL){
            if(playerId == PlayerId.PLAYER_1){
                players.put(playerId, new GraphicalPlayerAdapter());
                playerNames.put(playerId, parameters.isEmpty() ?  DEFAULT_NAMES.get(i++): parameters.get(i++));
                continue;
            }
            players.put(playerId, new RemotePlayerProxy(serverSocket.accept()));
            playerNames.put(playerId, parameters.isEmpty() ? DEFAULT_NAMES.get(i): parameters.get(i));
        }
        new Thread(() -> Game.play(players, playerNames, SortedBag.of(tickets()), new Random())).start();

    }
}

package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.AdvancedPlayer;
import ch.epfl.tchu.game.Game;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.net.MessageId;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.collections.ObservableList;
import javafx.scene.text.Text;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static ch.epfl.tchu.game.ChMap.tickets;
import static javafx.application.Platform.runLater;

/**
 * ServerMain : This class represents the main server that  will host the game.
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public final class ServerMain {
    private final static List<String> DEFAULT_NAMES = List.of("Ada", "Charles", "Bob", "Alice");
    private final static String CONNECTED_NAME = "%s is connected";

    /**
     * This method will launch the arguments of the program
     *
     * @param args (String[]) : the arguments of the program
     * @throws IOException : if something goes wrong
     */
    public static void main(String[] args, int portNumber, ObservableList<Text> playerConnected) throws IOException {
        ServerSocket server = new ServerSocket(portNumber);
        int i = 0;
        Map<PlayerId, AdvancedPlayer> players = new EnumMap<>(PlayerId.class);
        Map<PlayerId, String> playerNames = new EnumMap<>(PlayerId.class);
        MessageManager manager = new MessageManager(players, playerNames);
        for (PlayerId playerId : PlayerId.ALL) {
            Socket messageSocket = server.accept();
            Socket instructionSocket = server.accept();
            players.put(playerId, new RemotePlayerProxy(instructionSocket, messageSocket, manager));
            String name = args.length <= playerId.ordinal() ? DEFAULT_NAMES.get(i++) : args[i++];
            playerNames.put(playerId, name);
            runLater(() -> playerConnected.add(new Text(String.format(CONNECTED_NAME, name))));
        }

        new Thread(() -> Game.play(players, playerNames, SortedBag.of(tickets()), new Random())).start();
        for (PlayerId playerId : PlayerId.ALL) {
            new Thread(() -> players.get(playerId).sendToManager()).start();
        }


    }



}

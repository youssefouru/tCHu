package ch.epfl.tchu.bonus;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.PlayerId;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLOutput;
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
public final class ServerMainBonus{
    private final static int PORT_NUMBER = 5108;
    private final static List<String> DEFAULT_NAMES = List.of("Ada","Charles","Bob","Alice");
    /**
     * This method will launch the arguments of the program
     *
     * @param args (String[]) : the arguments of the program
     * @throws IOException : if something goes wrong
     */
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(PORT_NUMBER);
        int i = 0;
        Map<PlayerId, Player> players = new EnumMap<>(PlayerId.class);
        Map<PlayerId, String> playerNames = new EnumMap<>(PlayerId.class);
        MessageManager manager = new MessageManager(players,playerNames);
        for(PlayerId playerId : PlayerId.ALL) {
            Socket messageSocket = server.accept();
            Socket instructionSocket  = server.accept();
            RemotePlayerProxy playerProxy = new RemotePlayerProxy(instructionSocket, messageSocket,manager);
            players.put(playerId, playerProxy);
            playerNames.put(playerId, args.length == 0 ? DEFAULT_NAMES.get(i++): args[i++]);
        }

        new Thread(() -> Game.play(players, playerNames, SortedBag.of(tickets()), new Random())).start();
        for(PlayerId playerId : PlayerId.ALL){
            new Thread(()->players.get(playerId).sendToManager()).start();
        }
    }

}

package ch.epfl.tchu.bonus;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.PlayerId;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.EnumMap;
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
    /**
     * This method will launch the arguments of the program
     *
     * @param args (String[]) : the arguments of the program
     * @throws IOException : if something goes wrong
     */
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(5108);
        ServerSocket managerServer = new ServerSocket(5109);
        Socket managerSocket = new Socket("localhost",5109);
        int i = 0;
        Map<PlayerId, Player> players = new EnumMap<>(PlayerId.class);
        Map<PlayerId, String> playerNames = new EnumMap<>(PlayerId.class);
        for(PlayerId playerId : PlayerId.ALL) {
            Socket instructionSocket  = server.accept();
            Socket messageSocket = server.accept();
            RemotePlayerProxy playerProxy = new RemotePlayerProxy(server.accept(), messageSocket,managerSocket);
            players.put(playerId, playerProxy);
            playerNames.put(playerId, args.length == 0 ? "Charles" : args[i++]);
        }
        MessageManager manager = new MessageManager(players,managerSocket,playerNames);
        new Thread(() -> Game.play(players, playerNames, SortedBag.of(tickets()), new Random())).start();
        new Thread(manager::manage).start();
        for(PlayerId playerId : PlayerId.ALL){
            new Thread(()->players.get(playerId).sendToManager()).start();
        }
    }

}

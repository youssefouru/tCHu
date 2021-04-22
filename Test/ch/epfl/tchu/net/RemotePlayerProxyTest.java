package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

public class RemotePlayerProxyTest{

    public static void main(String[] args) throws IOException {
        System.out.println("Starting server!");

        try(ServerSocket serverSocket = new ServerSocket(5108);
        Socket socket = serverSocket.accept()) {
            Player playerProxy = new RemotePlayerProxy(socket);
            var playerNames = Map.of(PLAYER_1, "Ada",
            PLAYER_2, "Charles");
             playerProxy.initPlayers(PLAYER_1, playerNames);
             playerProxy.receiveInfo("Hola");
             playerProxy.setInitialTicketChoice(SortedBag.of(ChMap.tickets().subList(0,6)));
             playerProxy.nextTurn();
             playerProxy.drawSlot();
             playerProxy.chooseInitialTickets();
             playerProxy.claimedRoute();
             playerProxy.initialClaimCards();



        }

        System.out.println("Server done!");

    }
}

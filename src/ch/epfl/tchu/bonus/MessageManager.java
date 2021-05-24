package ch.epfl.tchu.bonus;

import ch.epfl.tchu.game.PlayerId;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class MessageManager {

    private final Map<PlayerId,RemotePlayerProxy> proxies;
    private final BufferedReader managerReader;
    private final Map<PlayerId,String> playerNames;
    private final static String MESSAGE_FROM = "Message from %s : %s";

    /**
     * Constructor of MessageManager.
     * @param proxies (Map< PlayerId, RemotePlayerProxy >) : The player Proxies.
     * @param socketMessage (Socket) : the socket where all the players writes the messages
     * @param playerNames (Map< PlayerId, String >) : the names of each Player
     */
    public MessageManager(Map<PlayerId, RemotePlayerProxy> proxies, Socket socketMessage, Map<PlayerId, String> playerNames) {
        try {
            this.proxies = proxies;
            this.managerReader = new BufferedReader(new InputStreamReader(socketMessage.getInputStream(), StandardCharsets.US_ASCII));
            this.playerNames = playerNames;
        }catch (IOException ioException){
            throw new UncheckedIOException(ioException);
        }
    }
}

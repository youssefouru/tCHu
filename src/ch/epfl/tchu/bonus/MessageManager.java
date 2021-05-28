package ch.epfl.tchu.bonus;

import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.net.Serdes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Pattern;

public final class MessageManager {

    private final Map<PlayerId,Player> proxies;
    private final Map<PlayerId,String> playerNames;
    private final static String MESSAGE_FROM = "Message from %s : %s";
    private final static String SEPARATION_CHAR = String.valueOf((char)28);

    /**
     * Constructor of MessageManager.
     * @param proxies (Map< PlayerId, RemotePlayerProxy >) : The player Proxies.
     * @param playerNames (Map< PlayerId, String >) : the names of each Player
     */
    public MessageManager(Map<PlayerId, Player> proxies, Map<PlayerId, String> playerNames) {
            this.proxies = proxies;
            this.playerNames = playerNames;
    }



    /**
     *
     * @param messageReceived
     */
    public void manage(String messageReceived){
            int i = 0;
            String[] messageTab = messageReceived.split(Pattern.quote(SEPARATION_CHAR),-1);
            PlayerId from = Serdes.PLAYER_ID_SERDE.deserialize(messageTab[i++]);
            PlayerId to = Serdes.PLAYER_ID_SERDE.deserialize(messageTab[i++]);
            String messageSent = Serdes.STRING_SERDE.deserialize(messageTab[i]);
            proxies.get(to).sendToClient(Serdes.STRING_SERDE.serialize(String.format(MESSAGE_FROM,from,messageSent)));
    }
}

package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.AdvancedPlayer;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.net.Serdes;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * MessageManager : this class will manages the messages
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public final class MessageManager {

    private final static String MESSAGE_FROM = "Message from %s (%s) : %s";
    private final static String SEPARATION_CHAR = String.valueOf((char) 28);
    private final Map<PlayerId, AdvancedPlayer> proxies;
    private final Map<PlayerId, String> playerNames;

    /**
     * Constructor of MessageManager.
     *
     * @param proxies     (Map< PlayerId, RemotePlayerProxy >) : The player Proxies.
     * @param playerNames (Map< PlayerId, String >) : the names of each Player
     */
    public MessageManager(Map<PlayerId, AdvancedPlayer> proxies, Map<PlayerId, String> playerNames) {
        this.proxies = proxies;
        this.playerNames = playerNames;
    }


    /**
     * This method is used to manage messages received by the proxies
     *
     * @param messageReceived (String) : the message received from the client
     */
    public void manage(String messageReceived) {
        int i = 0;
        String[] messageTab = messageReceived.split(Pattern.quote(SEPARATION_CHAR), -1);
        PlayerId from = Serdes.PLAYER_ID_SERDE.deserialize(messageTab[i++]);
        PlayerId to = Serdes.PLAYER_ID_SERDE.deserialize(messageTab[i++]);
        String messageSent = Serdes.STRING_SERDE.deserialize(messageTab[i]);
        if (to == null) {
            for (PlayerId playerId : PlayerId.ALL) {
                if (playerId == from) continue;
                proxies.get(playerId).sendToClient(Serdes.STRING_SERDE.serialize(String.format(MESSAGE_FROM, playerNames.get(from), "public", messageSent)));
            }
            return;
        }
        proxies.get(to).sendToClient(Serdes.STRING_SERDE.serialize(String.format(MESSAGE_FROM, playerNames.get(from), "private", messageSent)));
    }
}

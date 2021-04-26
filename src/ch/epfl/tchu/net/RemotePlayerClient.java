package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.Ticket;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * RemotePlayerClient : The type Remote player client.
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public final class RemotePlayerClient {

    private final Player player;
    private final BufferedWriter writer;
    private final BufferedReader reader;

    /**
     * Constructor of the remotePlayerClient
     *
     * @param player (Player)  : the player represented by this client
     * @param name   (String) : the name of the proxy
     * @param id     (int) : the id of the proxy
     */
    public RemotePlayerClient(Player player, String name, int id) {
        this.player = player;
        try {
            Socket socket = new Socket(name, id);
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), US_ASCII));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), US_ASCII));
        } catch (IOException ioException) {
            throw new UncheckedIOException(ioException);
        }
    }

    private void send(String message) {
        try {
            writer.write(message);
            writer.write("\n");
            writer.flush();
        } catch (IOException ioException) {
            throw new UncheckedIOException(ioException);
        }
    }

    private String receive() {
        try {
            return reader.readLine();
        } catch (IOException ioException) {
            throw new UncheckedIOException(ioException);
        }
    }
    /**
     * this method will receive the instruction from the proxy and will make the player
     */
    public void run() {
        String message;
        while ((message = receive()) != null) {
            int i = 0;
            String[] stringTab = message.split(Pattern.quote(" "), -1);
            MessageId messageReceived = MessageId.valueOf(stringTab[i++]);
            switch (messageReceived) {
                case INIT_PLAYERS:
                    PlayerId playerId = Serdes.PLAYER_ID_SERDE.deserialize(stringTab[i++]);
                    List<String> namesList = Serdes.STRING_LIST_SERDE.deserialize(stringTab[i]);
                    Map<PlayerId, String> playerNames = new HashMap<>();
                    for (int j = 0; j < PlayerId.ALL.size(); ++j) {
                        playerNames.put(PlayerId.ALL.get(j),
                                namesList.get(j));
                    }
                    player.initPlayers(playerId, playerNames);
                    break;

                case RECEIVE_INFO:
                    player.receiveInfo(Serdes.STRING_SERDE.deserialize(stringTab[i]));
                    break;

                case UPDATE_STATE:
                    player.updateState(Serdes.PUBLIC_GAME_STATE_SERDE.deserialize(stringTab[i++]), Serdes.PLAYER_STATE_SERDE.deserialize(stringTab[i]));
                    break;

                case CHOOSE_INITIAL_TICKETS:
                    SortedBag<Ticket> chooseInitialTickets = player.chooseInitialTickets();
                    send(Serdes.TICKET_BAG_SERDE.serialize(chooseInitialTickets));
                    break;

                case SET_INITIAL_TICKETS:
                    player.setInitialTicketChoice(Serdes.TICKET_BAG_SERDE.deserialize(stringTab[i]));
                    break;

                case NEXT_TURN:
                    send(Serdes.TURN_KIND_SERDE.serialize(player.nextTurn()));
                    break;

                case CHOOSE_TICKETS:
                    SortedBag<Ticket> chosenTicket = player.chooseTickets(Serdes.TICKET_BAG_SERDE.deserialize(stringTab[i]));
                    send(Serdes.TICKET_BAG_SERDE.serialize(chosenTicket));
                    break;

                case DRAW_SLOT:
                    send(Serdes.INTEGER_SERDE.serialize(player.drawSlot()));
                    break;

                case ROUTE:
                    send(Serdes.ROUTE_SERDE.serialize(player.claimedRoute()));
                    break;

                case CARDS:
                    send(Serdes.CARD_BAG_SERDE.serialize(player.initialClaimCards()));
                    break;

                case CHOOSE_ADDITIONAL_CARDS:
                    List<SortedBag<Card>> possibleAdditionalCard = Serdes.CARD_BAG_LIST_SERDE.deserialize(stringTab[i]);
                    SortedBag<Card> additionalCard = player.chooseAdditionalCards(possibleAdditionalCard);
                    send(Serdes.CARD_BAG_SERDE.serialize(additionalCard));
                    break;

                default:
                    throw new Error();
            }

        }
        try{
            reader.close();
            writer.close();
        }catch (IOException ioException){
            throw new UncheckedIOException(ioException);
        }
    }




}

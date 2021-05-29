package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

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
    private final static String SEPARATION_CHAR = " ";
    private final static String RETURN_NAME = "\n";
    private final AdvancedPlayer player;
    private final BufferedWriter instructionWriter;
    private final BufferedReader instructionReader, messageReader;

    /**
     * Constructor of the remotePlayerClient
     *
     * @param player (Player)  : the player represented by this client
     * @param name   (String) : the name of the proxy
     * @param id     (int) : the id of the proxy
     */
    public RemotePlayerClient(AdvancedPlayer player, String name, int id, Socket messageSocket) {
        this.player = player;
        try {
            Socket instructionSocket = new Socket(name, id);
            instructionWriter = creatWriter(instructionSocket);
            instructionReader = creatReader(instructionSocket);
            messageReader = creatReader(messageSocket);
        } catch (IOException ioException) {
            throw new UncheckedIOException(ioException);
        }

    }

    private BufferedReader creatReader(Socket socket) {
        try {
            return new BufferedReader(new InputStreamReader(socket.getInputStream(), US_ASCII));
        } catch (IOException ioException) {
            throw new UncheckedIOException(ioException);
        }
    }

    private BufferedWriter creatWriter(Socket socket) {
        try {
            return new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), US_ASCII));
        } catch (IOException ioException) {
            throw new UncheckedIOException(ioException);
        }
    }

    private void sendInstruction(String message) {
        try {
            instructionWriter.write(message);
            instructionWriter.write(RETURN_NAME);
            instructionWriter.flush();
        } catch (IOException ioException) {
            throw new UncheckedIOException(ioException);
        }
    }

    private String receive(BufferedReader reader) {
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
        while ((message = receive(instructionReader)) != null) {
            int i = 0;
            String[] stringTab = message.split(Pattern.quote(SEPARATION_CHAR), -1);
            MessageId messageReceived = MessageId.valueOf(stringTab[i++]);
            switch (messageReceived) {
                case INIT_PLAYERS:
                    PlayerId playerId = Serdes.PLAYER_ID_SERDE.deserialize(stringTab[i++]);
                    List<String> namesList = Serdes.STRING_LIST_SERDE.deserialize(stringTab[i]);
                    Map<PlayerId, String> playerNames = new HashMap<>();
                    PlayerId.ALL.forEach(id -> playerNames.put(PlayerId.ALL.get(id.ordinal()), namesList.get(id.ordinal())));
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
                    sendInstruction(Serdes.TICKET_BAG_SERDE.serialize(chooseInitialTickets));
                    break;

                case SET_INITIAL_TICKETS:
                    player.setInitialTicketChoice(Serdes.TICKET_BAG_SERDE.deserialize(stringTab[i]));
                    break;

                case NEXT_TURN:
                    sendInstruction(Serdes.TURN_KIND_SERDE.serialize(player.nextTurn()));
                    break;

                case CHOOSE_TICKETS:
                    SortedBag<Ticket> chosenTicket = player.chooseTickets(Serdes.TICKET_BAG_SERDE.deserialize(stringTab[i]));
                    sendInstruction(Serdes.TICKET_BAG_SERDE.serialize(chosenTicket));
                    break;

                case DRAW_SLOT:
                    sendInstruction(Serdes.INTEGER_SERDE.serialize(player.drawSlot()));
                    break;

                case ROUTE:
                    sendInstruction(Serdes.ROUTE_SERDE.serialize(player.claimedRoute()));
                    break;

                case CARDS:
                    sendInstruction(Serdes.CARD_BAG_SERDE.serialize(player.initialClaimCards()));

                    break;

                case CHOOSE_ADDITIONAL_CARDS:
                    List<SortedBag<Card>> possibleAdditionalCard = Serdes.CARD_BAG_LIST_SERDE.deserialize(stringTab[i]);
                    SortedBag<Card> additionalCard = player.chooseAdditionalCards(possibleAdditionalCard);
                    sendInstruction(Serdes.CARD_BAG_SERDE.serialize(additionalCard));

                    break;
                case NOTIFY_LONGEST:
                    List<Route> routesInTheLongestTrail = Serdes.ROUTE_LIST_SERDE.deserialize(stringTab[i]);
                    for (Route route : routesInTheLongestTrail) {
                        route.highlight();
                    }
                default:
                    throw new Error();
            }
        }
        try {
            instructionReader.close();
            instructionWriter.close();
        } catch (IOException ioException) {
            throw new UncheckedIOException(ioException);
        }
    }

    /**
     * This method will receive the messages
     */
    public void manageMessages() {
        String messageReceived;
        while ((messageReceived = receive(messageReader)) != null) {
            player.receiveMessage(messageReceived);
        }

    }


}

package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * RemotePlayerProxy : this class represents the proxy
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public final class RemotePlayerProxy implements Player {
    private final BufferedReader reader;
    private final BufferedWriter writer;

    /**
     * Constructor of RemotePlayerProxy
     *
     * @param socket (Socket) : the socket we will use to write and read the data in the server
     * @throws UncheckedIOException  : this error is thrown if
     */
    public RemotePlayerProxy(Socket socket) {
        try{
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), US_ASCII));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),US_ASCII));
        } catch (IOException ioException){
            throw new UncheckedIOException(ioException);
        }
    }

    private void send(MessageId messageId, String... strings)  {
        List<String> messageList = Arrays.stream(strings).collect(Collectors.toCollection(LinkedList::new));
        messageList.add(0,messageId.name());
        String message = String.join(" ", messageList);
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
     * this method writes the serialized message of the init players
     *
     * @param ownId       (PlayerId) : the id of the linked to this proxy
     * @param playerNames (Map< PlayerId, String >) : the map that we can get the name of each player in function of his id
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        send(MessageId.INIT_PLAYERS,
             Serdes.PLAYER_ID_SERDE.serialize(ownId),
             Serdes.STRING_LIST_SERDE.serialize((PlayerId.ALL.stream().
                                                              map(playerNames::get)).
                                                              collect(Collectors.toList())));
    }


    /**
     * this method transmit to the client the info  in parameter
     *
     * @param info (String) : info we want to communicate
     */
    @Override
    public void receiveInfo(String info) {
        send(MessageId.RECEIVE_INFO,
                Serdes.STRING_SERDE.serialize(info));
    }

    /**
     * this method send to the client the state updated to the client
     *
     * @param newState (PublicGameState) : the PublicGameState we want to communicate
     * @param ownState (PlayerState) : own state of the client
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        send(MessageId.UPDATE_STATE,
                Serdes.PUBLIC_GAME_STATE_SERDE.serialize(newState),
                Serdes.PLAYER_STATE_SERDE.serialize(ownState));
    }

    /**
     * this method send to the client the message to chose the initial cards among the tickets in parameter
     *
     * @param tickets (SortedBag<Ticket>) : the tickets initially proposed to the client
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        send(MessageId.SET_INITIAL_TICKETS,
                Serdes.TICKET_BAG_SERDE.serialize(tickets));
    }

    /**
     * this method send to the client the message to chose the the initialTickets and receive the tickets chosen by the player that he returns
     *
     * @return (SortedBag < Ticket >) : the tickets chosen by the the client
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        send(MessageId.CHOOSE_INITIAL_TICKETS);
        return Serdes.TICKET_BAG_SERDE.deserialize(receive());
    }

    /**
     * this method send to the client that he has to choose the next turn et receive the nextTurn chosen by him and returns it
     *
     * @return (TurnKind) : the turnKind chosen by the client
     */
    @Override
    public TurnKind nextTurn() {
        send(MessageId.NEXT_TURN);
        return Serdes.TURN_KIND_SERDE.deserialize(receive());
    }

    /**
     * this method send to the client that he has received the tickets in parameter and receive the cards that he chosen
     *
     * @param options (SortedBag<Ticket>) : the tickets drawn by the client
     * @return (SortedBag < Ticket >) : the tickets that the client has chosen
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        send(MessageId.CHOOSE_TICKETS,
                Serdes.TICKET_BAG_SERDE.serialize(options));

        return Serdes.TICKET_BAG_SERDE.deserialize(receive());
    }

    /**
     * this method send to the client to choose the slot he wants to draw , receives it and returns it
     *
     * @return (int) : the slot that the client has chosen
     */
    @Override
    public int drawSlot() {
        send(MessageId.DRAW_SLOT);
        return Serdes.INTEGER_SERDE.deserialize(receive());
    }

    /**
     * this method send to the client to claim a route he wants to claim , receives it and returns it
     *
     * @return (Route) : the route claimed by the client
     */
    @Override
    public Route claimedRoute() {
        send(MessageId.ROUTE);
        return Serdes.ROUTE_SERDE.deserialize(receive());
    }

    /**
     * this method send to the client to choose the claim  cards he wants to play to claim the route , receives it and returns it
     *
     * @return (SortedBag < Card >) : returns the initial cards that the player chosen
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        send(MessageId.CARDS);
        return Serdes.CARD_BAG_SERDE.deserialize(receive());
    }

    /**
     * this method send to the client the additional cards he wants to play to claim the underground route , receives it and returns it
     *
     * @param options (List< SortedBag< Card > >) : all the additional cards that the client can play
     * @return (SortedBag < Card >) : the additional cards that the player can play to claim the underground route
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        send(MessageId.CHOOSE_ADDITIONAL_CARDS,
                Serdes.CARD_BAG_LIST_SERDE.serialize(options));
        return Serdes.CARD_BAG_SERDE.deserialize(receive());
    }
}

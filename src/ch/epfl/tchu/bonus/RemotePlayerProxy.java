package ch.epfl.tchu.bonus;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import ch.epfl.tchu.net.Serdes;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * RemotePlayerProxy : this class represents the proxy
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public final class RemotePlayerProxy implements Player {
    private final BufferedReader instructionReader, messageReader, managerReader;
    private final BufferedWriter instructionWriter, messageWriter, managerWriter;


    /**
     * Constructor of RemotePlayerProxy
     *
     * @param socket (Socket) : the socket we will use to write and read the data in the server
     */
    public RemotePlayerProxy(Socket socket, Socket clientToProxy, Socket managerToProxy) {
        instructionReader = readerCreator(socket);
        instructionWriter = writerCreator(socket);
        messageReader = readerCreator(clientToProxy);
        messageWriter = writerCreator(clientToProxy);
        managerReader = readerCreator(managerToProxy);
        managerWriter = writerCreator(managerToProxy);

    }

    private static BufferedReader readerCreator(Socket socket) {
        try {
            return new BufferedReader(new InputStreamReader(socket.getInputStream(), US_ASCII));
        } catch (IOException ioException) {
            throw new UncheckedIOException(ioException);
        }
    }

    private static BufferedWriter writerCreator(Socket socket) {
        try {
            return new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), US_ASCII));
        } catch (IOException ioException) {
            throw new UncheckedIOException(ioException);
        }
    }

    private void sendTo(BufferedWriter writer, String message){
        try {
            writer.write(message);
            writer.write("\n");
            writer.flush();
        } catch (IOException ioException) {
            throw new UncheckedIOException(ioException);
        }
    }

    private String receiveFrom(BufferedReader reader){
        try {
            return reader.readLine();
        } catch (IOException ioException) {
            throw new UncheckedIOException(ioException);
        }
    }

    private void sendInstruction(MessageId messageId, String... strings) {
        List<String> messageList = Arrays.stream(strings).collect(Collectors.toCollection(ArrayList::new));
        messageList.add(0, messageId.name());
        String message = String.join(" ", messageList);
        sendTo(instructionWriter,message);
    }

    private String receiveInstruction() {
        return receiveFrom(instructionReader);
    }

    /**
     * this method writes the serialized message of the init players
     *
     * @param ownId       (PlayerId) : the id of the linked to this proxy
     * @param playerNames (Map< PlayerId, String >) : the map that we can get the name of each player in function of his id
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        sendInstruction(MessageId.INIT_PLAYERS,
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
        sendInstruction(MessageId.RECEIVE_INFO,
                Serdes.STRING_SERDE.serialize(info));
    }

    /**
     * this method sendInstruction to the client the state updated to the client
     *
     * @param newState (PublicGameState) : the PublicGameState we want to communicate
     * @param ownState (PlayerState) : own state of the client
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        sendInstruction(MessageId.UPDATE_STATE,
                Serdes.PUBLIC_GAME_STATE_SERDE.serialize(newState),
                Serdes.PLAYER_STATE_SERDE.serialize(ownState));
    }

    /**
     * this method sendInstruction to the client the message to chose the initial cards among the tickets in parameter
     *
     * @param tickets (SortedBag<Ticket>) : the tickets initially proposed to the client
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        sendInstruction(MessageId.SET_INITIAL_TICKETS,
                Serdes.TICKET_BAG_SERDE.serialize(tickets));
    }

    /**
     * this method sendInstruction to the client the message to chose the the initialTickets and receiveInstruction the tickets chosen by the player that he returns
     *
     * @return (SortedBag < Ticket >) : the tickets chosen by the the client
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        sendInstruction(MessageId.CHOOSE_INITIAL_TICKETS);
        return Serdes.TICKET_BAG_SERDE.deserialize(receiveInstruction());
    }

    /**
     * this method sendInstruction to the client that he has to choose the next turn et receiveInstruction the nextTurn chosen by him and returns it
     *
     * @return (TurnKind) : the turnKind chosen by the client
     */
    @Override
    public ch.epfl.tchu.game.Player.TurnKind nextTurn() {
        sendInstruction(MessageId.NEXT_TURN);
        return Serdes.TURN_KIND_SERDE.deserialize(receiveInstruction());
    }

    /**
     * this method sendInstruction to the client that he has received the tickets in parameter and receiveInstruction the cards that he chosen
     *
     * @param options (SortedBag<Ticket>) : the tickets drawn by the client
     * @return (SortedBag < Ticket >) : the tickets that the client has chosen
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        sendInstruction(MessageId.CHOOSE_TICKETS,
                Serdes.TICKET_BAG_SERDE.serialize(options));

        return Serdes.TICKET_BAG_SERDE.deserialize(receiveInstruction());
    }

    /**
     * this method sendInstruction to the client to choose the slot he wants to draw , receives it and returns it
     *
     * @return (int) : the slot that the client has chosen
     */
    @Override
    public int drawSlot() {
        sendInstruction(MessageId.DRAW_SLOT);
        return Serdes.INTEGER_SERDE.deserialize(receiveInstruction());
    }

    /**
     * this method sendInstruction to the client to claim a route he wants to claim , receives it and returns it
     *
     * @return (Route) : the route claimed by the client
     */
    @Override
    public Route claimedRoute() {
        sendInstruction(MessageId.ROUTE);
        return Serdes.ROUTE_SERDE.deserialize(receiveInstruction());
    }

    /**
     * this method sendInstruction to the client to choose the claim  cards he wants to play to claim the route , receives it and returns it
     *
     * @return (SortedBag < Card >) : returns the initial cards that the player chosen
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        sendInstruction(MessageId.CARDS);
        return Serdes.CARD_BAG_SERDE.deserialize(receiveInstruction());
    }

    /**
     * this method sendInstruction to the client the additional cards he wants to play to claim the underground route , receives it and returns it
     *
     * @param options (List< SortedBag< Card > >) : all the additional cards that the client can play
     * @return (SortedBag < Card >) : the additional cards that the player can play to claim the underground route
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        sendInstruction(MessageId.CHOOSE_ADDITIONAL_CARDS,
                Serdes.CARD_BAG_LIST_SERDE.serialize(options));
        return Serdes.CARD_BAG_SERDE.deserialize(receiveInstruction());
    }


    /**
     * This method is used to send a message to the client bound to the player.
     * @param serializedMessage (String) : the serialized message sent from the manager that we want to send tot the client
     */
    public void sendToClient(String serializedMessage){
        sendTo(messageWriter,serializedMessage);
    }

    /**
     * This method is used to verify if a message has been written in the socket of the client and write it in the socket of the manager
     */
    public void sendToManager(){
        String receiveMessage;
        while((receiveMessage=receiveFrom(messageReader)) != null){
            sendTo(managerWriter,receiveMessage);
        }
    }

    /**
     * This method is used to receive a message from a the socket of messages.
     *
     * @return (String) : The message received from the proxy
     */
    @Override
    public String receiveMessage() {
        return null;
    }

    /**
     * This method is used to notify the client that the routes in parameter are in the longest Trail.
     *
     * @param routes (List< Route >) : the routes in the longest trail.
     */
    @Override
    public void notifyLongest(List<Route> routes) {
        sendInstruction(MessageId.NOTIFY_LONGEST,Serdes.ROUTE_LIST_SERDE.serialize(routes));
    }
}

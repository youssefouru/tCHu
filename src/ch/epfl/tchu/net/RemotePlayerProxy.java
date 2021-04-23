package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.US_ASCII;

public class RemotePlayerProxy implements Player {
    Socket socket;

    public RemotePlayerProxy(Socket socket) {

    }

    private void send(MessageId id, List<String> arguments) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), US_ASCII))) {
            writer.write(id.name());

            for (String argument : arguments){
                writer.write(" ");
                writer.write(argument);
            }
            writer.write("\n");
            writer.flush();
        } catch (IOException ioException) {
            throw new UncheckedIOException(ioException);
        }
    }

    private String receive() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), US_ASCII))) {
                return reader.readLine();
                            } catch (IOException ioException) {
                throw new UncheckedIOException(ioException);
            }
    }

    @Override

    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        List<String> list = new ArrayList();
        list.add(Serdes.PLAYER_ID_SERDE.serialize(ownId));
        list.add(Serdes.STRING_LIST_SERDE.serialize(new ArrayList<String>(playerNames.values())));
        send(MessageId.INIT_PLAYERS, list);
    }

    @Override
    public void receiveInfo(String info) {
        send(MessageId.RECEIVE_INFO, List.of(Serdes.STRING_SERDE.serialize(info)));
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        send(MessageId.UPDATE_STATE, List.of(Serdes.PUBLIC_GAME_STATE_SERDE.serialize(newState), Serdes.PLAYER_STATE_SERDE.serialize(ownState)));
            }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        send(MessageId.SET_INITIAL_TICKETS, List.of(Serdes.TICKET_BAG_SERDE.serialize(tickets)));
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        send(MessageId.CHOOSE_INITIAL_TICKETS, null);
        return Serdes.TICKET_BAG_SERDE.deserialize(receive());
    }

    @Override
    public TurnKind nextTurn() {
        send(MessageId.NEXT_TURN, null);
         return Serdes.TURN_KIND_SERDE.deserialize(receive());
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
         send(MessageId.CHOOSE_TICKETS, List.of(Serdes.TICKET_BAG_SERDE.serialize(options)));
         return Serdes.TICKET_BAG_SERDE.deserialize(receive());
    }

    @Override
    public int drawSlot() {
        send(MessageId.DRAW_SLOT, null);
        return Serdes.INTEGER_SERDE.deserialize(receive());
    }

    @Override
    public Route claimedRoute() {
        send(MessageId.ROUTE, null);
        return Serdes.ROUTE_SERDE.deserialize(receive());
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
            send(MessageId.CARDS, null);
        return Serdes.CARD_BAG_SERDE.deserialize(receive());
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        send(MessageId.CHOOSE_ADDITIONAL_CARDS, null);
        return Serdes.CARD_BAG_SERDE.deserialize(receive());
    }


}

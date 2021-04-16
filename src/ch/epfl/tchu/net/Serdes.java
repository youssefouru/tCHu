package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

/**
 * A Serde : this class contains the serdes we will use
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public final class Serdes {
    /**
     * (Serde<Integer>) :this attribute is the serde that will help us to serialize and deserialize an integer
     */
    public final static Serde<Integer> INTEGER_SERDE = Serde.of(String::valueOf, Integer::parseInt);

    /**
     * (Serde<String>) : this attribute is the serde that will help us to serialize and deserialize a string
     */
    public final static Serde<String> STRING_SERDE = Serde.of((string -> Base64.getEncoder().encodeToString(string.getBytes(StandardCharsets.UTF_8))),
                                                              (s2 -> new String(Base64.getDecoder().decode(s2.getBytes(StandardCharsets.UTF_8)))));

    /**
     * (Serde<PlayerId>) : this is the serde used to serialize and deserialize the enums of PlayerId
     */
    public final static Serde<PlayerId> PLAYER_ID_SERDE = Serde.oneOf(PlayerId.ALL);

    /**
     * (Serde<TurnKind>) : this is the serde used to serialize and deserialize the enums of TurnKind
     */
    public final static Serde<Player.TurnKind> TURN_KIND_SERDE = Serde.oneOf(Player.TurnKind.ALL);

    /**
     * (Serde<Card>) : this is the serde used to serialize and deserialize the enums of Card
     */
    public final static Serde<Card> CARD_SERDE = Serde.oneOf(Card.ALL);

    /**
     * (Serde<Route>) : this is the serde used to serialize and deserialize a routes
     */
    public final static Serde<Route> ROUTE_SERDE = Serde.oneOf(ChMap.routes());

    /**
     * (Serde<Ticket>) : this is the serde used to serialize and deserialize a ticket
     */
    public final static Serde<Ticket> TICKET_SERDE = Serde.oneOf(ChMap.tickets());

    /**
     * (Serde<List<String>>) : this is the serde use to serialize and deserialize a list of strings
     */
    public final static Serde<List<String>> STRING_LIST_SERDE = Serde.listOf(STRING_SERDE, ',');

    /**
     * (Serde<List<Card>>) : this is the serde use to serialize and deserialize a list of cards
     */
    public final static Serde<List<Card>> CARD_LIST_SERDE = Serde.listOf(CARD_SERDE, ',');

    /**
     * (Serde<List<Route>>) : this is the serde use to serialize and deserialize a list of routes
     */
    public final static Serde<List<Route>> ROUTE_LIST_SERDE = Serde.listOf(ROUTE_SERDE, ',');

    /**
     * (Serde<SortedBag<Card>>) : this is the serde use to serialize and deserialize a bag of cards
     */
    public final static Serde<SortedBag<Card>> CARD_BAG_SERDE = Serde.bagOf(CARD_SERDE, ',');

    /**
     * (Serde<SortedBag<Ticket>>) : this is the serde use to serialize and deserialize a bag of tickets
     */
    public final static Serde<SortedBag<Ticket>> TICKET_BAG_SERDE = Serde.bagOf(TICKET_SERDE, ',');

    /**
     * (Serde<SortedBag<Route>>) : this is the serde use to serialize and deserialize a list of bags of cards
     */
    public final static Serde<List<SortedBag<Card>>> CARD_BAG_LIST_SERDE = Serde.listOf(CARD_BAG_SERDE, ';');

    /**
     * (Serde<PublicCardState>) : this is the serde used to serialize and deserialize a PublicCardState
     */
    public final static Serde<PublicCardState> PUBLIC_CARD_STATE_SERDE = new Serde<>() {

        @Override
        public String serialize(PublicCardState publicCardState) {
            List<String> list = List.of(CARD_LIST_SERDE.serialize(publicCardState.faceUpCards()),
                                        INTEGER_SERDE.serialize(publicCardState.deckSize()),
                                        INTEGER_SERDE.serialize(publicCardState.discardsSize()));
            return String.join(";", list);
        }

        @Override
        public PublicCardState deserialize(String name) {
            String[] stringTab = name.split(Pattern.quote(";"), -1);
            int i = 0;
            return new PublicCardState(CARD_LIST_SERDE.deserialize(stringTab[i++]),
                                      INTEGER_SERDE.deserialize(stringTab[i++]),
                                      INTEGER_SERDE.deserialize(stringTab[i]));
        }
    };

    /**
     * (Serde<PublicPlayerState>) : this is the serde used to serialize and deserialize a PublicPlayerState
     */
    public final static Serde<PublicPlayerState> PUBLIC_PLAYER_STATE_SERDE = new Serde<>() {
        @Override
        public String serialize(PublicPlayerState state) {
            List<String> list = List.of(INTEGER_SERDE.serialize(state.ticketCount()),
                                        INTEGER_SERDE.serialize(state.cardCount()),
                                        ROUTE_LIST_SERDE.serialize(state.routes()));
            return String.join(";", list);
        }

        @Override
        public PublicPlayerState deserialize(String name) {
            String[] stringTab = name.split(Pattern.quote(";"), -1);
            int i = 0;
            return new PublicPlayerState(INTEGER_SERDE.deserialize(stringTab[i++]),
                                         INTEGER_SERDE.deserialize(stringTab[i++]),
                                         ROUTE_LIST_SERDE.deserialize(stringTab[i]));
        }
    };

    /**
     * (Serde<PlayerState>) : this is the serde used to serialize and deserialize a PlayerState
     */
    public final static Serde<PlayerState> PLAYER_STATE_SERDE = new Serde<>() {
        @Override
        public String serialize(PlayerState playerState) {
            List<String> stringList = List.of(TICKET_BAG_SERDE.serialize(playerState.tickets()),
                                              CARD_BAG_SERDE.serialize(playerState.cards()),
                                              ROUTE_LIST_SERDE.serialize(playerState.routes()));
            return String.join(";", stringList);
        }

        @Override
        public PlayerState deserialize(String name) {
            String[] stringTab = name.split(Pattern.quote(";"), -1);
            int i = 0;
            return new PlayerState(TICKET_BAG_SERDE.deserialize(stringTab[i++]),
                                   CARD_BAG_SERDE.deserialize(stringTab[i++]),
                                   ROUTE_LIST_SERDE.deserialize(stringTab[i]));
        }
    };

    /**
     * (Serde<PublicGameState>) : this is the serde used to serialize and deserialize a PublicGameState
     */
    public final static Serde<PublicGameState> PUBLIC_GAME_STATE_SERDE = new Serde<>() {
        @Override
        public String serialize(PublicGameState publicGameState) {
            List<String> stringList = new ArrayList<>();
            stringList.add(INTEGER_SERDE.serialize(publicGameState.ticketsCount()));
            stringList.add(PUBLIC_CARD_STATE_SERDE.serialize(publicGameState.cardState()));
            stringList.add(PLAYER_ID_SERDE.serialize(publicGameState.currentPlayerId()));
            PlayerId.ALL.forEach(playerId -> stringList.
                                            add(PUBLIC_PLAYER_STATE_SERDE.
                                            serialize(publicGameState.
                                            playerState(playerId))));
            stringList.add(PLAYER_ID_SERDE.serialize(publicGameState.lastPlayer()));
            return String.join(":", stringList);
        }

        @Override
        public PublicGameState deserialize(String name) {
            String[] stringsTab = name.split(Pattern.quote(":"), -1);
            //we create this variable to iterate over every attribute needed to create a publicGameState
            int i = 0;
            Map<PlayerId, PublicPlayerState> map = new HashMap<>();
            int ticketsCount = INTEGER_SERDE.deserialize(stringsTab[i++]);
            PublicCardState cardState = PUBLIC_CARD_STATE_SERDE.deserialize(stringsTab[i++]);
            PlayerId currentPlayerId = PLAYER_ID_SERDE.deserialize(stringsTab[i++]);
            for (PlayerId playerId : PlayerId.ALL) {
                map.put(playerId, PUBLIC_PLAYER_STATE_SERDE.deserialize(stringsTab[i++]));
            }
            PlayerId lastPlayer = PLAYER_ID_SERDE.deserialize(stringsTab[i]);
            return new PublicGameState(ticketsCount, cardState, currentPlayerId, map, lastPlayer);
        }
    };


    private Serdes() {
    }

}
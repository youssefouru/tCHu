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
    private final static char COMPOSITE_SEPARATOR = ';';

    private final static char LIST_SEPARATOR = ',';

    private final static char gameStateSeparator = ':';

    /**
     * (Serde<Integer>) :this attribute is the serde that will help us to serialize and deserialize an integer
     */
    public final static Serde<Integer> INTEGER_SERDE = Serde.of(String::valueOf, Integer::parseInt);
    /**
     * (Serde<String>) : this attribute is the serde that will help us to serialize and deserialize a string
     */
    public final static Serde<String> STRING_SERDE = Serde.of((string -> Base64.getEncoder().encodeToString(string.getBytes(StandardCharsets.UTF_8))),
            (string -> new String(Base64.getDecoder().decode(string.getBytes(StandardCharsets.UTF_8)))));
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
     * (Serde<Route>) : this is the serde used to serialize and deserialize a routeOwner
     */
    public final static Serde<Route> ROUTE_SERDE = Serde.oneOf(ChMap.routes());
    /**
     * (Serde<Ticket>) : this is the serde used to serialize and deserialize a ticket
     */
    public final static Serde<Ticket> TICKET_SERDE = Serde.oneOf(ChMap.tickets());

    /**
     * (Serde<List<String>>) : this is the serde use to serialize and deserialize a list of strings
     */
    public final static Serde<List<String>> STRING_LIST_SERDE = Serde.listOf(STRING_SERDE, LIST_SEPARATOR);
    /**
     * (Serde<List<Card>>) : this is the serde use to serialize and deserialize a list of cards
     */
    public final static Serde<List<Card>> CARD_LIST_SERDE = Serde.listOf(CARD_SERDE, LIST_SEPARATOR);
    /**
     * (Serde<List<Route>>) : this is the serde use to serialize and deserialize a list of routeOwner
     */
    public final static Serde<List<Route>> ROUTE_LIST_SERDE = Serde.listOf(ROUTE_SERDE, LIST_SEPARATOR);
    /**
     * (Serde<SortedBag<Card>>) : this is the serde use to serialize and deserialize a bag of cards
     */
    public final static Serde<SortedBag<Card>> CARD_BAG_SERDE = Serde.bagOf(CARD_SERDE, LIST_SEPARATOR);
    /**
     * (Serde<SortedBag<Ticket>>) : this is the serde use to serialize and deserialize a bag of tickets
     */
    public final static Serde<SortedBag<Ticket>> TICKET_BAG_SERDE = Serde.bagOf(TICKET_SERDE, LIST_SEPARATOR);

    /**
     * (Serde<SortedBag<Route>>) : this is the serde use to serialize and deserialize a list of bags of cards
     */
    public final static Serde<List<SortedBag<Card>>> CARD_BAG_LIST_SERDE = Serde.listOf(CARD_BAG_SERDE, COMPOSITE_SEPARATOR);
    /**
     * (Serde<PublicCardState>) : this is the serde used to serialize and deserialize a PublicCardState
     */
    public final static Serde<PublicCardState> PUBLIC_CARD_STATE_SERDE = new Serde<>() {

        @Override
        public String serialize(PublicCardState publicCardState) {
            return String.join(String.valueOf(COMPOSITE_SEPARATOR),
                    List.of(CARD_LIST_SERDE.serialize(publicCardState.faceUpCards()),
                            INTEGER_SERDE.serialize(publicCardState.deckSize()),
                            INTEGER_SERDE.serialize(publicCardState.discardsSize())));
        }

        @Override
        public PublicCardState deserialize(String name) {
            String[] stringTab = name.split(Pattern.quote(String.valueOf(COMPOSITE_SEPARATOR)), -1);
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
            return String.join(String.valueOf(COMPOSITE_SEPARATOR),
                    List.of(INTEGER_SERDE.serialize(state.ticketCount()),
                            INTEGER_SERDE.serialize(state.cardCount()),
                            ROUTE_LIST_SERDE.serialize(state.routes())));
        }

        @Override
        public PublicPlayerState deserialize(String name) {
            String[] stringTab = name.split(Pattern.quote(String.valueOf(COMPOSITE_SEPARATOR)), -1);
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
            return String.join(String.valueOf(COMPOSITE_SEPARATOR),
                    List.of(TICKET_BAG_SERDE.serialize(playerState.tickets()),
                            CARD_BAG_SERDE.serialize(playerState.cards()),
                            ROUTE_LIST_SERDE.serialize(playerState.routes())));
        }

        @Override
        public PlayerState deserialize(String name) {
            String[] stringTab = name.split(Pattern.quote(String.valueOf(COMPOSITE_SEPARATOR)), -1);
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
            //we add gradually the attributes serialized because we want to extend our game for more that two player
            List<String> stringList = new ArrayList<>();
            stringList.add(INTEGER_SERDE.serialize(publicGameState.ticketsCount()));
            stringList.add(PUBLIC_CARD_STATE_SERDE.serialize(publicGameState.cardState()));
            stringList.add(PLAYER_ID_SERDE.serialize(publicGameState.currentPlayerId()));
            PlayerId.ALL.forEach(playerId -> stringList.
                          add(PUBLIC_PLAYER_STATE_SERDE.
                                                        serialize(publicGameState.playerState(playerId))));
            stringList.add(PLAYER_ID_SERDE.serialize(publicGameState.lastPlayer()));
            return String.join(String.valueOf(gameStateSeparator), stringList);
        }

        @Override
        public PublicGameState deserialize(String name) {
            String[] stringsTab = name.split(Pattern.quote(String.valueOf(gameStateSeparator)), -1);
            //we create this variable to iterate over every attribute needed to create a publicGameState
            int i = 0;

            int ticketsCount = INTEGER_SERDE.deserialize(stringsTab[i++]);
            PublicCardState cardState = PUBLIC_CARD_STATE_SERDE.deserialize(stringsTab[i++]);
            PlayerId currentPlayerId = PLAYER_ID_SERDE.deserialize(stringsTab[i++]);
            Map<PlayerId, PublicPlayerState> map = new EnumMap<>(PlayerId.class);
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

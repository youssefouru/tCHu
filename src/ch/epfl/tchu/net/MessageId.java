package ch.epfl.tchu.net;

import java.util.List;

/**
 * A MessageId : this class represents all of the types of a messages used by a player
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public enum MessageId {
    INIT_PLAYERS,
    RECEIVE_INFO,
    UPDATE_STATE,
    SET_INITIAL_TICKETS,
    CHOOSE_INITIAL_TICKETS,
    NEXT_TURN,
    CHOOSE_TICKETS,
    DRAW_SLOT,
    ROUTE,
    CARDS,
    CHOOSE_ADDITIONAL_CARDS;

    /**
     * COUNT (int) :  represent the number of all the values of this enum type
     */
    public static int COUNT = values().length;

    /**
     * ALL (List<MessageId>) : contains all the element of this enum type
     */
    public static List<MessageId> ALL = List.of(values());

}

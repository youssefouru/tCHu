package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;

/**
 * PlayerId : this enum type represent the playerId of the players in the game
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public enum PlayerId {
    PLAYER_1,
    PLAYER_2,
    PLAYER_3;

    /**
     * ALL (List<PLayerId>) : contains all the element of this enum type
     */
    public final static List<PlayerId> ALL = List.of(PlayerId.values());
    /**
     * COUNT (int) :  represent the number of all the values of this enum type
     */
    public final static int COUNT = PlayerId.values().length;

    /**
     * This method will set the players in the game
     *
     * @param i :the number of players that can play
     * @return (List < PlayerId >) : the players who plays
     */
    public static List<PlayerId> playable(int i) {
        return ALL.subList(0, i);
    }

    /**
     * this method returns the next player who has to play
     *
     * @return the next player
     */
    public PlayerId next() {
        int i = ALL.indexOf(this);
        int newIndex = (i + 1) % COUNT;
        return ALL.get(newIndex);
    }


}

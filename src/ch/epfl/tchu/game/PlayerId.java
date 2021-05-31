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
    PLAYER_2;

    /**
     * ALL (List<PLayerId>) : contains all the element of this enum type
     */
    public final static List<PlayerId> ALL = List.of(PlayerId.values());
    /**
     * COUNT (int) :  represent the number of all the values of this enum type
     */
    public final static int COUNT = PlayerId.values().length;

    /**
     * this method returns the next player who has to play
     *
     * @return the next player
     */
    public PlayerId next() {
        return nextPlayable(ALL);
    }

    /**
     * This method will set the players in the game
     *
     * @param i :the number of players that can play
     * @return (List < PlayerId >) : the players who plays
     */
    public List<PlayerId> playable(int i) {
        return ALL.subList(0, i);
    }

    /**
     * This Method will gives us the next player in the playable list
     * @param playablePlayer (List<PlayerId> ) : the playable players
     * @return (PlayerId) : the next playerId who plays
     * @throws IllegalArgumentException : if this playerId is not in the playable players
     */
    public PlayerId nextPlayable(List<PlayerId> playablePlayer){
        Preconditions.checkArgument(playablePlayer.contains(this));
        int i = playablePlayer.indexOf(this);
        int newIndex = (i + 1) % playablePlayer.size();
        return playablePlayer.get(newIndex);
    }
}

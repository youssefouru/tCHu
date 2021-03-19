package ch.epfl.tchu.game;

import java.util.List;

public enum PlayerId {
    PLAYER_1,
    PLAYER_2;


    /**
     * ALL (List<Card>) : contains all the element of this enum type
     */
    public static List<PlayerId> ALL = List.of(PlayerId.values());
    /**
     * COUNT (int) :  represent the number of all the values of this enum type
     */
    public static int COUNT = PlayerId.values().length;

    /**
     * this method returns the next player who has to play
     *
     * @return the next player
     */
    public PlayerId next(){
        int  i = ALL.indexOf(this);
        int newIndex= (i+1)%COUNT;
        return ALL.get(newIndex);
    }
}

package ch.epfl.tchu.bonus;

import ch.epfl.tchu.Preconditions;

import java.util.List;

public enum SpectatorId {
    SPECTATOR_1,
    SPECTATOR_2,
    SPECTATOR_3,
    SPECTATOR_4,
    SPECTATOR_5,
    SPECTATOR_6,
    SPECTATOR_7;

    /**
     * ALL (List<SpectatorId>) : Contains all the element of this enum type.
     */
    public final static List<SpectatorId> ALL = List.of(values());

    /**
     * COUNT (int) :  represent the number of all the values of this enum type
     */
    public final static int COUNT = ALL.size();

    /**
     * This method gives us the list  of the spectators of the game
     * @param i (int) : the number of the spectators
     * @return (List< SpectatorId >) : the current spectators
     */
    public static List<SpectatorId> currentSpectators(int i){
        Preconditions.checkArgument(i<COUNT);
        return ALL.subList(0,i);
    }

}

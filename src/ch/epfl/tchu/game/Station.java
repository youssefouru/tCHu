package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.Objects;

/**
 * A Station : this class represent a station in the game
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public final class Station {

    private final int id;

    private final String name;

    /**
     * Constructor of Station
     *
     * @param id   (int) : the id of the station
     * @param name (String) : the name of the station
     */
    public Station(int id, String name) {
        Preconditions.checkArgument(id >= 0);
        this.id = id;
        this.name = Objects.requireNonNull(name);

    }

    /**
     * return the identification number of the station
     *
     * @return (int) : the attribute id
     */
    public int id() {
        return id;
    }

    /**
     * return the name of the station
     *
     * @return (String):  the attribute name
     */
    public String name() {
        return this.name;
    }

    /**
     * return the name of the Station
     *
     * @return (String): the name of the station
     */
    @Override
    public String toString() {
        return this.name();
    }

}

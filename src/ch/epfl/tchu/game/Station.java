package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * A Station
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public final class Station {

    private int id;

    private String name;

    /**
     * Constructor of Station
     *
     * @param id (int) : the id of the station
     * @param name (String) : the name of the station
     */
    public Station(int id, String name) {
        Preconditions.checkArgument(id >= 0);
        this.id = id;
        this.name = name;

    }

    /**
     * return the identification number of the station
     *
     * @return id (int) : the attribute id
     */
    public int id() {
        return id;
    }

    /**
     * return the name of the station
     *
     * @return name(String): return the attribute name
     */
    public String name() {
        return this.name;
    }

    /**
     * return the name of the Station
     *
     * @return name(String): return the the return of the function name()
     */
    @Override
    public String toString() {
        return this.name();
    }
}

package ch.epfl.tchu.game;

import ch.epfl.tchu.Precodition;

//Amine Youssef Louis Barinka
public final class Station {
    private int id;
    private String name;

    /**
     * constructor of Station
     *
     * @param id   (int): the id of the station
     * @param name (String) : the name of the station
     */
    public Station(int id, String name) {
        Precodition.checkArgument(id >= 0);
        this.id = id;
        this.name = name;

    }

    /**
     * return the identification number of the station
     *
     * @return id (int) : this int represent the id of the station
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

    @Override
    public String toString() {
        return this.name();
    }

}

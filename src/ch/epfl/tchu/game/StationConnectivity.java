package ch.epfl.tchu.game;

/**
 * StationConnectivity : the interface
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public interface StationConnectivity {

    /**
     * return true if and only if station1 and station2 are connected
     *
     * @param station1 (Station) : the first station
     * @param station2 (Station) : the second station
     * @return (boolean) : returns if the two stations are connected
     */
    boolean connected(Station station1, Station station2);
}

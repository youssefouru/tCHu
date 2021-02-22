package ch.epfl.tchu.game;

public interface StationConnectivity {
    /**
     * return true if and only if station1 and station2 are connected
     * @param station1 (Station)
     * @param station2 (Station)
     * @return aboolean(Boolean)
     */
    boolean connected(Station station1,Station station2);
}

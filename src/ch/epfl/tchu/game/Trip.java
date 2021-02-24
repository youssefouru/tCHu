package ch.epfl.tchu.game;

import ch.epfl.tchu.Precondition;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A Trip
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public final class Trip {
    /**
     * (Station) departure Station
     */
    final private Station from;
    /**
     * (Station) arrival station
     */
    final private Station to;
    /**
     * (int) point of a trip
     */
    final private int points;

    /**
     * constructor of Trip
     *
     * @param from   (Station) : the departure station of the the trip
     * @param to     (Station) : the arrival station of the trip
     * @param points (int) : point corresponding to the trip
     */
    public Trip(Station from, Station to, int points) {
        Precondition.checkArgument(points > 0);
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.points = points;
    }

    /**
     * this method return all the trips from all the stations from the stations of the from list to all the stations of the to list
     *
     * @param from   (List<Station>) :List of all daparture's stations
     * @param to     (List<Station>) : List of all the arrival's stations
     * @param points (int) : point of all the trips
     * @return all (List<Trip>) : list of all the trips
     */
    public static List<Trip> all(List<Station> from, List<Station> to, int points) {
        List<Trip> all = new ArrayList<Trip>();
        for (Station station : from) {
            for (Station stationBis : to) {
                Trip trip = new Trip(station, stationBis, points);
                all.add(trip);
            }
        }
        return all;
    }

    /**
     * this method return the departure Station of the Trip
     *
     * @return from (Station) : retrun the  attribute from
     */
    public Station from() {
        return this.from;
    }

    /**
     * this method return the arrival Station of the Trip
     *
     * @return to (Station) : retrun the  attribute to
     */
    public Station to() {
        return this.to;
    }

    /**
     * this method retrun the points of the trip
     *
     * @return points (int) : retrun the  attribute points
     */
    public int points() {
        return this.points;
    }

    /**
     * this method check if the from and to station based on their connectivity
     *
     * @param connectivity (StationConnectivity) : represent network to which we want to check the connection of the two stations from and to
     * @return points (int) : it returns the points based on the connectivity of the from's station and the to's station
     */
    public int points(StationConnectivity connectivity) {
        return (connectivity.connected(from, to) ? points : -points);
    }
}





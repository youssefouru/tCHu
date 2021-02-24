package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

public class Trip {
    final private Station from;
    final private Station to;
    final private int points;

    public Trip(Station from, Station to, int points) {
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);

        this.points = points;
    }

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

    public Station from() {
        return from;
    }

    public Station to() {
        return to;
    }

    public int points() {
        return points;
    }

    public int points(StationConnectivity connectivity) {
        return (connectivity.connected(from, to) ? points : points * (-1));
    }
}





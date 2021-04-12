package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * A Ticket : this class represents a ticket of a list of trips
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public final class Ticket implements Comparable<Ticket> {


    private final List<Trip> trips;

    private final String text;

    /**
     * Constructor of Ticket
     *
     * @param trips (List<Trip>) : the list of Trips linked to this ticket
     */
    public Ticket(List<Trip> trips) {
        Preconditions.checkArgument(check(trips));
        this.trips = List.copyOf(Objects.requireNonNull(trips));
        text = computeText(this.trips);
    }

    private boolean check(List<Trip> trips){
        if(trips.isEmpty()){
            return false;
        }
        String name = trips.get(0).from().name();
        Stream<Trip> checkStream = trips.stream();
        Predicate<Trip> predicate = (trip -> trip.from().name().equals(name));
       return checkStream.allMatch(predicate);

    }
    /**
     * Constructor of Ticket
     *
     * @param from   (Station) : the departure's station of trip linked to this ticket
     * @param to     (Station) : the arrival's station of the trip linked to this ticket
     * @param points (Station) : the points earned in the trip linked to this ticket
     */
    public Ticket(Station from, Station to, int points) {
        this(List.of(new Trip(from, to, points)));
    }

    /**
     * this method compute and return the text which has been to be written on the ticket based on the trips in parameter
     *
     * @param trips (List<Trip>) : Set of the trips whose ticket we want to print
     * @return text (String)     : the text which has to be written on the ticket corresponding to the trips in parameter
     */
    private static String computeText(List<Trip> trips) {
        Set<String> destinations = new TreeSet<>();
        String departureStation = trips.get(0).from().name();
        String finalText;

        for (Trip trip : trips) {
            String destination = trip.to().name();
            int points = trip.points();
            String dp = String.format("%s (%s)", destination, points);
            destinations.add(dp);
        }
        String rightPart = String.join(", ", destinations);
        if (destinations.size() == 1) {
            return  String.format("%s - %s", departureStation, rightPart);
        } else {
            return String.format("%s - {%s}", departureStation, rightPart);
        }
    }

    /**
     * return the text written on the ticket
     *
     * @return text (String) : the attribute text
     */
    public String text() {
        return text;
    }

    /**
     * this method the number of points the ticket is worth knowing that the connectivity in parameter is that of the player owning the ticket
     *
     * @param connectivity (StationConnectivity) : represent network to which we want to check the connection of the two stations from and to
     * @return points (int) : it returns the points based on the connectivity of the from's station and the to's station
     */
    public int points(StationConnectivity connectivity) {
        int maxPointsEarned = 0;
        int minPoint = trips.get(0).points();
        int totalPoint = 0;
        boolean isConnected = false;

        for (Trip trip : trips) {
            Station from = trip.from();
            Station to = trip.to();
            int points = trip.points();
            if (connectivity.connected(from, to)) {
                isConnected = true;
                if (points > maxPointsEarned) {
                    maxPointsEarned = points;
                }
            }
            if (minPoint > points) {
                minPoint = points;
            }
            if (isConnected) {
                totalPoint = maxPointsEarned;
            } else {
                totalPoint = minPoint * (-1);
            }
        }
        return totalPoint;
    }


    /**
     * this method compare this (Ticket) to that (That) based on the alphabetic order
     *
     * @param ticket (Ticket) : Ticket that we compare this to
     * @return compare (int) : return a number based on the comparison
     */
    @Override
    public int compareTo(Ticket ticket) {
        return text.compareTo(ticket.text());
    }

    /**
     * this method returns the text written on this ticket
     *
     * @return text (Text) : return the same return of the text() method
     */
    @Override
    public String toString() {
        return text();
    }


}
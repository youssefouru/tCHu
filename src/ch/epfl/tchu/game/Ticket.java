package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

/**
 * A ticket
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
        Preconditions.checkArgument(!trips.isEmpty());
        boolean cond = true;
        String name = trips.get(0).from().name();
        for (Trip trip : trips) {
            if (!(trip.from().name()).equals(name)) {
                cond = false;

            }
        }
        Preconditions.checkArgument(cond);
        this.trips = Objects.requireNonNull(trips);
        text = computeText(this.trips);
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
     * this method compute and return the text wich has been to be written on the ticket based on the trips in parameter
     *
     * @param trips (List<Trip>) : Set of the trips whose ticket we want to print
     * @return text (String)     : the text wich has to be written on the ticket corresponding to the trips in parameter
     */
    private static String computeText(List<Trip> trips) {
        TreeSet<String> destinations = new TreeSet<String>();
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
            finalText = String.format("%s - %s", departureStation, rightPart);
        } else {
            finalText = String.format("%s - {%s}", departureStation, rightPart);
        }
        return finalText;
    }

    /**
     * return the text written on the ticket
     *
     * @return text (String) : the attribute text
     */
    public String text() {
        return this.text;
    }

    /**
     * this method the number of points the ticket is worth knowing that the connectivity in paramater is that of the player owning the ticket
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
    public int compareTo(Ticket ticket) {
        int compare = text.compareTo(ticket.text());
        return compare;
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
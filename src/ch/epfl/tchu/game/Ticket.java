package ch.epfl.tchu.game;

import ch.epfl.tchu.Precondition;

import java.util.*;

public class Ticket implements Comparable<Ticket> {
    private final List<Trip> trips;
    private final String text;


    public Ticket(List<Trip> trips) {
        Precondition.checkArgument(!trips.isEmpty());
        this.trips = Objects.requireNonNull(trips);
        text = computeText();
    }

    public Ticket(Station from, Station to, int points) {
        this(Arrays.asList(new Trip(from, to, points)));
    }

    private String computeText() {
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

    public String text() {
        return text;
    }

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

    public int compareTo(Ticket ticket) {
        int compare = text.compareTo(ticket.text());
        return compare;
    }




}
package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * Route : this class represent a route of the game
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public final class Route {
    private final String id;
    private final Station station1;
    private final Station station2;
    private final int length;
    private final Level level;
    private final Color color;


    /**
     * Constructor of Route
     *
     * @param id       (String)  : the id of the Route
     * @param station1 (Station) : the station1 of the Route
     * @param station2 (Station) : the station2 of the Route
     * @param length   (int)     : the length of the Route
     * @param level    (Level)   : the Level of the Route
     * @param color    (Color)   : the Color of the Route
     */
    public Route(String id, Station station1, Station station2, int length, Level level, Color color) {
        Preconditions.checkArgument((station1.id() != station2.id()) && (length >= Constants.MIN_ROUTE_LENGTH && length <= Constants.MAX_ROUTE_LENGTH));
        this.station1 = Objects.requireNonNull(station1);
        this.station2 = Objects.requireNonNull(station2);
        this.id = Objects.requireNonNull(id);
        this.length = length;
        this.level = Objects.requireNonNull(level);
        this.color = color;

    }

    /**
     * this method return the id of the Route
     *
     * @return (String) : the attribute id
     */
    public String id() {
        return this.id;
    }

    /**
     * this method return the station1 of the Route
     *
     * @return (Station) :the attribute station1
     */
    public Station station1() {
        return this.station1;
    }

    /**
     * this method return the station2 of the Route
     *
     * @return (Station) :the attribute station2
     */
    public Station station2() {
        return this.station2;
    }

    /**
     * this method return the length of the Route
     *
     * @return (int) :the attribute length
     */
    public int length() {
        return this.length;
    }

    /**
     * this method return the level of the Route
     *
     * @return (Level) :the attribute level
     */
    public Level level() {
        return this.level;
    }

    /**
     * this method return the color of the Route
     *
     * @return (Color) :the attribute color
     */
    public Color color() {
        return this.color;
    }

    /**
     * this method return the List of the stations of the Route
     *
     * @return (List < Station >) :a list of the two Stations
     */
    public List<Station> stations() {
        return List.of(station1, station2);
    }

    /**
     * this method returns the opposite station to the one in parameter or throw the IllegalArgumentException if the station in parameter isn't the station1 nor the station2
     *
     * @param station (Station)  : the station that we want the opposite
     * @return (Station) : the opposite station of the route to the station in parameter
     */
    public Station stationOpposite(Station station) {
        Preconditions.checkArgument(stations().contains(station));
        return ((station.equals(station1)) ? station2 : station1);
    }


    /**
     * this method returns a list of all the possible claim card that we need to take this route
     *
     * @return (List<SortedBag<Card>>) :  a list of card's sorted bag that we need to take this route
     */
    public List<SortedBag<Card>> possibleClaimCards() {
        List<SortedBag<Card>> possibleClaimCard = new ArrayList<>();
        List<Card> myCards = color() == null ? Card.CARS : List.of(Card.of(color));
        int constant = level() == Level.UNDERGROUND ? length : 0;
        for (int i = 0; i <= constant; ++i) {
            if (i != length) {
                for (Card card : myCards) {
                    SortedBag<Card> bag = SortedBag.of(i, Card.LOCOMOTIVE, length - i, card);
                    possibleClaimCard.add(bag);
                }
            } else {
                SortedBag<Card> bag = SortedBag.of(length, Card.LOCOMOTIVE);
                possibleClaimCard.add(bag);
            }
        }

        return possibleClaimCard;
    }

    /**
     * Additional claim cards count int.
     *
     * @param claimCards (SortedBag<Card>) : the claim cards
     * @param drawnCards (SortedBag<Card>) : the drawn cards
     * @return (int) : number of additional cards which are needed to take a underground route
     */
    public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards) {
        Preconditions.checkArgument((level() == Level.UNDERGROUND) && (drawnCards.size() == Constants.ADDITIONAL_TUNNEL_CARDS));
        int count = 0;
        for (Card card : drawnCards) {
            if (card == Card.LOCOMOTIVE || claimCards.contains(card)) {
                ++count;
            }
        }

        return count;
    }

    /**
     * this method returns the claim points of a route
     *
     * @return (int) : the claim point of a route
     */
    public int claimPoints() {
        return Constants.ROUTE_CLAIM_POINTS.get(length);
    }


    /**
     * Level enum type
     */
    public enum Level {
        OVERGROUND,
        UNDERGROUND
    }


}

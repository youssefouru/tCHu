package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

/**
 * A Route
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
        Preconditions.checkArgument((station1 != station2) && (length >= Constants.MIN_ROUTE_LENGTH && length <= Constants.MAX_ROUTE_LENGTH));
        this.id = id;
        this.length = length;
        this.station1 = Objects.requireNonNull(station1);
        this.station2 = Objects.requireNonNull(station2);
        this.level = level;
        this.color = color;

    }

    /**
     * this method return the id of the Route
     *
     * @return id (int) : the attribute id
     */
    public String id() {
        return this.id;
    }

    /**
     * this method return the station1 of the Route
     *
     * @return station1(Station) :the attribute station1
     */
    public Station station1() {
        return this.station1;
    }

    /**
     * this method return the station2 of the Route
     *
     * @return station2(Station) :the attribute station2
     */
    public Station station2() {

        return this.station2;
    }

    /**
     * this method return the length of the Route
     *
     * @return length(int) :the attribute length
     */
    public int length() {
        return this.length;
    }

    /**
     * this method return the length of the Route
     *
     * @return length(int) :the attribute length
     */
    public Level level() {
        return this.level;
    }

    /**
     * this method return the color of the Route
     *
     * @return color(Color) :the attribute color
     */
    public Color color() {
        return this.color;
    }

    /**
     * this method return the List of the stations of the Route
     *
     * @return stations(List < Station >) :a list of the two Stations
     */
    public List<Station> stations() {
        return List.of(station1, station2);
    }

    /**
     * this method returns the opposite station to the one in parameter or throw the IllegalArgumentException if the station in parameter isn't the station1 nor the station2
     *
     * @param station (Station)  : the station that we want the opposite
     * @return station (Station) : the opposite station to the station in parameter
     */
    public Station stationOpposite(Station station) {
        if (!stations().contains(station)) {
            throw new IllegalArgumentException();
        }
        if (station.equals(station1())) {
            return station2();
        } else {
            return station1();
        }
    }


    /**
     * this method returns a list of all the possible claim card that we need to take this route
     *
     * @return a list (List<SortedBag<Card>>) :  a list of card's sorted bag that we need to take this route
     */
    public List<SortedBag<Card>> possibleClaimCards() {
        List<SortedBag<Card>> myList = new ArrayList<SortedBag<Card>>();
        //this list contains all the cards if the color of the route is null or just the of the color of the route if it's not null
        List<Card> myCards = color() == null ? Card.CARS :List.of(Card.of(color()));
        //this constant depends on the road level if the road is overground is 0 if the road is underground it is 0
        int constante = level() == Level.UNDERGROUND ?1 : 0;
        //the constant constante is intended to give us an indication about how much iterations we will have to do
        //if the level is Overground there is no iterations and if it is underground it does lenght + 1 iteration
        for(int i = 0 ; i<=length*constante;++i) {
            if(i != length) {
                for (Card card : myCards) {
                    SortedBag<Card> bag = SortedBag.of(i, Card.LOCOMOTIVE, length - i, card);
                    myList.add(bag);
                }
            }else{
                SortedBag<Card> bag = SortedBag.of(length, Card.LOCOMOTIVE);
                myList.add(bag);
            }
        }

        return myList;
    }

    /**
     * Additional claim cards count int.
     *
     * @param claimCards the claim cards
     * @param drawnCards the drawn cards
     * @return count(int) : number of additional cards wich are needed to take a underground route
     */
    int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards) {
        Preconditions.checkArgument((this.level() == Level.UNDERGROUND) && (drawnCards.size() == 3));
        int count = 0;
        for(Card card : drawnCards){
            if(card == Card.LOCOMOTIVE || claimCards.contains(card)){
                ++count;
            }
        }

        return count;
    }



    /**
     * Level enum type
     */
    public enum Level {
        OVERGROUND,
        UNDERGROUND;
    }


    /**
     * Claim points int.
     *
     * @return the int
     */
    public int claimPoints(){
        return Constants.ROUTE_CLAIM_POINTS.get(length);
    }

    public static Station findCommonStation(Route route1, Route route2) {
        Station commonStation = new Station(null);
        boolean stationInCommon = true;
        if (route2.stations().contains(route1.station1)) {
            commonStation = route1.station1;
        } else if (route2.stations().contains(route1.station2)) {
            commonStation = route1.station2;
        } else {
            stationInCommon = false;
        }
        Preconditions.checkArgument(stationInCommon);
        return commonStation;
    }

}

package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;

/**
 * PublicPlayerState : this class represents the public part of a playerState
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public class PublicPlayerState {
    private final List<Route> routes;
    private final int ticketCount;
    private final int cardCount;
    private final int numberOfWagon;
    private final int claimPoints;

    /**
     * Constructor of PublicPlayerState
     *
     * @param ticketCount (int) : number of tickets that the player has
     * @param cardCount   (int) : number of card that the player has
     * @param routes      (List<Route>) : list of routeOwner that the player has
     */
    public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes) {
        Preconditions.checkArgument(ticketCount >= 0 && cardCount >= 0);
        this.routes = List.copyOf(routes);
        this.ticketCount = ticketCount;
        this.cardCount = cardCount;
        this.numberOfWagon = computeWagon(routes);
        claimPoints  = computePoints(routes);
    }

    private static int computeWagon(List<Route> routes) {
        int i = 0;
        for (Route route : routes) {
            i += route.length();
        }
        return Constants.INITIAL_CAR_COUNT - i;
    }

    /**
     * this method returns the number of cards of the player
     *
     * @return (int) : the number of tickets of the player
     */
    public int ticketCount() {
        return ticketCount;
    }

    /**
     * this method returns the number of cards of the player
     *
     * @return (int) : the number of cards of the player
     */
    public int cardCount() {
        return cardCount;
    }

    /**
     * this method returns the list of routeOwner that the player has
     *
     * @return (int) : the list routeOwner
     */
    public List<Route> routes() {
        return routes;
    }

    /**
     * this method returns the number of wagon left
     *
     * @return (int) : returns the attribute numberOfWagon
     */
    public int carCount() {
        return numberOfWagon;
    }

    /**
     * this method compute the total number of points claimed by the player thanks to the routeOwner he has
     *
     * @return (int) : returns the number of points wins by the player thanks to the routeOwner he has
     */
    public int claimPoints() {
       return claimPoints;
    }

    private static int computePoints(List<Route> routes){
        int i = 0;
        for (Route route : routes) {
            i += route.claimPoints();
        }
        return i;
    }


}

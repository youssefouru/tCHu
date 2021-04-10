package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;

/**
 * A PublicPlayerState
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public class PublicPlayerState {
    private final List<Route> routes;
    private final int ticketCount;
    private final int cardCount;
    private final int numberOfWagon;

    /**
     * Constructor of PublicPlayerState
     *
     * @param ticketCount (int) : number of tickets that the player has
     * @param cardCount   (int) : number of card that the player has
     * @param routes      (List<Route>) : list of routes that the player has
     */
    public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes) {
        Preconditions.checkArgument(ticketCount >= 0 && cardCount >= 0);
        this.routes = List.copyOf(routes);
        this.ticketCount = ticketCount;
        this.cardCount = cardCount;
        this.numberOfWagon = computeWagon(routes);
    }

    private static int computeWagon(List<Route> routes) {
        int i = 0;
        for (Route route : routes) {
            i += route.length();
        }
        return 40 - i;
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
     * this method returns the list of routes that the player has
     *
     * @return (int) : the list routes
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
     * this method compute the total number of points claimed by the player thanks to the routes he has
     *
     * @return (int) : returns the number of points wins by the player thanks to the routes he has
     */
    public int claimPoints() {
        int i = 0;
        for (Route route : routes) {
            i += route.claimPoints();
        }
        return i;
    }


}

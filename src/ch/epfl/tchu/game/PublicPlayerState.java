package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;

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
        Preconditions.checkArgument(ticketCount >= 0 && cardCount>=0);
        this.routes = List.copyOf(routes);
        this.ticketCount = ticketCount;
        this.cardCount = cardCount;
        this.numberOfWagon = computeWagon();
    }

    /**
     * this method returns the number of cards of the player
     *
     * @return the number of tickets of the player
     */
    public int ticketCount() {
        return ticketCount;
    }

    /**
     * this method returns the number of cards of the player
     *
     * @return the number of cards of the player
     */
    public int cardCount() {
        return cardCount;
    }

    /**
     * this method returns the list of routes that the player has
     *
     * @return the list routes
     */
    public List<Route> routes() {
        return List.copyOf(routes);
    }

    /**
     * this method returns the number of wagon left
     *
     * @return returns the attribute numberOfWagon
     */
    public int carCount(){
        return numberOfWagon;
    }

    /**
     * this method returns the number of points claimed
     *
     * @return returns the number of points of the player
     */
    public int claimPoints(){
        int i = 0;
        for(Route route : routes){
            i += route.claimPoints();
        }
        return i;
    }

    private  int computeWagon(){
        int i = 0;
        for(Route route : routes){
            i+= route.length();
        }
        return 40- i;
    }


}

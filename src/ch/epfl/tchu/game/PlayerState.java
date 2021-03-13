package ch.epfl.tchu.game;

import java.util.List;

public class PlayerState extends PublicPlayerState{
    /**
     * Constructor of PlayerState
     *
     * @param ticketCount (int) : number of tickets that the player has
     * @param cardCount   (int) : number of card that the player has
     * @param routes      (List<Route>) : list of routes that the player has
     */
    public PlayerState(int ticketCount, int cardCount, List<Route> routes) {
        super(ticketCount, cardCount, routes);
    }

}

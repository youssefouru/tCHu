package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * A PublicGameState : this class represents the public part of a gameState
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public class PublicGameState {
    private final static int NUMBER_OF_CARDS_DREW = 2;
    private final int ticketsCount;
    private final PublicCardState cardState;
    private final PlayerId currentPlayerId;
    private final Map<PlayerId, PublicPlayerState> playerState;
    private final PlayerId lastPlayer;

    /**
     * Constructor of PublicGameState
     *
     * @param ticketsCount    (int) : number of tickets
     * @param cardState       (PublicCardState) : the current CardState
     * @param currentPlayerId (PlayerId) : the Id of the current player Id
     * @param playerState     (Map<PlayerId, PublicPlayerState>): the map which will give us the player state of each player based on id of the player
     * @param lastPlayer      (PlayerId) : the Id of the last Player
     */
    public PublicGameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId, Map<PlayerId,? extends PublicPlayerState> playerState, PlayerId lastPlayer) {
        Preconditions.checkArgument(playerState.size() == PlayerId.COUNT && ticketsCount >= 0);
        this.ticketsCount = ticketsCount;
        this.cardState = Objects.requireNonNull(cardState);
        this.currentPlayerId = Objects.requireNonNull(currentPlayerId);
        this.playerState = Map.copyOf(playerState);
        this.lastPlayer = lastPlayer;
    }

    /**
     * getter of tickets count
     *
     * @return (int) : the attribute ticketsCount
     */
    public int ticketsCount() {
        return this.ticketsCount;
    }

    /**
     * this method tells if it's possible to draw tickets
     *
     * @return (boolean) :  returns true if the number of tickets is not equals to 0
     */
    public boolean canDrawTickets() {
        return ticketsCount > 0;
    }

    /**
     * this method tells if the player can draw the cards
     *
     * @return (boolean) : returns if the player can draw a card or not
     */
    public boolean canDrawCards() {
        return cardState.deckSize() + cardState.discardsSize() >= Constants.ADDITIONAL_TUNNEL_CARDS + NUMBER_OF_CARDS_DREW;
    }

    /**
     * this method returns the public part the attribute cardState
     *
     * @return (PublicCardState) : public part of the cardState
     */
    public PublicCardState cardState() {
        return cardState;
    }


    /**
     * this method returns the ID of the current player
     *
     * @return (PlayerId) : returns the ID of the current player
     */
    public PlayerId currentPlayerId() {
        return currentPlayerId;
    }

    /**
     * this method returns the public part of the playerState of the player with Id in parameter
     *
     * @param playerId (PlayerId) : the id of the player that we want to know the public state
     * @return (PublicPlayerState) : the public player state of the player with the id in parameter
     */
    public PublicPlayerState playerState(PlayerId playerId) {
        return playerState.get(playerId);
    }

    /**
     * returns the public part of the playerState of the current player
     *
     * @return (PublicPlayerState) : the public player state of the current player
     */
    public PublicPlayerState currentPlayerState() {
        return playerState(currentPlayerId);
    }

    /**
     * returns all the routeOwner that has been claimed by one of the two players
     *
     * @return (List < Route >) : the list of the routeOwner that has been claimed
     */
    public List<Route> claimedRoutes() {
        List<Route> routes = new ArrayList<>();
        PlayerId.ALL.forEach(playerId -> routes.addAll(playerState(playerId).routes()));
        return routes;
    }

    /**
     * returns the id of the last player
     *
     * @return (PlayerId) : the id of the last player who  player (can be null)
     */
    public PlayerId lastPlayer() {
        return this.lastPlayer;
    }

}

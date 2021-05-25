package ch.epfl.tchu.bonus;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.util.List;
import java.util.Map;

/**
 * Player : this class is implemented by all the players, it has most of the methods that a player to use during the game
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public interface Player {
    /**
     * this method communicate to the player his own ID and the name of the other player
     *
     * @param ownId       (PlayerId) : id of the player
     * @param playerNames (Map<PlayerId, String>) : the names of the players of the game
     */
    void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames);

    /**
     * this method is used to communicate the info in parameter
     *
     * @param info (String) : info we want to communicate
     */
    void receiveInfo(String info);

    /**
     * this methode inform the player of the new state of the game and his own state
     *
     * @param newState (PublicGameState) : the PublicGameState we want to communicate
     * @param ownState (PlayerState) : own state of the player
     */
    void updateState(PublicGameState newState, PlayerState ownState);

    /**
     * this methode tells the player the tickets which were distributed to the player
     *
     * @param tickets (SortedBag<Ticket>) : the tickets which were distributed to the player
     */
    void setInitialTicketChoice(SortedBag<Ticket> tickets);


    /**
     * this method ask the player which tickets from the first tickets which were distributed to the player initially
     *
     * @return (SortedBag < Ticket >) : the tickets chosen by the player
     */
    SortedBag<Ticket> chooseInitialTickets();

    /**
     * this method is called at the beginning of each tours and tells to the player which action he wants to do
     *
     * @return (TurnKind) : the turn kind the player has chosen
     */
    ch.epfl.tchu.game.Player.TurnKind nextTurn();

    /**
     * this method is called when a player has to choose tickets from the options
     *
     * @param options (SortedBag<Ticket>) : the tickets drawn by the player
     * @return (SortedBag < Ticket >) : the tickets that the player has chosen
     */
    SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options);

    /**
     * this method is called when a player decide to draw a face up card and returns the index of the card he wants to draw
     *
     * @return (int) : index of the card the player wants to draw
     */
    int drawSlot();

    /**
     * this method is called when a player decide or try to claim a route
     *
     * @return (Route) : returns the route that the player decided to claim
     */
    Route claimedRoute();

    /**
     * this method is called when decide or try to claim a route
     *
     * @return (SortedBag < Card >) : returns the cards used to claim the route
     */
    SortedBag<Card> initialClaimCards();

    /**
     * this method is called when a player try to claim a route and give the choice of the additional Cards that he can play
     *
     * @param options (List<SortedBag<Card>>) : all the additional cards that he can play
     * @return (SortedBag < Card >) : all the possible cards that the player can use to claim a route
     */
    SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options);

    /**
     * This method is used to send a message to the client bound to the player.
     *
     * @param serializedMessage (String) : the serialized message sent from the manager that we want to send tot the client
     */
    void sendToClient(String serializedMessage);

    /**
     * This method is used to verify if a message has been written in the socket of the client and write it in the socket of the manager
     */
    void sendToManager();

    /**
     * This method is used to receive a message from a the socket of messages.
     *
     * @return (String) : The message received from the proxy
     */
    String receiveMessage();

    /**
     * This method is used to notify the client that the routes in parameter are in the longest Trail.
     *
     * @param routes (List< Route >) : the routes in the longest trail.
     */
    void notifyLongest(List<Route> routes);

    /**
     * A TurnKind
     */
    enum TurnKind {
        DRAW_TICKETS,
        DRAW_CARDS,
        CLAIM_ROUTE;


        /**
         * ALL (List<TurnKind>) : contains all the element of this enum type
         */
        public static List<TurnKind> ALL = List.of(TurnKind.values());
    }
}

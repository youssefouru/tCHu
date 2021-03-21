package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.List;
import java.util.Map;

/**
 * A Player
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public interface Player {
    /**
     * this method communicate to the player his own ID and the name of the other player
     *
     * @param ownId       (PlayerId) :
     * @param playerNames (Map<PlayerId, String>) :
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
     * this method is called when a player decide to draw a face up card and returns the index of card he want to draw
     *
     * @return (int) : index of the card he want to draw
     */
    int drawSlot();

    /**
     * @return
     */
    Route claimedRoute();

    /**
     * @return
     */
    SortedBag<Card> initialClaimCards();

    /**
     * this method
     *
     * @param options (List<SortedBag<Card>>) :
     * @return
     */
    SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options);

    /**
     * A TurnKind
     */
    enum TurnKind {
        DRAW_TICKETS,
        DRAW_CARDS,
        CLAIM_ROUTES;


        /**
         * ALL (List<TurnKind>) : contains all the element of this enum type
         */
        public static List<TurnKind> ALL = List.of(TurnKind.values());
    }
}

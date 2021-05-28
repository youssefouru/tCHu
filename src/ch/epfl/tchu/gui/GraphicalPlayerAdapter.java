package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import static ch.epfl.tchu.gui.ActionHandlers.*;
import static javafx.application.Platform.runLater;

/**
 * GraphicalPlayerAdapter :This class is the adapter of the graphical Player
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public final class GraphicalPlayerAdapter implements Player {

    private final BlockingQueue<GraphicalPlayer> playerQueue = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<SortedBag<Ticket>> ticketChoice = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<SortedBag<Card>> additionalCardQueue = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<SortedBag<Card>> initialClaimCard = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<Integer> slotQueue = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<Route> routeQueue = new ArrayBlockingQueue<>(1);
    private GraphicalPlayer graphicalPlayer;


    private static <C> C taker(BlockingQueue<C> queue) {
        try {
            return queue.take();
        } catch (InterruptedException interruptedException) {
            throw new Error();
        }
    }

    /**
     * This method communicate to the player his own ID and the name of the other player.
     *
     * @param ownId       (PlayerId) : The id of this player.
     * @param playerNames (Map<PlayerId, String>) : The names of the players of the game.
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        runLater(() -> playerQueue.add(new GraphicalPlayer(ownId, playerNames)));
        graphicalPlayer = taker(playerQueue);

    }

    /**
     * This method is used to communicate the info in parameter.
     *
     * @param info (String) : The info we want to communicate.
     */
    @Override
    public void receiveInfo(String info) {
        runLater(() -> graphicalPlayer.receiveInfo(info));
    }

    /**
     * This methode inform the player of the new state of the game and his own state.
     *
     * @param newState (PublicGameState) : The PublicGameState we want to communicate.
     * @param ownState (PlayerState) : The state of the player.
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        runLater(() -> graphicalPlayer.setState(newState, ownState));
    }

    /**
     * This methode tells the player the tickets which were distributed to the player.
     *
     * @param tickets (SortedBag<Ticket>) : The tickets which were distributed to the player.
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        runLater(() -> graphicalPlayer.chooseTickets(tickets, ticketChoice::add));
    }

    /**
     * this method ask the player which tickets from the first tickets which were distributed to the player initially
     *
     * @return (SortedBag < Ticket >) : the tickets chosen by the player
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        return taker(ticketChoice);
    }

    /**
     * this method is called at the beginning of each tours and tells to the player which action he wants to do
     *
     * @return (TurnKind) : the turn kind the player has chosen
     */
    @Override
    public TurnKind nextTurn() {
        BlockingQueue<TurnKind> turnKindBlockingQueue = new LinkedBlockingDeque<>();
        DrawCardHandler cardHandler = (c) -> {
            turnKindBlockingQueue.add(TurnKind.DRAW_CARDS);
            slotQueue.add(c);
        };
        ClaimRouteHandler routeHandler = (route, cards) -> {
            turnKindBlockingQueue.add(TurnKind.CLAIM_ROUTE);
            routeQueue.add(route);
            initialClaimCard.add(cards);
        };
        DrawTicketsHandler ticketsHandler = () ->
                turnKindBlockingQueue.add(TurnKind.DRAW_TICKETS);
        runLater(() -> graphicalPlayer.startTurn(ticketsHandler, cardHandler, routeHandler));
        return taker(turnKindBlockingQueue);
    }

    /**
     * this method is called when a player has to choose tickets from the options
     *
     * @param options (SortedBag<Ticket>) : the tickets drawn by the player
     * @return (SortedBag < Ticket >) : the tickets that the player has chosen
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        BlockingQueue<SortedBag<Ticket>> ticketsQueue = new ArrayBlockingQueue<>(1);
        runLater(() -> graphicalPlayer.chooseTickets(options, ticketsQueue::add));
        return taker(ticketsQueue);
    }

    /**
     * this method is called when a player decide to draw a face up card and returns the index of the card he wants to draw
     *
     * @return (int) : index of the card the player wants to draw
     */
    @Override
    public int drawSlot() {
        if (slotQueue.isEmpty()) {
            runLater(() -> graphicalPlayer.drawCard(slotQueue::add));
        }
        return taker(slotQueue);
    }

    /**
     * this method is called when a player decide or try to claim a route
     *
     * @return (Route) : returns the route that the player decided to claim
     */
    @Override
    public Route claimedRoute() {
        return taker(routeQueue);
    }

    /**
     * this method is called when decide or try to claim a route
     *
     * @return (SortedBag < Card >) : returns the cards used to claim the route
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        return taker(initialClaimCard);
    }

    /**
     * this method is called when a player try to claim a route and give the choice of the additional Cards that he can play
     *
     * @param options (List<SortedBag<Card>>) : all the additional cards that he can play
     * @return (SortedBag < Card >) : all the possible cards that the player can use to claim a route
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        runLater(() -> graphicalPlayer.chooseAdditionalCards(options, additionalCardQueue::add));
        return taker(additionalCardQueue);
    }
}

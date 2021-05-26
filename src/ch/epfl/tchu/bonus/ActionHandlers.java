package ch.epfl.tchu.bonus;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

/**
 * ActionHandlers : this interface is composed of different handlers
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public interface ActionHandlers {


    /**
     * This functional interface is used by the players to draw the tickets
     */
    @FunctionalInterface
    interface DrawTicketsHandler {
        /**
         * This method is called when the player want to draw a ticket.
         */
        void onDrawTickets();
    }

    /**
     * This functional interface is used by the players to draw cards.
     */
    @FunctionalInterface
    interface DrawCardHandler {
        /**
         * This method is called when the player want to draw a card.
         *
         * @param slot (int) : The card the player wants to draw a card.
         */
        void onDrawCard(int slot);
    }

    /**
     * This functional Interface is used by the players to claim the routes.
     */
    @FunctionalInterface
    interface ClaimRouteHandler {
        /**
         * This method is called when the player wants to claim a route with a sortedBag of cards.
         *
         * @param route (Route) : The route the player wants to claim.
         * @param cards (SortedBag< Card >) : The cards used to claim the route.
         */
        void onClaimRoute(Route route, SortedBag<Card> cards);
    }

    /**
     * This functional interface is used by the players to choose the tickets.
     */
    @FunctionalInterface
    interface ChooseTicketsHandler {
        /**
         * This method is called when the player wants to choose which tickets he wants to keep.
         *
         * @param tickets (SortedBag< Ticket >) : Tickets that the player wants to keep.
         */
        void onChooseTickets(SortedBag<Ticket> tickets);
    }

    /**
     * This functional interface will be used by the player to choose the cards.
     */
    @FunctionalInterface
    interface ChooseCardsHandler {
        /**
         * This method is called when the player wants to play additional cards to claim an underground route.
         *
         * @param cards (SortedBag< Card >) : The additional cards the player wants to play to claim an underground route.
         */
        void onChooseCards(SortedBag<Card> cards);
    }

    @FunctionalInterface
    interface MessageSender {
        /**
         * This method is used to send messages to a player of the game
         *
         * @param message (String) : the message Sent
         * @param playerId (PlayerId) : the id of the player we want him to receive the message
         */
        void onSendedMessage(String message, PlayerId playerId);
    }
}

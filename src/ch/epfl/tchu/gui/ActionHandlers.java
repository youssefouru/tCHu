package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

/**
 * ActionHandlers : this interface is composed of different handlers
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public interface ActionHandlers {


    
    @FunctionalInterface
    interface DrawTicketsHandler{
        /**
         * this method is called when the player want to draw a ticket
         */
        void onDrawTickets();
    }

    @FunctionalInterface
    interface DrawCardHandler{
        /**
         * this method is called when the player want to draw a card
         *
         * @param slot (int) : the card the player wants to draw a card
         */
        void onDrawCard(int slot);
    }

    @FunctionalInterface
    interface ClaimRouteHandler{
        /**
         * this method is called when the player wants to claim a route with a sortedBag of cards
         *
         * @param route (Route) : the route the player wants to claim
         * @param cards (SortedBag< Card >) : the cards used to claim the route
         */
        void onClaimRoute(Route route, SortedBag<Card> cards);
    }

    @FunctionalInterface
    interface ChooseTicketsHandler{
        /**
         * this method is called when the player wants to choose which tickets he wants to keep
         *
         * @param tickets (SortedBag< Ticket >) : tickets that the player wants to keep
         */
        void onChooseTickets(SortedBag<Ticket> tickets);
    }

    @FunctionalInterface
    interface ChooseCardsHandler{
        /**
         *this method is called when the player wants to play additional cards to claim an underground route
         *
         * @param cards (SortedBag< Card >) : the additional cards the player wants to play to claim an underground route
         */
        void onChooseCards(SortedBag<Card> cards);
    }
}

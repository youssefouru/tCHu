package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;


import java.util.Random;

public class GameStateTest2 {

    @Test
    void initialTest() {
        Random rng = new Random();
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets().subList(0, 10));
        GameState gameState = GameState.initial(tickets, rng);
        assertEquals(null, gameState.lastPlayer());
        assertEquals(Constants.ALL_CARDS.size() - 8 - 5, gameState.cardState().deckSize());
        assertEquals(5, gameState.cardState().faceUpCards().size());
        assertEquals(4, gameState.currentPlayerState().cards().size());
        assertEquals(gameState.currentPlayerState(), gameState.playerState(gameState.currentPlayerId()));
    }

    @Test
    void topTicketWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class, () -> {
            Random rng = new Random();
            SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets().subList(0, 10));
            GameState gameState = GameState.initial(tickets, rng);

            gameState.topTickets(11);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            Random rng = new Random();
            SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets().subList(0, 10));
            GameState gameState = GameState.initial(tickets, rng);

            gameState.topTickets(-1);
        });
    }

    @Test
    void topTicketsTest() {
        Random rng = new Random();
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets().subList(0, 10));
        GameState gameState = GameState.initial(tickets, rng);
        assertEquals(tickets, gameState.topTickets(10));

        SortedBag<Ticket> tickets2 = SortedBag.of(ChMap.tickets().subList(0, 20));
        GameState gameState2 = GameState.initial(tickets2, rng);
        assertEquals(tickets2, gameState2.topTickets(20));
    }

    @Test
    void withoutTopTicketWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class, () -> {
            Random rng = new Random();
            SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets().subList(0, 10));
            GameState gameState = GameState.initial(tickets, rng);
            gameState.withoutTopTickets(-1);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            Random rng = new Random();
            SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets().subList(0, 10));
            GameState gameState = GameState.initial(tickets, rng);
            gameState.withoutTopTickets(11);
        });

    }

    @Test
    void withoutTopTicketsTest() {
        Random rng = new Random();
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets().subList(0, 30));
        GameState gameState = GameState.initial(tickets, rng);

        assertEquals(tickets.difference(gameState.topTickets(10)), gameState.withoutTopTickets(10).topTickets(tickets.size() - 10));
        assertEquals(tickets.difference(gameState.topTickets(2)), gameState.withoutTopTickets(2).topTickets(tickets.size() - 2));
        assertEquals(tickets.difference(gameState.topTickets(15)), gameState.withoutTopTickets(15).topTickets(tickets.size() - 15));
    }

    @Test
    void topCardWithTest() {
        Random rng = new Random();
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets().subList(0, 30));
        GameState gameState = GameState.initial(tickets, rng);

        // idk comment tester mais dans withoutTopCardTest ça marchait quand j'avais ajouter les getters etc
    }

    @Test
    void withoutTopCardTest() {
        Random rng = new Random(){
          @Override
          public int nextInt(int i){
              return i-1;
          }
        };
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets().subList(0, 30));
        GameState gameState = GameState.initial(tickets, rng);
        Card gs = gameState.withoutTopCard().topCard();
        assertEquals(gameState.topCard(), gs);
    }

    @Test
    void withMoreDiscardedCardsTest() {
        Random rng = new Random();
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets().subList(0, 30));
        GameState gameState = GameState.initial(tickets, rng);

        GameState gs = gameState.withMoreDiscardedCards(SortedBag.of(4, Card.LOCOMOTIVE, 1, Card.ORANGE));

        //non accessible normalement (il faut ajouter getter)
//        System.out.println(gs.getCardState().getDiscards());
    }

    @Test
    void withCardsDeckRecreatedIfNeeded() {
        Random rng = new Random();
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets().subList(0, 30));
        GameState gameState = GameState.initial(tickets, rng);

        assertEquals(gameState, gameState.withCardsDeckRecreatedIfNeeded(rng));
    }
}

package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class DeckTest {
   @Test
    void sizeWorksWell(){
        Deck<Card> deck = Deck.of(SortedBag.of(3,Card.BLUE), new Random());
        assertEquals(deck.size(),3);
       Deck<Card> deck1 = Deck.of(SortedBag.of(0,Card.BLUE), new Random());
       assertEquals(deck1.size(),0);

    }

    @Test

    void isEmptyWorksWell(){
       Deck<Card> deck = Deck.of(SortedBag.of(0,Card.BLUE), new Random());
       assertTrue(deck.isEmpty());
    }

    @Test
    void topCardWorks(){
       Deck<Card> deck =  Deck.of(SortedBag.of(3,Card.BLUE), new Random());
        assertEquals(deck.topCard(),Card.BLUE);
    }

    @Test
    void WithoutTopCardWorks(){
       Deck<Card> deck = Deck.of(SortedBag.of(3,Card.BLUE), new Random());
       Deck<Card> value = deck.withoutTopCard();
       Deck<Card> excpected = Deck.of(SortedBag.of(2,Card.BLUE), new Random());
       assertEquals(value.topCards(value.size()),excpected.topCards(value.size()));
    }

    @Test
    void TopcardsWorks(){
       Deck<Card> deck=Deck.of(SortedBag.of(3,Card.BLUE), new Random());
       List<Card> cards = List.of(Card.BLUE,Card.BLUE,Card.BLUE);
       SortedBag<Card> bag = SortedBag.of(cards);
       assertEquals(deck.topCards(3),bag);
    }



    @Test
    void topCardFailsWithEmptyDeck(){
        Deck<Card> deck = Deck.of(SortedBag.of(0,Card.BLUE), new Random());
        assertThrows(IllegalArgumentException.class, ()->{
            deck.topCard();
        });
    }

    @Test
    void WithoutTopCardFailsWithEmptyDeck(){
        Deck<Card> deck = Deck.of(SortedBag.of(0,Card.BLUE), new Random());
        assertThrows(IllegalArgumentException.class, ()->{
            deck.withoutTopCard();
        });
    }

    @Test
    void WithoutTopCardsFailsIllegalParameter(){
        Deck<Card> deck = Deck.of(SortedBag.of(0,Card.BLUE), new Random());
        assertThrows(IllegalArgumentException.class, ()->{
            deck.withoutTopCards(-1);
        });
        assertThrows(IllegalArgumentException.class, ()->{
            deck.withoutTopCards(5);
        });
    }

    @Test
    void listHasBeenShuffled(){
       List<Card > cards = new ArrayList<>(Card.ALL);
       cards.add(Card.BLUE);
       Deck<Card> deck = Deck.of(SortedBag.of(cards), new Random());
       assertNotEquals(cards,deck.topCards(deck.size()));
    }


    @Test
    void TopCardsFailsIllegalParameter(){
        Deck<Card> deck = Deck.of(SortedBag.of(0,Card.BLUE), new Random());
        assertThrows(IllegalArgumentException.class, ()->{
            deck.topCards(-1);
        });
        assertThrows(IllegalArgumentException.class, ()->{
            deck.topCards(5);
        });
    }



}

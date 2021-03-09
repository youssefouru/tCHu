package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class CardStateTest {
    public static CardState all;
    public static Deck<Card> deck;

    @BeforeAll
    static void  init(){
        List<Card> cards = new LinkedList<>();
        for(Card c : Card.ALL){
            Random rng = new Random();
            int i = rng.nextInt(40) +  5;
            for(int j = 0 ; j<i;++j){
                cards.add(c);
            }
        }
        deck =Deck.of(SortedBag.of(cards),new Random());
        all = CardState.of(deck);
    }

    @Test
    void ofCardStateFailsWithLessThanFiveCards(){
        assertThrows(IllegalArgumentException.class, ()->{
            CardState.of(Deck.of(SortedBag.of(3,Card.BLUE),new Random()));
        });
    }

   @Test
   void withDrawnFaceUpCardWorks(){
     List<Card> face = new ArrayList<>(all.faceUpCards());
     int i = TestRandomizer.newRandom().nextInt(4);
     face.set(i,all.topDeckCard());
     assertEquals(face,all.withDrawnFaceUpCard(i).faceUpCards());
   }

   @Test
    void withDrawnFaceUpCardfailsWithAIllegalSlot(){
        assertThrows(IndexOutOfBoundsException.class, ()->{
            all.withDrawnFaceUpCard(-1);
        });
       assertThrows(IndexOutOfBoundsException.class, ()->{
           all.withDrawnFaceUpCard(10);
       });
   }

    @Test
    void withDrawnFaceUpCardfailsWithAnEmptyDeck(){
        CardState cardState = CardState.of(Deck.of(SortedBag.of(5,Card.BLUE),new Random()));
        assertThrows(IllegalArgumentException.class, ()->{
            cardState.withDrawnFaceUpCard(4);
        });
    }

    @Test
    void topDeckCardFailsWithAnEmptyDeck(){
        CardState cardState = CardState.of(Deck.of(SortedBag.of(5,Card.BLUE),new Random()));
        assertThrows(IllegalArgumentException.class, ()->{
            cardState.topDeckCard();
        });
    }

    @Test
    void topDeckCardWorks(){
        assertEquals(deck.topCard(),all.topDeckCard());
    }

    @Test
    void withoutTopDeckCardWorksWell(){
        assertEquals(CardState.of(deck.withoutTopCard()),all.withoutTopDeckCard());
    }

    @Test
    void withoutTopDeckCardFails(){
        CardState cardState = CardState.of(Deck.of(SortedBag.of(5,Card.BLUE),new Random()));
        assertThrows(IllegalArgumentException.class, ()->{
            cardState.withoutTopDeckCard();
        });
    }


    @Test
    void withMoreDiscardedCardsWorksWell(){

    }


}

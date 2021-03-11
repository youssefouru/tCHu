package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PublicCardStateTest {

    @Test
    void ConstructorFailsWithaceUpCardsNot5(){
        List<Card> f1= List.of(Card.BLUE, Card.GREEN, Card.ORANGE);
        assertThrows(IllegalArgumentException.class, () -> {
            new PublicCardState(f1, 1, 1);
        });

    }

    @Test
    void ConstructorfailsWithNegativeDeck(){
        List<Card> f1= List.of(Card.BLUE, Card.GREEN, Card.ORANGE, Card.WHITE, Card.LOCOMOTIVE);
        assertThrows(IllegalArgumentException.class, () -> {
            new PublicCardState(f1, -1, 1);
        });

    }

    @Test
    void ConstructorFailsWithNegativeDiscards(){
        List<Card> f1= List.of(Card.BLUE, Card.GREEN, Card.ORANGE, Card.WHITE, Card.LOCOMOTIVE);
        assertThrows(IllegalArgumentException.class, () -> {
            new PublicCardState(f1, 1, -1);
        });

    }

    @Test
    void totalSizeWorks(){
        List<Card> f1= List.of(Card.BLUE, Card.GREEN, Card.ORANGE, Card.WHITE, Card.LOCOMOTIVE);
        PublicCardState cs1= new PublicCardState(f1, 1,1);
        assertEquals(7, cs1.totalSize());
    }

    @Test
    void faceUpCardsWorks(){
        List<Card> f1= List.of(Card.BLUE, Card.GREEN, Card.ORANGE, Card.WHITE, Card.LOCOMOTIVE);
        PublicCardState cs1= new PublicCardState(f1, 1,1);
        assertEquals(f1, cs1.faceUpCards());
    }

    @Test
    void faceUpCard2Works(){
        List<Card> f1= List.of(Card.BLUE, Card.GREEN, Card.ORANGE, Card.WHITE, Card.LOCOMOTIVE);
        PublicCardState cs1= new PublicCardState(f1, 1,1);
        for (int i=0; i<5; i++){
            assertEquals(f1.get(i), cs1.faceUpCard(i));
        }
    }

    @Test
    void deckSizeWorks(){
        List<Card> f1= List.of(Card.BLUE, Card.GREEN, Card.ORANGE, Card.WHITE, Card.LOCOMOTIVE);
        PublicCardState cs1= new PublicCardState(f1, 1,1);
        assertEquals(1, cs1.deckSize());
    }

    @Test
    void deckIsemptyWorks(){
        List<Card> f1= List.of(Card.BLUE, Card.GREEN, Card.ORANGE, Card.WHITE, Card.LOCOMOTIVE);
        PublicCardState cs1= new PublicCardState(f1, 0,1);
        assertEquals(true, cs1.isDeckEmpty());
    }

    @Test
    void deckIsemptyFailsWithNonEmptyDeck(){
        List<Card> f1= List.of(Card.BLUE, Card.GREEN, Card.ORANGE, Card.WHITE, Card.LOCOMOTIVE);
        PublicCardState cs1= new PublicCardState(f1, 1,1);
        assertEquals(false, cs1.isDeckEmpty());
    }

    @Test
    void DiscardWorks(){
        List<Card> f1= List.of(Card.BLUE, Card.GREEN, Card.ORANGE, Card.WHITE, Card.LOCOMOTIVE);
        PublicCardState cs1= new PublicCardState(f1, 0,2);
        assertEquals(2, cs1.discardsSize());
    }

}

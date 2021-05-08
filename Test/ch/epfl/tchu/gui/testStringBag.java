package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class testStringBag {



    @Test
    void test(){
        SortedBag<Card> cards  = SortedBag.of(3,Card.LOCOMOTIVE,5,Card.BLUE).union(SortedBag.of(4,Card.RED,8,Card.WHITE));
        CardBagStringConverter bagStringConverter = new CardBagStringConverter();
        String c = bagStringConverter.toString(cards);
        System.out.println(c);
        Assertions.assertEquals(cards,bagStringConverter.fromString(c));
    }
}

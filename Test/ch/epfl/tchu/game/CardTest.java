package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.List;

import static ch.epfl.tchu.game.Card.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class CardTest {

    @Test
    void checkIfTheColorIsRight() {
        for (int i = 0; i < Card.CARS.size(); ++i) {
            assertEquals(Card.CARS.get(i).color(), Color.ALL.get(i));
        }

    }

    @Test
    void checkIfTheCardsCreatedByTheMethodOfHasTheRightColor() {
        for (int i = 0; i < Color.ALL.size(); ++i) {
            assertEquals(Card.of(Color.ALL.get(i)).color(), Color.ALL.get(i));
        }
    }

  @Test
  void checkTheElements(){
      var expectedValues = new Card[]{
              BLACK, VIOLET, BLUE, GREEN, YELLOW, ORANGE, RED, WHITE, LOCOMOTIVE
      };

      assertArrayEquals(expectedValues, Card.values());
  }

    @Test
    void checkTheALLList(){
        var excpected = new Card[]{Card.BLACK, Card.VIOLET, Card.BLUE, Card.GREEN, Card.YELLOW, Card.ORANGE, Card.RED, Card.WHITE, Card.LOCOMOTIVE};
        var excpectedList= List.of(excpected);
        assertEquals(excpectedList, Card.ALL);
    }

    @Test
    void CheckTheCarsList(){
        var excpected = new Card[]{Card.BLACK, Card.VIOLET, Card.BLUE, Card.GREEN, Card.YELLOW, Card.ORANGE, Card.RED, Card.WHITE};
        var excpectedList= List.of(excpected);
        assertEquals(excpectedList, Card.CARS);
    }

    @Test
    void checkTheCOUNTattribute(){
        assertEquals(COUNT,9);
    }

}

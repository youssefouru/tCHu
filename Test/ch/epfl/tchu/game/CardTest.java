package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class CardTest {

    @Test
    void checkIfTheColorIsRight(){
        for(int i = 0 ; i<Card.CARS.size();++i){
            assertEquals(Card.CARS.get(i).color(), Color.ALL.get(i));
        }

    }

    @Test
    void checkIfTheCardsCreatedByTheMethodOfHasTheRightColor(){
        for(int i = 0 ; i<Color.ALL.size();++i){
            assertEquals(Card.of(Color.ALL.get(i)).color(), Color.ALL.get(i));
        }
    }

}

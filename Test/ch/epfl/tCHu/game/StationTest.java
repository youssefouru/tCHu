package ch.epfl.tCHu.game;

import ch.epfl.tchu.game.Station;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StationTest {
    @Test
    void checkIfTheConstructorThrowsTheRightException(){
        assertThrows(IllegalArgumentException.class,()-> {
            new Station(-6,"");
        });
    }

    @Test
    void checkIfItRetrunsTheRightId(){
        Station station = new Station(13,"");
        assertEquals(station.id(),13);
    }

    @Test
    void checkIfItRetrunsTheRightName(){
        Station station = new Station(13,"name");
        assertEquals(station.name(),"name");
        assertEquals(station.toString(),"name");
    }
}

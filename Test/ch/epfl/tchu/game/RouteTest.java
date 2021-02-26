package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RouteTest {


    @Test
    void possibleClaimCardWorks(){
        Route route = new Route("l",new Station(2,"d"),new Station(1,"k"),2, Route.Level.OVERGROUND,Color.RED);
        List<SortedBag<Card>> excpectedList = new ArrayList<>();
        excpectedList.add(SortedBag.of(2,Card.RED));
        assertEquals(excpectedList,route.possibleClaimCards());

    }
}

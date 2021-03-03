package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RouteTest {


    @Test
    void possibleClaimCardWorks(){
        Route route = new Route("l",new Station(2,"d"),new Station(1,"k"),2, Route.Level.OVERGROUND,Color.RED);
        List<SortedBag<Card>> excpectedList = new ArrayList<>();
        excpectedList.add(SortedBag.of(2,Card.RED));
        assertEquals(excpectedList,route.possibleClaimCards());

    }

    @Test
    void constructorFailsWithADifferentLenght(){
        assertThrows(IllegalArgumentException.class, ()->{
           new Route("l",new Station(2,"d"),new Station(1,"k"),0, Route.Level.OVERGROUND,Color.RED);

        });
        assertThrows(IllegalArgumentException.class, ()->{
            new Route("l",new Station(2,"d"),new Station(1,"k"),-5, Route.Level.OVERGROUND,Color.RED);

        });
        assertThrows(IllegalArgumentException.class, ()->{
            new Route("l",new Station(2,"d"),new Station(1,"k"),10, Route.Level.OVERGROUND,Color.RED);

        });
        Station s= new Station(1,"k");
        Station s2= new Station(1,"k");
        assertThrows(IllegalArgumentException.class, ()->{
            new Route("l",s,s,0, Route.Level.OVERGROUND,Color.RED);

        });
        assertThrows(IllegalArgumentException.class, ()->{
            new Route("l",s,s2,0, Route.Level.OVERGROUND,Color.RED);

        });
        assertThrows(NullPointerException.class, ()->{
            new Route("l",null,s,1, Route.Level.OVERGROUND,Color.RED);

        });
        assertThrows(NullPointerException.class, ()->{
            new Route("l",s,null,1, Route.Level.OVERGROUND,Color.RED);

        });
        assertThrows(NullPointerException.class, ()-> {
            new Route("l", null, null, 1, Route.Level.OVERGROUND, Color.RED);
        });

    }

    @Test
    void oppositeStationWorksWell(){
        Station s1= new Station(1,"k");
        Station s2= new Station(2,"s");
        Route r  = new Route("l",s1,s2,2, Route.Level.UNDERGROUND,Color.BLUE);
        assertEquals(r.stationOpposite(s1),s2);
        assertEquals(r.stationOpposite(s2),s1);
    }


    @Test
    void oppositeStationFailsWithAdiffrentStation(){
        Station s1= new Station(1,"k");
        Station s2= new Station(2,"s");
        Route r  = new Route("l",s1,s2,2, Route.Level.UNDERGROUND,Color.BLUE);
        Station s3 = new Station(3,"r");
        assertThrows(IllegalArgumentException.class, ()->{
            r.stationOpposite(s3);
        });
    }


    @Test
    void checkIfTheAdditionalClaimCardIsOkay(){
        SortedBag<Card> cards = SortedBag.of(3,Card.YELLOW);
        SortedBag<Card> drawnCard = SortedBag.of(2,Card.YELLOW,1,Card.LOCOMOTIVE);
        Station s1= new Station(1,"k");
        Station s2= new Station(2,"s");
        Route r  = new Route("l",s1,s2,2, Route.Level.UNDERGROUND,Color.BLUE);
        assertEquals(r.additionalClaimCardsCount(cards,drawnCard),3);
        drawnCard = SortedBag.of(3,Card.RED);
        assertEquals(r.additionalClaimCardsCount(cards,drawnCard),0);

    }
}

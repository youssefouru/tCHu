package ch.epfl.tchu.game;

import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PublicPlayerStateTest {


    public static List<Route> list;
    public static int points;

    @BeforeAll
    public static void initialize(){
        list =  new ArrayList<>();
        int i = TestRandomizer.newRandom().nextInt(50);
        int j =TestRandomizer.newRandom().nextInt(50);
        while(i>j){
            j =TestRandomizer.newRandom().nextInt(50);
        }
        list.addAll(ChMap.routes().subList(i,45));
        points= 0 ;
        for(Route route : list){
            points+=route.claimPoints();
        }
    }


    @Test
    void claimPointsWorksWell(){
        assertEquals((new PublicPlayerState(5,6,list)).claimPoints(),points);
    }

    @Test
    void constructorFailsWithANegatifCardCount(){

        assertThrows(IllegalArgumentException.class, ()->{
            new PublicPlayerState(1,-2,list);
        });
    }

    @Test
    void constructorFailsWithNegatifTicketCount(){
        assertThrows(IllegalArgumentException.class, ()->{
            new PublicPlayerState(-4,1,list);
        });
    }

    @Test
    void constructorFailsWithNegatifTicketCountAndCardCount(){
        assertThrows(IllegalArgumentException.class, ()->{
            new PublicPlayerState(-4,-1,list);
        });
    }

    @Test
    void methodsroutesWorks(){
        List<Route> myList = List.of(ChMap.routes().get(0),ChMap.routes().get(1));
        PublicPlayerState playerState = new PublicPlayerState(4,6,myList);
        assertEquals(myList,playerState.routes());
    }

    @Test
    void cardCountWorksWell(){
        List<Route> myList = List.of(ChMap.routes().get(0),ChMap.routes().get(1));
        PublicPlayerState playerState = new PublicPlayerState(4,6,myList);
        assertEquals(playerState.cardCount(), playerState.cardCount());
    }
    @Test
    void ticketsCountWorksWell(){
        List<Route> myList = List.of(ChMap.routes().get(0),ChMap.routes().get(1));
        PublicPlayerState playerState = new PublicPlayerState(4,6,List.of());
        assertEquals(playerState.ticketCount(),4);
    }

    @Test
    void carCountWorksWell(){
        List<Route> myList = List.of(ChMap.routes().get(0),ChMap.routes().get(1));
        PublicPlayerState playerState = new PublicPlayerState(4,6,List.of());
        assertEquals(playerState.carCount(),40);
    }
}



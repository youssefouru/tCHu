package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PublicGameStateTest {

    List<Card> f1= List.of(Card.BLUE, Card.GREEN, Card.ORANGE, Card.WHITE, Card.RED);
    PublicCardState cs1= new PublicCardState(f1, 10,5);
    PublicCardState csf= new PublicCardState(f1, 3,1);
    Map<PlayerId, PublicPlayerState> playerState= new HashMap<>();



    @Test
    public void ConstructorFailWithNegativeTicketsCountSize(){
        assertThrows(IllegalArgumentException.class, () -> {
            new PublicGameState(-1, cs1, PlayerId.PLAYER_1, playerState, PlayerId.PLAYER_2 );
        });

    }

    @Test
    public void ConstructorFailsWithPlayerStateSizeFalse(){
        List<Route> routes = List.of(ChMap.routes().get(0), ChMap.routes().get(1), ChMap.routes().get(3));
        PublicPlayerState publicPlayerState1 = new PublicPlayerState(3, 4, routes);
        playerState.put(PlayerId.PLAYER_1, publicPlayerState1);
        assertThrows(IllegalArgumentException.class, () -> {
            new PublicGameState(3, cs1, PlayerId.PLAYER_1, playerState, PlayerId.PLAYER_2 );
        });
    }

    @Test
    public void canDrawTicketsWorks(){
        List<Route> routes = List.of(ChMap.routes().get(0), ChMap.routes().get(1), ChMap.routes().get(3));
        PublicPlayerState publicPlayerState1 = new PublicPlayerState(3, 4, routes);
        playerState.put(PlayerId.PLAYER_1, publicPlayerState1);
        PublicPlayerState publicPlayerState2 = new PublicPlayerState(3, 4, List.of(ChMap.routes().get(4)));
        playerState.put(PlayerId.PLAYER_2, publicPlayerState2);
        PublicGameState p= new PublicGameState(3, cs1, PlayerId.PLAYER_1, playerState, PlayerId.PLAYER_2 );
        assertEquals(true, p.canDrawTickets());

    }

    @Test
    public void canDrawTicketsFails(){
        List<Route> routes = List.of(ChMap.routes().get(0), ChMap.routes().get(1), ChMap.routes().get(3));
        PublicPlayerState publicPlayerState1 = new PublicPlayerState(3, 4, routes);
        playerState.put(PlayerId.PLAYER_1, publicPlayerState1);
        PublicPlayerState publicPlayerState2 = new PublicPlayerState(3, 4, List.of(ChMap.routes().get(4)));
        playerState.put(PlayerId.PLAYER_2, publicPlayerState2);
        PublicGameState p= new PublicGameState(0, cs1, PlayerId.PLAYER_1, playerState, PlayerId.PLAYER_2 );
        assertEquals(false, p.canDrawTickets());

    }

    @Test
    public void canDrawCardsWorks(){
        List<Route> routes = List.of(ChMap.routes().get(0), ChMap.routes().get(1), ChMap.routes().get(3));
        PublicPlayerState publicPlayerState1 = new PublicPlayerState(3, 4, routes);
        playerState.put(PlayerId.PLAYER_1, publicPlayerState1);
        PublicPlayerState publicPlayerState2 = new PublicPlayerState(3, 4, List.of(ChMap.routes().get(4)));
        playerState.put(PlayerId.PLAYER_2, publicPlayerState2);
        PublicGameState p= new PublicGameState(3, cs1, PlayerId.PLAYER_1, playerState, PlayerId.PLAYER_2 );
        assertEquals(true, p.canDrawCards());


    }

    @Test
    public void canDrawCardsFails(){
        List<Route> routes = List.of(ChMap.routes().get(0), ChMap.routes().get(1), ChMap.routes().get(3));
        PublicPlayerState publicPlayerState1 = new PublicPlayerState(3, 4, routes);
        playerState.put(PlayerId.PLAYER_1, publicPlayerState1);
        PublicPlayerState publicPlayerState2 = new PublicPlayerState(3, 4, List.of(ChMap.routes().get(4)));
        playerState.put(PlayerId.PLAYER_2, publicPlayerState2);
        PublicGameState p= new PublicGameState(3, csf, PlayerId.PLAYER_1, playerState, PlayerId.PLAYER_2 );
        assertEquals(false, p.canDrawCards());

    }

    @Test
    public void claimedRoutesWorks(){
        List<Route> routes = List.of(ChMap.routes().get(0), ChMap.routes().get(1), ChMap.routes().get(3));
        PublicPlayerState publicPlayerState1 = new PublicPlayerState(3, 4, routes);
        playerState.put(PlayerId.PLAYER_1, publicPlayerState1);
        PublicPlayerState publicPlayerState2 = new PublicPlayerState(3, 4, List.of(ChMap.routes().get(4)));
        playerState.put(PlayerId.PLAYER_2, publicPlayerState2);
        PublicGameState gameState = new PublicGameState(3, csf, PlayerId.PLAYER_1, playerState, PlayerId.PLAYER_2 );
        List<Route> routes1 = new LinkedList<>(publicPlayerState1.routes());
        routes1.addAll(publicPlayerState2.routes());
        assertEquals(routes1.size(), gameState.claimedRoutes().size());
    }


}

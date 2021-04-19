package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class SerdesTest {

    @Test
    void stringSerdeWorksWell(){
        String s = "coucou";
        String encoded = Serdes.STRING_SERDE.serialize(s);
        assertEquals(s,Serdes.STRING_SERDE.deserialize(encoded));
    }

    @Test
    void integerSerdesWorksWell(){
        Random testRandomizer = TestRandomizer.newRandom();
        int i = testRandomizer.nextInt();
        String valueSerialized = Serdes.INTEGER_SERDE.serialize(i);
        assertEquals(Serdes.INTEGER_SERDE.deserialize(valueSerialized),i);
    }

    @Test
    void TurnKindWorksWell(){
        int i = TestRandomizer.newRandom().nextInt(Player.TurnKind.ALL.size());
        Player.TurnKind turnKind =Player.TurnKind.ALL.get(i);
        String serialExpected = String.valueOf(turnKind.ordinal());
        assertEquals(serialExpected,Serdes.TURN_KIND_SERDE.serialize(turnKind));
        assertEquals(turnKind,Serdes.TURN_KIND_SERDE.deserialize(serialExpected));
    }

    @Test
    void StringListGotSerialized(){
    List<String> stringList = List.of("tatata","coucocucou","ropgjfghjdfojgdfiojg","gopfhiujtohjhj");
    String serialized = Serdes.STRING_LIST_SERDE.serialize(stringList);
    assertEquals(stringList,Serdes.STRING_LIST_SERDE.deserialize(serialized));
    }

    @Test
    void routeListSerdeWorksOnAList(){
        List<Route> routes = ChMap.routes().subList(0,6);
        String serialExcepted = "0,1,2,3,4,5";
        String actualSerial =Serdes.ROUTE_LIST_SERDE.serialize(routes);
        assertEquals(serialExcepted,actualSerial);
        List<Route> routesDeserialized = Serdes.ROUTE_LIST_SERDE.deserialize(actualSerial);
        assertEquals(routes,routesDeserialized);
    }

    @Test
    void cardBagListSerialize(){
        List<SortedBag<Card>> sortedBagCardList = List.of(SortedBag.of(4,Card.BLACK),SortedBag.of(5,Card.BLUE),SortedBag.of(4,Card.WHITE));
        String serialExcepted = "0,0,0,0;2,2,2,2,2;7,7,7,7";
        assertEquals(serialExcepted,Serdes.CARD_BAG_LIST_SERDE.serialize(sortedBagCardList));
        assertEquals(sortedBagCardList,Serdes.CARD_BAG_LIST_SERDE.deserialize(serialExcepted));
    }

    @Test
    void publicPlayerStateGotSerialized(){
        int ticketsCount = 50;
        int cardsCount =10;
        List<Route> routes = ChMap.routes().subList(0,6);
        String serial = "50;10;0,1,2,3,4,5";
        PublicPlayerState playerState = new PublicPlayerState(ticketsCount,cardsCount,routes);
        String serialPlayerState =Serdes.PUBLIC_PLAYER_STATE_SERDE.serialize(playerState);
        assertEquals(serial,serialPlayerState);
        PublicPlayerState playerState1 = Serdes.PUBLIC_PLAYER_STATE_SERDE.deserialize(serialPlayerState);
        assertTrue(equalsPublicPlayerState(playerState1,playerState));

    }

    @Test
    void publicCardStateHasBeenSerializedWell(){
        List<Card> list = SortedBag.of(5,Card.BLACK).toList();
        String serial = "0,0,0,0,0;50;10";
        int deckSize = 50;
        int discardSize = 10;
        PublicCardState publicCardState = new PublicCardState(list, deckSize,discardSize);
        String cardStateSerialized = Serdes.PUBLIC_CARD_STATE_SERDE.serialize(publicCardState);
        assertEquals(serial,cardStateSerialized);
        assertTrue(equalsCardState(publicCardState,Serdes.PUBLIC_CARD_STATE_SERDE.deserialize(cardStateSerialized)));
    }

    @Test
    void publicGameStateGotSerialized(){
        Random testRandomizer = TestRandomizer.newRandom();
        int i = testRandomizer.nextInt(50);
        int ticketsCount = 10;
        List<Card> list = SortedBag.of(5,Card.BLACK).toList();
        int deckSize = 50;
        int discardSize = 10;
        PlayerId currentPlayer = PlayerId.ALL.get(i%PlayerId.COUNT);
        int ticketCount =100;
        int cardCount = 55;
        List<Route> routes = ChMap.routes().subList(0,6);
        Map<PlayerId,PublicPlayerState> map = new HashMap<>();
        for (PlayerId playerId: PlayerId.ALL) {
            map.put(playerId,new PublicPlayerState(ticketCount,cardCount,routes));
        }
        PublicCardState publicCardState = new PublicCardState(list, deckSize,discardSize);
        PublicGameState publicGameState = new PublicGameState(ticketsCount,publicCardState,currentPlayer,map,null);
        String serial = "10:0,0,0,0,0;50;10:"+currentPlayer.ordinal()+":100;55;0,1,2,3,4,5:100;55;0,1,2,3,4,5:";
        String gameStateSerial = Serdes.PUBLIC_GAME_STATE_SERDE.serialize(publicGameState);
        assertEquals(serial,gameStateSerial);
        assertTrue(equalsPublicGameState(publicGameState,Serdes.PUBLIC_GAME_STATE_SERDE.deserialize(gameStateSerial)));

    }

    @Test
    void RouteSerdeWorksWithEmptyList(){
        List<Route> routes = new ArrayList<>();
        String serial = "";
        String actualSerial = Serdes.ROUTE_LIST_SERDE.serialize(routes);
        assertEquals(serial,actualSerial);
        assertEquals(routes,Serdes.ROUTE_LIST_SERDE.deserialize(actualSerial));
    }

    @Test
    void playerStateGotSerialized(){
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets().subList(0,6));
        SortedBag<Card> Cards = SortedBag.of(5,Card.BLACK);
        List<Route> routes = ChMap.routes().subList(0,6);
        String serial = "3,4,5,0,1,2;0,0,0,0,0;0,1,2,3,4,5";
        PlayerState playerState = new PlayerState(tickets,Cards,routes);
        String serialPlayer= Serdes.PLAYER_STATE_SERDE.serialize(playerState);
        assertEquals(serial,serialPlayer);
        assertTrue(equalsPlayerState(playerState,Serdes.PLAYER_STATE_SERDE.deserialize(serialPlayer)));
    }

    @Test
    void sortedBagGotSerializedWellWithEmpty(){
    SortedBag<Card> routes = SortedBag.of();
    String serial = "";
    String actualSerial = Serdes.CARD_BAG_SERDE.serialize(routes);
    assertEquals(serial,actualSerial);
    assertEquals(routes,Serdes.CARD_BAG_SERDE.deserialize(actualSerial));
}

    private boolean equalsCardState(PublicCardState cardState1,PublicCardState cardState2){
        return cardState1.faceUpCards().equals(cardState2.faceUpCards()) && cardState1.deckSize() == cardState2.deckSize() && cardState1.discardsSize() == cardState2.discardsSize();
    }
    private boolean equalsPublicPlayerState(PublicPlayerState playerState1, PublicPlayerState playerState2){
        return playerState1.routes().equals(playerState2.routes()) && playerState1.ticketCount() == playerState2.ticketCount() && playerState1.cardCount() == playerState2.cardCount();
    }
    private boolean equalsPlayerState(PlayerState playerState, PlayerState playerState2){
        return playerState.cards().equals(playerState2.cards()) &&playerState.tickets().equals(playerState2.tickets())  && playerState.routes().equals(playerState2.routes());
    }
    private boolean equalsPublicGameState(PublicGameState gameState1,PublicGameState gameState2){
        return equalsCardState(gameState1.cardState(),gameState2.cardState()) && gameState1.currentPlayerId() == gameState2.currentPlayerId() && mapEqualizer(gameState1,gameState2) && gameState1.ticketsCount() == gameState2.ticketsCount() && gameState1.lastPlayer() == gameState2.lastPlayer();
    }
    private boolean mapEqualizer(PublicGameState gameState1,PublicGameState gameState2){
        for(PlayerId playerId : PlayerId.ALL){
            if(!equalsPublicPlayerState(gameState1.playerState(playerId) ,gameState2.playerState(playerId))) return false;
        }
        return true;
    }
}

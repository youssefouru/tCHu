package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;


import static ch.epfl.tchu.game.Constants.INITIAL_CARDS_COUNT;
import static ch.epfl.tchu.game.PlayerState.initial;
import static org.junit.jupiter.api.Assertions.*;

class PlayerStateTest {

    private static PlayerState player;
    private static List<Card> initCards = new ArrayList<>();
    final SortedBag<Card> testCards = SortedBag.of(Card.ALL.subList(0,4));
    final Station[] stations = new Station[]{ new Station(0, "a"),
            new Station(1, "b"),
            new Station(2, "c"),
            new Station(3, "d"),
            new Station(4, "e")};



    @BeforeAll
    public static void setUp(){
        initCards.add(Card.BLUE);
        initCards.add(Card.GREEN);
        initCards.add(Card.ORANGE);
        initCards.add(Card.RED);


        player = initial(SortedBag.of(initCards));
    }

    @Test
    void initialThrowIllegalArgumentExceptionIfInitialCardsCountNotEqual0(){
        List<Card> tempCard = new ArrayList<>();
        tempCard.add(Card.ORANGE);
        tempCard.add(Card.BLUE);
        tempCard.add(Card.RED);
        assertThrows(IllegalArgumentException.class, () -> {initial(SortedBag.of(tempCard));});
    }
    @Test
    void initialWorks(){
        assertTrue(player.tickets().isEmpty() && player.routes().isEmpty() && player.cards().size() == INITIAL_CARDS_COUNT);
    }

    @Test
    void withAddedTicketsWorksWith1Ticket(){

        PlayerState playerAfterAddingTicket = player.withAddedTickets(SortedBag.of(ChMap.tickets().get(3)));
        assertEquals(1,playerAfterAddingTicket.tickets().size());
    }

    @Test
    void withAddedTicketsWorksWith4Tickets(){
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(ChMap.tickets().get(0));
        tickets.add(ChMap.tickets().get(4));
        tickets.add(ChMap.tickets().get(24));
        tickets.add(ChMap.tickets().get(13));

        PlayerState playerAfterAddingTickets = player.withAddedTickets(SortedBag.of(tickets));
        assertEquals(4,playerAfterAddingTickets.tickets().size());
    }

    @Test
    void cardsGetterWorks(){
        assertEquals(initCards, player.cards().toList());
    }

    @Test
    void withAddedCardWorks(){
        PlayerState playerAfterAdding1Card = player.withAddedCard(Card.WHITE);
        assertEquals(5,playerAfterAdding1Card.cards().size());

    }
    @Test
    void withAddedCardsWorks(){
        List<Card> additionalCards = new ArrayList<>();
        additionalCards.add(Card.BLUE);
        additionalCards.add(Card.RED);
        additionalCards.add(Card.RED);
        additionalCards.add(Card.ORANGE);
        additionalCards.add(Card.BLACK);
        additionalCards.add(Card.VIOLET);
        PlayerState playerAfterAddingCards = player.withAddedCards(SortedBag.of(additionalCards));
        assertEquals(10,playerAfterAddingCards.cards().size());
    }
    @Test
    void canClaimRouteReturnTrue(){
        assertTrue(player.canClaimRoute(ChMap.routes().get(1)));
        List<Card> additionalCards = new ArrayList<>();
        additionalCards.add(Card.VIOLET);
        additionalCards.add(Card.VIOLET);
        additionalCards.add(Card.VIOLET);
        additionalCards.add(Card.VIOLET);
        PlayerState playerAfterAddingCards = player.withAddedCards(SortedBag.of(additionalCards));
        assertTrue(playerAfterAddingCards.canClaimRoute(ChMap.routes().get(49)));

    }
    @Test
    void canClaimRouteReturnFalse(){
        assertFalse(player.canClaimRoute(ChMap.routes().get(26)));
    }


    //TODO test de possibleClaimCards et possibleAdditionalCards
    @Test
    void possibleClaimCardsTest1()
    {
        List<Card> claimCards = new ArrayList<>();
        claimCards.add(Card.WHITE);
        claimCards.add(Card.LOCOMOTIVE);
        claimCards.add(Card.LOCOMOTIVE);
        assertEquals(List.of(SortedBag.of(claimCards)), player.withAddedCards(SortedBag.of(claimCards)).possibleClaimCards(ChMap.routes().get(39)));
    }
    @Test
    void possibleClaimCardsTest2()
    {
        List<Card> claimCards = new ArrayList<>();
        claimCards.add(Card.VIOLET);
        claimCards.add(Card.VIOLET);
        claimCards.add(Card.VIOLET);
        claimCards.add(Card.VIOLET);
        assertEquals(List.of(SortedBag.of(claimCards)), player.withAddedCards(SortedBag.of(claimCards)).possibleClaimCards(ChMap.routes().get(49)));
    }
    @Test
    void possibleAdditionalCardsExceptions() {
        List<Card> okCards = List.of(Card.BLUE, Card.LOCOMOTIVE);
        List<Card> notOkCards = List.of(Card.BLUE, Card.YELLOW, Card.ORANGE);

        List<Card> okDrawnCards = notOkCards;
        List<Card> notOkDrawnCards = List.of(Card.RED);

        assertThrows(IllegalArgumentException.class, () -> {
            player.possibleAdditionalCards(0, SortedBag.of(okCards), SortedBag.of(okDrawnCards));
        });


    }

    @Test
    void possibleAdditionalCardsExceptions2(){
        List<Card> okCards = List.of(Card.BLUE, Card.LOCOMOTIVE);
        List<Card> notOkCards = List.of(Card.BLUE, Card.YELLOW, Card.ORANGE);

        List<Card> okDrawnCards = notOkCards;
        List<Card> notOkDrawnCards = List.of(Card.RED);

        assertThrows(IllegalArgumentException.class, () -> {
            player.possibleAdditionalCards(20, SortedBag.of(okCards), SortedBag.of(okDrawnCards));
        });
    }

    @Test
    void possibleAdditionalCardsExceptions3(){
        List<Card> okCards = List.of(Card.BLUE, Card.LOCOMOTIVE);
        List<Card> notOkCards = List.of(Card.BLUE, Card.YELLOW, Card.ORANGE);

        List<Card> okDrawnCards = notOkCards;
        List<Card> notOkDrawnCards = List.of(Card.RED);

        assertThrows(IllegalArgumentException.class, () -> {
            player.possibleAdditionalCards(2, SortedBag.of(), SortedBag.of(okDrawnCards));
        });

    }

    @Test
    void possibleAdditionalCardsExceptions4() {
        List<Card> okCards = List.of(Card.BLUE, Card.LOCOMOTIVE);
        List<Card> notOkCards = List.of(Card.BLUE, Card.YELLOW, Card.ORANGE);

        List<Card> okDrawnCards = notOkCards;
        List<Card> notOkDrawnCards = List.of(Card.RED);
        assertThrows(IllegalArgumentException.class, () -> {
            player.possibleAdditionalCards(2, SortedBag.of(notOkCards), SortedBag.of(okDrawnCards));
        });

    }



    @Test
    void possibleAdditionalCardsExceptions5(){
        List<Card> okCards = List.of(Card.BLUE, Card.LOCOMOTIVE);
        List<Card> notOkCards = List.of(Card.BLUE, Card.YELLOW, Card.ORANGE);

        List<Card> okDrawnCards = notOkCards;
        List<Card> notOkDrawnCards = List.of(Card.RED);

        assertThrows(IllegalArgumentException.class, () -> {
            player.possibleAdditionalCards(2, SortedBag.of(okCards), SortedBag.of(notOkDrawnCards));
        });
    }




    @Test
    void possibleAdditionalCardsTest1()
    {
        List<Card> playerAdditionalCards = List.of(Card.BLUE, Card.BLUE, Card.BLUE, Card.BLUE, Card.LOCOMOTIVE,Card.LOCOMOTIVE,Card.LOCOMOTIVE);
        List<Card> initialCards =  List.of(Card.BLUE, Card.BLUE, Card.BLUE, Card.LOCOMOTIVE, Card.LOCOMOTIVE);
        List<Card> additionalCards = List.of(Card.GREEN, Card.BLUE, Card.LOCOMOTIVE);
        List<Card> possibleAdditionalCards1 = List.of(Card.BLUE);
        List<Card> possibleAdditionalCards2 = List.of(Card.LOCOMOTIVE);
        PlayerState playerState =player.withAddedCards(SortedBag.of(playerAdditionalCards));
        assertEquals(List.of(SortedBag.of(possibleAdditionalCards1), SortedBag.of(possibleAdditionalCards2)),
               playerState.possibleAdditionalCards(1, SortedBag.of(initialCards), SortedBag.of(additionalCards)));
    }

    @Test
    void possibleAdditionalCards() {
        PlayerState ps = new PlayerState(SortedBag.of(new Ticket(stations[0], stations[1], 2)), SortedBag.of(), List.of(new Route("r", stations[0], stations[1], 5, Route.Level.OVERGROUND, null)));
        SortedBag looser = SortedBag.of(3, Card.GREEN);
        PlayerState psWithAddedCards = ps.withAddedCards(SortedBag.of(2, Card.LOCOMOTIVE, 8, Card.GREEN));
        PlayerState psWithLocos = ps.withAddedCards(SortedBag.of(8, Card.LOCOMOTIVE));
        PlayerState anotherPs = ps.withAddedCards(SortedBag.of(8, Card.LOCOMOTIVE, 1, Card.GREEN));

        assertEquals(1, psWithLocos.possibleAdditionalCards(3, SortedBag.of(5, Card.LOCOMOTIVE), looser).size()); //
        assertEquals(0, psWithLocos.possibleAdditionalCards(3, SortedBag.of(7, Card.LOCOMOTIVE), looser).size()); // pas assez de locomotives, zut
        assertEquals(0, psWithLocos.possibleAdditionalCards(2, SortedBag.of(7, Card.LOCOMOTIVE), looser).size()); // pas assez de locomotives, zut
        assertEquals(1, psWithLocos.possibleAdditionalCards(1, SortedBag.of(7, Card.LOCOMOTIVE), looser).size()); // a 8-7 locomotives, il peut faire 1 choix
        assertEquals(0, anotherPs.possibleAdditionalCards(1, SortedBag.of(8, Card.LOCOMOTIVE), looser).size()); // doit payer en locomotives

        assertEquals(4, psWithAddedCards.withAddedCard(Card.LOCOMOTIVE).possibleAdditionalCards(3, SortedBag.of(4, Card.GREEN), looser).size());
        assertEquals(3, psWithAddedCards.possibleAdditionalCards(3, SortedBag.of(4, Card.GREEN), looser).size());
        assertEquals(3, psWithAddedCards.possibleAdditionalCards(2, SortedBag.of(4, Card.GREEN), looser).size());
        assertEquals(1, psWithAddedCards.possibleAdditionalCards(2, SortedBag.of(8, Card.GREEN), looser).size());
        assertEquals(0, psWithAddedCards.possibleAdditionalCards(2, SortedBag.of(8, Card.GREEN, 2, Card.LOCOMOTIVE), looser).size());
    }


    @Test
    void possibleAdditionalCardsTest3()
    {
        List<Card> initialCards =  List.of(Card.BLACK, Card.LOCOMOTIVE);
        List<Card> additionalCards = List.of(Card.GREEN, Card.BLACK, Card.LOCOMOTIVE);
        assertEquals(List.of(),
                player.possibleAdditionalCards(2,
                        SortedBag.of(initialCards),
                        SortedBag.of(additionalCards)));
    }

    @Test
    void possibleAdditionalCardsTest4(){
        List<Card> playerAdditionalCards = List.of(Card.GREEN,Card.GREEN, Card.LOCOMOTIVE,Card.LOCOMOTIVE);
        PlayerState newPlayer = player.withAddedCards(SortedBag.of(playerAdditionalCards));
        List<Card> initialCards =  List.of(Card.GREEN);
        List<Card> additionalCards = List.of(Card.RED, Card.LOCOMOTIVE, Card.GREEN);
        List<Card> possibleAdditionalCards1 = List.of(Card.GREEN, Card.GREEN);
        List<Card> possibleAdditionalCards2 = List.of(Card.GREEN, Card.LOCOMOTIVE);
        List<Card> possibleAdditionalCards3 = List.of(Card.LOCOMOTIVE,Card.LOCOMOTIVE);
        assertEquals(List.of(SortedBag.of(possibleAdditionalCards1), SortedBag.of(possibleAdditionalCards2),SortedBag.of(possibleAdditionalCards3)), newPlayer.possibleAdditionalCards(2, SortedBag.of(initialCards), SortedBag.of(additionalCards)));

    }

    @Test
    void possibleAdditionalCardsTest2()
    {
        List<Card> playerAdditionalCards = List.of(Card.RED,Card.RED,Card.LOCOMOTIVE, Card.LOCOMOTIVE,Card.LOCOMOTIVE);
        List<Card> initialCards =  List.of(Card.RED, Card.LOCOMOTIVE, Card.LOCOMOTIVE);
        List<Card> additionalCards = List.of(Card.RED, Card.LOCOMOTIVE, Card.GREEN);
        PlayerState playerState =  player.withAddedCards(SortedBag.of(playerAdditionalCards));
        List<Card> possibleAdditionalCards1 = List.of(Card.RED, Card.RED);
        List<Card> possibleAdditionalCards2 = List.of(Card.RED, Card.LOCOMOTIVE);
        assertEquals(List.of(SortedBag.of(possibleAdditionalCards1), SortedBag.of(possibleAdditionalCards2)),
                        playerState.possibleAdditionalCards(2,
                                SortedBag.of(initialCards),
                                SortedBag.of(additionalCards)));
    }

    @Test
    void possibleAdditionalCardsTestWithNoCards(){

    }



    @Test
    void withClaimedRouteWorks(){
        PlayerState playerAfterTakingARoute = player.withClaimedRoute(ChMap.routes().get(5),SortedBag.of(initCards));
        assertEquals("BAL_DE1_1",playerAfterTakingARoute.routes().get(playerAfterTakingARoute.routes().size()-1).id());
    }
    @Test
    void ticketPointsWorks(){
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(ChMap.tickets().get(25));//15 pts
        tickets.add(ChMap.tickets().get(16));//13 pts
        tickets.add(ChMap.tickets().get(29));//3 pts
        PlayerState playerAfterAddingTickets = player.withAddedTickets(SortedBag.of(tickets));
        assertEquals(-31,playerAfterAddingTickets.ticketPoints());
        Ticket ticket = new Ticket(stations[0],stations[1],50); //50
        Ticket ticket2 = new Ticket(stations[1],stations[2],40);//-40
        Ticket ticket3 = new Ticket(stations[3],stations[4],40);//-40
        Route route1 = new Route("d",stations[0],stations[1],5, Route.Level.OVERGROUND,Color.BLACK);
        Route route2 = new Route("d",stations[2],stations[4],5, Route.Level.OVERGROUND,Color.BLACK);
        PlayerState playerState = new PlayerState(SortedBag.of(List.of(ticket,ticket2,ticket3)),SortedBag.of(),List.of(route1,route2));
        assertEquals(playerState.ticketPoints(),-30);



    }

}
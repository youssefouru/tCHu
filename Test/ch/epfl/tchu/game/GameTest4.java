package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ch.epfl.tchu.SortedBag.of;
import static org.junit.jupiter.api.Assertions.*;

class TestTurn {
    private Player.TurnKind kind;
    public TestTurn(Player.TurnKind turnKind) {
        this.kind = turnKind;
    }

    public boolean playable(PublicGameState gs) { return false; }

    public Player.TurnKind kind() {
        return this.kind;
    }
}

class DrawTicketsTurn extends TestTurn {
    private Function<SortedBag<Ticket>, SortedBag<Ticket>> initialTicketChooser;
    public DrawTicketsTurn(Function<SortedBag<Ticket>, SortedBag<Ticket>> initialTicketChooser) {
        super(Player.TurnKind.DRAW_TICKETS);
        this.initialTicketChooser = initialTicketChooser;
    }

    public SortedBag<Ticket> chooseFrom(SortedBag<Ticket> tickets) {
        return initialTicketChooser.apply(tickets);
    }

    @Override
    public boolean playable(PublicGameState gs) {
        return gs.canDrawTickets();
    }
}

class DrawCardsTurn extends TestTurn {
    private final int slot2;
    private final int slot1;

    public DrawCardsTurn(int slot1, int slot2) {
        super(Player.TurnKind.DRAW_CARDS);
        this.slot1 = slot1;
        this.slot2 = slot2;
    }

    public int slot1() {
        return slot1;
    }

    public int slot2() {
        return slot2;
    }

    @Override
    public boolean playable(PublicGameState gs) {
        return gs.canDrawCards();
    }
}

class ClaimRouteTurn extends TestTurn {
    public SortedBag<Card> additionalCards;
    public Route route;
    public SortedBag<Card> cards;
    public ClaimRouteTurn(Route route, SortedBag<Card> cards, SortedBag<Card> additionalCards) {
        super(Player.TurnKind.CLAIM_ROUTE);
        this.route = route;
        this.cards = cards;
        this.additionalCards = additionalCards;
    }

    public ClaimRouteTurn(Route route, SortedBag<Card> cards) {
        this(route, cards, of());
    }

    @Override
    public boolean playable(PublicGameState gs) {
        return true;
    }
}

class DummyPlayer implements Player {
    public String name;
    public List<String> details = new ArrayList<>();
    public List<PublicGameState> states = new ArrayList<>();
    public List<PlayerState> ownstates = new ArrayList<>();
    public List<SortedBag<Ticket>> ticketsOption = new ArrayList<>();
    public List<SortedBag<Ticket>> ticketsChosen = new ArrayList<>();
    public List<Route> routes = new ArrayList<>();
    public List<SortedBag<Card>> routeCards = new ArrayList<>();
    public List<SortedBag<Card>> routePlayAdditionalCardsOptions = new ArrayList<>();
    public SortedBag<Ticket> initialTickets;
    public PlayerId ownId;

    private Function<SortedBag<Ticket>, SortedBag<Ticket>> initialTicketChooser;
    List<TestTurn> list;
    int count = -1;
    int drawCardsCount = 0;

    public DummyPlayer(List<TestTurn> list) {
        this.list = list;
    }

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        List<String> names = new ArrayList<>(playerNames.values());
        Collections.sort(names);
        this.ownId = ownId;
        this.name = playerNames.get(ownId);
        details.add("INIT:" + ownId + "/PLAYERNAMES:" + String.join(",", names));
    }

    @Override
    public void receiveInfo(String info) {
        details.add("RECEIVED:" + info);
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        details.add("STATEUPDT:" + (states.size()));

        states.add(newState);
        ownstates.add(ownState);
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        this.initialTickets = tickets;
    }

    public void setInitialTicketChooser(Function<SortedBag<Ticket>, SortedBag<Ticket>> c) {
        this.initialTicketChooser = c;
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        details.add("INITIALTICKET");
        return this.initialTicketChooser.apply(this.initialTickets);
    }

    public TestTurn getTurn() {
        return list.get(count % list.size());
    }

    @Override
    public TurnKind nextTurn() {
        TestTurn turn;
        do {
            count++;

            turn = getTurn();
        } while(!turn.playable(states.get(states.size() - 1)));

        details.add("TURN:" + turn.kind().toString());
        return turn.kind();
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        if (options.isEmpty())
            throw new Error("The bag given to chooseTickets should not be empty");

        DrawTicketsTurn ticketsTurn = (DrawTicketsTurn) getTurn();
        details.add("CHOSE-TICKETS-FROM:" + this.ticketsOption.size());
        ticketsOption.add(options);
        ticketsChosen.add(ticketsTurn.chooseFrom(options));
        return ticketsTurn.chooseFrom(options);
    }

    @Override
    public int drawSlot() {
        DrawCardsTurn turn = ((DrawCardsTurn) getTurn());
        details.add("DRAW:" + turn.slot1() + "/" + turn.slot2());
        return new int[]{turn.slot1(), turn.slot2()}[drawCardsCount++ % 2];
    }

    @Override
    public Route claimedRoute() {
        ClaimRouteTurn turn = ((ClaimRouteTurn) getTurn());
        details.add("CLAIMROUTE:" + routes.size());
        routes.add(turn.route);
        return turn.route;
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        ClaimRouteTurn turn = ((ClaimRouteTurn) getTurn());
        details.add("ROUTECARDS:" + routeCards.size());
        routePlayAdditionalCardsOptions.add(of());
        routeCards.add(turn.cards);
        return turn.cards;
    }

    public int expectedPoints() {
        StationPartition.Builder partition = new StationPartition.Builder(50);
        routes.forEach(route -> partition.connect(route.station1(), route.station2()));
        StationPartition part = partition.build();
        SortedBag<Ticket> tickets = ownstates.get(ownstates.size() - 1).tickets();
        return tickets.stream().mapToInt(t -> t.points(part)).sum() + routes.stream().mapToInt(Route::claimPoints).sum();
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        if (options.isEmpty())
            throw new Error("The argument given to chooseAdditionalCards should not be empty.");

        ClaimRouteTurn turn = ((ClaimRouteTurn) getTurn());

        assert turn.route.level() == Route.Level.UNDERGROUND;
        details.add("ADDCARDS:" + options + "/" + turn.additionalCards);
        routePlayAdditionalCardsOptions.set(routes.size() - 1, turn.additionalCards);
        return turn.additionalCards;
    }
}

class GameTest4 {
    Station stationA = new Station(1, "A");
    Station stationB = new Station(2, "B");
    Station stationC = new Station(3, "C");
    Station stationD = new Station(4, "D");
    Station stationE = new Station(5, "E");
    Station stationF = new Station(6, "F");
    Station stationG = new Station(7, "G");
    Station stationH = new Station(8, "H");

    Ticket A = new Ticket(stationA, stationB, 1);
    Ticket B = new Ticket(stationA, stationC, 2);
    Ticket C = new Ticket(stationA, stationD, 4);
    Ticket D = new Ticket(stationB, stationA, 8);
    Ticket E = new Ticket(stationE, stationA, 16);
    Ticket F = new Ticket(stationF, stationA, 32);
    Ticket G = new Ticket(stationG, stationD, 64);
    Ticket H = new Ticket(stationH, stationE, 128);
    Ticket J = new Ticket(stationB, stationE, 256);
    Ticket K = new Ticket(stationB, stationF, 512);
    SortedBag<Ticket> tickets = of(List.of(A,B,C,D,E,F,G,H,J,K,A,B,C,D,E,F,G,H,J,K));

    @Test
    public void sameBehavior() {
        Route routeA = new Route("a", stationA, stationB, 4, Route.Level.OVERGROUND, Color.RED);
        Route routeB = new Route("a", stationB, stationC, 2, Route.Level.UNDERGROUND, Color.RED);
        DummyPlayer Pb = new DummyPlayer(Arrays.asList(
                new DrawCardsTurn(-1, 2),
                new ClaimRouteTurn(new Route("a", stationA, stationB, 4, Route.Level.OVERGROUND, Color.RED), SortedBag.of(6, Card.RED))));

        Pb.setInitialTicketChooser(tickets -> SortedBag.of(A)); // take all tickets

        DummyPlayer Pa = new DummyPlayer(Arrays.asList(
                new DrawCardsTurn(-1, 2),
                new ClaimRouteTurn(new Route("a", stationA, stationB, 4, Route.Level.OVERGROUND, Color.RED), SortedBag.of(6, Card.RED))));
        Pa.setInitialTicketChooser(tickets -> SortedBag.of(A)); // only the first two

        Game.play(
                Map.of(PlayerId.PLAYER_1, Pa, PlayerId.PLAYER_2, Pb),
                Map.of(PlayerId.PLAYER_1, "A", PlayerId.PLAYER_2, "B"),
                tickets,
                new Random(1)
        );

        int readPointerA = 0;
        int readPointerB = 0;

        // init player
        assertEquals("INIT:PLAYER_1/PLAYERNAMES:A,B", Pa.details.get(readPointerA++));
        assertEquals("INIT:PLAYER_2/PLAYERNAMES:A,B", Pb.details.get(readPointerB++));

        // check of receive info at who begins
        PlayerId firstPlayer = null;

        if (("RECEIVED:" +new Info("A").willPlayFirst()).equals(Pa.details.get(1))) {
            firstPlayer = PlayerId.PLAYER_1;
        } else if (("RECEIVED:" +new Info("B").willPlayFirst()).equals(Pa.details.get(1))) {
            firstPlayer = PlayerId.PLAYER_2;
        }
        Assertions.assertNotNull(firstPlayer);

        readPointerA = readPointerB = 2;

        // update state before choseInitialTickets
        assertEquals("STATEUPDT:0", Pa.details.get(readPointerA++));
        assertEquals("STATEUPDT:0", Pb.details.get(readPointerB++));

        // chose initial tickets correctly
        assertEquals("INITIALTICKET", Pa.details.get(readPointerA++));
        assertEquals("INITIALTICKET", Pb.details.get(readPointerB++));

        assertTrue(Pa.details.subList(readPointerA, readPointerA + 2).contains("RECEIVED:" + new Info("A").keptTickets(1)));
        assertTrue(Pa.details.subList(readPointerA, readPointerA + 2).contains("RECEIVED:" + new Info("B").keptTickets(1)));
        assertTrue(Pa.details.subList(readPointerB, readPointerB + 2).contains("RECEIVED:" + new Info("A").keptTickets(1)));
        assertTrue(Pa.details.subList(readPointerB, readPointerB + 2).contains("RECEIVED:" + new Info("B").keptTickets(1)));
        readPointerA += 2; readPointerB += 2;

        // nextTurn should be called
        PlayerId currentPlayerId = firstPlayer;

        int countA = 0; int countB = 0;

        int isFinished = 2;
        while (isFinished != 0) {

            String pName = currentPlayerId == PlayerId.PLAYER_1 ? "A" : "B";
            DummyPlayer currentP = pName.equals("A") ? Pa : Pb;
            DummyPlayer otherP = pName.equals("B") ? Pa : Pb;
            int readPointerCurrent = pName.equals("A") ? readPointerA : readPointerB;
            int readPointerOther = pName.equals("A") ? readPointerB : readPointerA;


            if (isFinished == 1) {
                isFinished--;
            }

            if (currentP.details.get(readPointerCurrent).contains("le dernier tour commence")) {
                isFinished = 1;
                readPointerCurrent++;
                readPointerOther++;
            }
            int updateId = hasDoneUpdate(currentP, otherP, readPointerCurrent++, readPointerOther++);
            assertEquals("RECEIVED:" + new Info(pName).canPlay(), currentP.details.get(readPointerCurrent++));
            assertEquals("RECEIVED:" + new Info(pName).canPlay(), otherP.details.get(readPointerOther++));
            int[] alpha;
            TestTurn kind = currentP.list.get((pName.equals("A") ? countA : countB) % currentP.list.size());

            while(!kind.playable(currentP.states.get(updateId))) {
                if (pName.equals("A"))
                    countA++;
                else
                    countB++;
                kind = currentP.list.get((pName.equals("A") ? countA : countB) % currentP.list.size());
            }

            if (kind.kind() == Player.TurnKind.DRAW_CARDS) {
                alpha = testDrawCard(currentP, otherP, readPointerCurrent, readPointerOther, updateId);
            } else if (kind.kind() == Player.TurnKind.DRAW_TICKETS) {
                alpha = testDrawTickets(currentP, otherP, readPointerCurrent, readPointerOther, updateId);
            } else if (kind.kind() == Player.TurnKind.CLAIM_ROUTE) {
                alpha = testClaimRoute(currentP, otherP, readPointerCurrent, readPointerOther, updateId);
            } else {
                throw new Error();
            }


            if (pName.equals("A")) { countA++; } else { countB++; };

            readPointerCurrent = alpha[0]; readPointerOther = alpha[1];
            readPointerA = pName.equals("A") ? readPointerCurrent : readPointerOther;
            readPointerB = pName.equals("A") ? readPointerOther : readPointerCurrent;

            if (alpha[2] == 1) {
                currentPlayerId = currentPlayerId.next();
            }
        }

        hasDoneUpdate(Pa, Pb, readPointerA++, readPointerB++);

        assertTrue(Pa.details.subList(readPointerA, readPointerA + 2).contains("RECEIVED:" + new Info("A").getsLongestTrailBonus(Trail.longest(Arrays.asList(routeA)))));
        assertTrue(Pa.details.subList(readPointerA, readPointerA + 2).contains("RECEIVED:" + new Info("B").getsLongestTrailBonus(Trail.longest(Arrays.asList(routeA)))));
        assertTrue(Pb.details.subList(readPointerB, readPointerB + 2).contains("RECEIVED:" + new Info("A").getsLongestTrailBonus(Trail.longest(Arrays.asList(routeA)))));
        assertTrue(Pb.details.subList(readPointerB, readPointerB + 2).contains("RECEIVED:" + new Info("B").getsLongestTrailBonus(Trail.longest(Arrays.asList(routeA)))));
        readPointerA+=2;readPointerB+=2;

        Map<PlayerId, Integer> ranking = new HashMap<>();
        ranking.put(PlayerId.PLAYER_2, Pb.expectedPoints() + Constants.LONGEST_TRAIL_BONUS_POINTS);
        ranking.put(PlayerId.PLAYER_1, Pa.expectedPoints() + Constants.LONGEST_TRAIL_BONUS_POINTS);

        assertEquals(Pa.expectedPoints(), Pb.expectedPoints());

        // normally ex-aequo
        assertEquals(ranking.get(PlayerId.PLAYER_1).compareTo(ranking.get(PlayerId.PLAYER_2)), 0);
        List<String> playerNames = new ArrayList<>(List.of("A","B"));
        assertTrue(Pa.details.get(readPointerA).contains(ranking.get(PlayerId.PLAYER_1).toString()));
        assertTrue(Pa.details.get(readPointerA++).contains("sont ex"));
        assertTrue(Pb.details.get(readPointerB).contains(ranking.get(PlayerId.PLAYER_1).toString()));
        assertTrue(Pb.details.get(readPointerB++).contains("sont ex"));

    }

    @Test
    public void gameWhereBWinsOverA() {
        Route routeA = new Route("a", stationA, stationB, 4, Route.Level.OVERGROUND, Color.RED);
        Route routeB = new Route("a", stationB, stationC, 2, Route.Level.UNDERGROUND, Color.RED);
        DummyPlayer Pb = new DummyPlayer(Arrays.asList(
                new DrawCardsTurn(1, 2),
                new ClaimRouteTurn(new Route("a", stationA, stationB, 4, Route.Level.OVERGROUND, Color.RED), SortedBag.of(6, Card.RED)),
                new ClaimRouteTurn(new Route("a", stationB, stationC, 2, Route.Level.UNDERGROUND, Color.RED), SortedBag.of(6, Card.RED), SortedBag.of(2, Card.GREEN))));

        Pb.setInitialTicketChooser(tickets -> tickets); // take all tickets

        DummyPlayer Pa = new DummyPlayer(Arrays.asList(
                new DrawTicketsTurn(tickets -> tickets.difference(tickets.size() > 0 ? SortedBag.of(tickets.get(0)) : SortedBag.of())),
                new ClaimRouteTurn(new Route("a", stationA, stationB, 4, Route.Level.OVERGROUND, Color.RED), SortedBag.of(6, Card.RED), SortedBag.of())));

        Pa.setInitialTicketChooser(tickets -> SortedBag.of(1, tickets.get(0), 1, tickets.get(1))); // only the first two

        Game.play(
                Map.of(PlayerId.PLAYER_1, Pa, PlayerId.PLAYER_2, Pb),
                Map.of(PlayerId.PLAYER_1, "A", PlayerId.PLAYER_2, "B"),
                tickets,
                new Random(1)
        );

        int readPointerA = 0;
        int readPointerB = 0;

        // init player
        assertEquals("INIT:PLAYER_1/PLAYERNAMES:A,B", Pa.details.get(readPointerA++));
        assertEquals("INIT:PLAYER_2/PLAYERNAMES:A,B", Pb.details.get(readPointerB++));

        // check of receive info at who begins
        PlayerId firstPlayer = null;

        if (("RECEIVED:" +new Info("A").willPlayFirst()).equals(Pa.details.get(1))) {
            firstPlayer = PlayerId.PLAYER_1;
        } else if (("RECEIVED:" +new Info("B").willPlayFirst()).equals(Pa.details.get(1))) {
            firstPlayer = PlayerId.PLAYER_2;
        }
        Assertions.assertNotNull(firstPlayer);

        readPointerA = readPointerB = 2;

        // update state before choseInitialTickets
        assertEquals("STATEUPDT:0", Pa.details.get(readPointerA++));
        assertEquals("STATEUPDT:0", Pb.details.get(readPointerB++));

        // chose initial tickets correctly
        assertEquals("INITIALTICKET", Pa.details.get(readPointerA++));
        assertEquals("INITIALTICKET", Pb.details.get(readPointerB++));

        assertTrue(Pa.details.subList(readPointerA, readPointerA + 2).contains("RECEIVED:" + new Info("A").keptTickets(2)));
        assertTrue(Pa.details.subList(readPointerA, readPointerA + 2).contains("RECEIVED:" + new Info("B").keptTickets(5)));
        assertTrue(Pa.details.subList(readPointerB, readPointerB + 2).contains("RECEIVED:" + new Info("A").keptTickets(2)));
        assertTrue(Pa.details.subList(readPointerB, readPointerB + 2).contains("RECEIVED:" + new Info("B").keptTickets(5)));
        readPointerA += 2; readPointerB += 2;

        // nextTurn should be called
        PlayerId currentPlayerId = firstPlayer;

        int countA = 0; int countB = 0;

        int isFinished = 2;
        while (isFinished != 0) {
            String pName = currentPlayerId == PlayerId.PLAYER_1 ? "A" : "B";
            DummyPlayer currentP = pName.equals("A") ? Pa : Pb;
            DummyPlayer otherP = pName.equals("B") ? Pa : Pb;
            int readPointerCurrent = pName.equals("A") ? readPointerA : readPointerB;
            int readPointerOther = pName.equals("A") ? readPointerB : readPointerA;


            if (isFinished == 1) {
                isFinished--;
            }

            if (currentP.details.get(readPointerCurrent).contains("le dernier tour commence")) {
                isFinished = 1;
                readPointerCurrent++;
                readPointerOther++;
            }
            int updateId = hasDoneUpdate(currentP, otherP, readPointerCurrent++, readPointerOther++);
            assertEquals("RECEIVED:" + new Info(pName).canPlay(), currentP.details.get(readPointerCurrent++));
            assertEquals("RECEIVED:" + new Info(pName).canPlay(), otherP.details.get(readPointerOther++));
            int[] alpha;

            TestTurn kind = currentP.list.get((pName.equals("A") ? countA : countB) % currentP.list.size());

            while(!kind.playable(currentP.states.get(updateId))) {
                if (pName.equals("A"))
                    countA++;
                else
                    countB++;
                kind = currentP.list.get((pName.equals("A") ? countA : countB) % currentP.list.size());
            }

            if (kind.kind() == Player.TurnKind.DRAW_CARDS) {
                alpha = testDrawCard(currentP, otherP, readPointerCurrent, readPointerOther, updateId);
            } else if (kind.kind() == Player.TurnKind.DRAW_TICKETS) {
                alpha = testDrawTickets(currentP, otherP, readPointerCurrent, readPointerOther, updateId);
            } else if (kind.kind() == Player.TurnKind.CLAIM_ROUTE) {
                alpha = testClaimRoute(currentP, otherP, readPointerCurrent, readPointerOther, updateId);
            } else {
                throw new Error();
            }


            if (pName.equals("A")) { countA++; } else { countB++; };

            readPointerCurrent = alpha[0]; readPointerOther = alpha[1];
            readPointerA = pName.equals("A") ? readPointerCurrent : readPointerOther;
            readPointerB = pName.equals("A") ? readPointerOther : readPointerCurrent;

            if (alpha[2] == 1) {
                currentPlayerId = currentPlayerId.next();
            }
        }

        hasDoneUpdate(Pa, Pb, readPointerA++, readPointerB++);
        // Normally, A has a the biggest trail
        assertEquals("RECEIVED:" + new Info("B").getsLongestTrailBonus(Trail.longest(Arrays.asList(routeA, routeB))), Pa.details.get(readPointerA++));
        assertEquals("RECEIVED:" + new Info("B").getsLongestTrailBonus(Trail.longest(Arrays.asList(routeA, routeB))), Pb.details.get(readPointerB++));

        Map<PlayerId, Integer> ranking = new HashMap<>();
        ranking.put(PlayerId.PLAYER_2, Pb.expectedPoints() + Constants.LONGEST_TRAIL_BONUS_POINTS);
        ranking.put(PlayerId.PLAYER_1, Pa.expectedPoints());

        // normally A
        assertEquals(ranking.get(PlayerId.PLAYER_1).compareTo(ranking.get(PlayerId.PLAYER_2)), -1);
        assertEquals("RECEIVED:" + new Info("B").won(ranking.get(PlayerId.PLAYER_2), ranking.get(PlayerId.PLAYER_1)), Pa.details.get(readPointerA++));
        assertEquals("RECEIVED:" + new Info("B").won(ranking.get(PlayerId.PLAYER_2), ranking.get(PlayerId.PLAYER_1)), Pb.details.get(readPointerB++));

    }

    @Test
    public void firstGame() {
        Route routeB = new Route("a", stationB, stationC, 2, Route.Level.UNDERGROUND, Color.RED);
        Route routeA = new Route("a", stationA, stationB, 4, Route.Level.OVERGROUND, Color.RED);
        DummyPlayer Pa = new DummyPlayer(Arrays.asList(
                new DrawCardsTurn(1, 2),
                new ClaimRouteTurn(new Route("a", stationA, stationB, 4, Route.Level.OVERGROUND, Color.RED), SortedBag.of(6, Card.RED)),
                new ClaimRouteTurn(new Route("a", stationB, stationC, 2, Route.Level.UNDERGROUND, Color.RED), SortedBag.of(6, Card.RED), SortedBag.of(2, Card.GREEN))));

        Pa.setInitialTicketChooser(tickets -> tickets); // take all tickets

        DummyPlayer Pb = new DummyPlayer(Arrays.asList(
                new DrawTicketsTurn(tickets -> tickets.difference(tickets.size() > 0 ? SortedBag.of(tickets.get(0)) : SortedBag.of())),
                new ClaimRouteTurn(new Route("a", stationA, stationB, 4, Route.Level.OVERGROUND, Color.RED), SortedBag.of(6, Card.RED), SortedBag.of())));

        Pb.setInitialTicketChooser(tickets -> SortedBag.of(1, tickets.get(0), 1, tickets.get(1))); // only the first two

        Game.play(
                Map.of(PlayerId.PLAYER_1, Pa, PlayerId.PLAYER_2, Pb),
                Map.of(PlayerId.PLAYER_1, "A", PlayerId.PLAYER_2, "B"),
                tickets,
                new Random(1)
        );

        int readPointerA = 0;
        int readPointerB = 0;

        // init player
        assertEquals("INIT:PLAYER_1/PLAYERNAMES:A,B", Pa.details.get(readPointerA++));
        assertEquals("INIT:PLAYER_2/PLAYERNAMES:A,B", Pb.details.get(readPointerB++));

        // check of receive info at who begins
        PlayerId firstPlayer = null;

        if (("RECEIVED:" +new Info("A").willPlayFirst()).equals(Pa.details.get(1))) {
            firstPlayer = PlayerId.PLAYER_1;
        } else if (("RECEIVED:" +new Info("B").willPlayFirst()).equals(Pa.details.get(1))) {
            firstPlayer = PlayerId.PLAYER_2;
        }
        Assertions.assertNotNull(firstPlayer);

        readPointerA = readPointerB = 2;

        // update state before choseInitialTickets
        assertEquals("STATEUPDT:0", Pa.details.get(readPointerA++));
        assertEquals("STATEUPDT:0", Pb.details.get(readPointerB++));

        // chose initial tickets correctly
        assertEquals("INITIALTICKET", Pa.details.get(readPointerA++));
        assertEquals("INITIALTICKET", Pb.details.get(readPointerB++));

        assertTrue(Pa.details.subList(readPointerA, readPointerA + 2).contains("RECEIVED:" + new Info("A").keptTickets(5)));
        assertTrue(Pa.details.subList(readPointerA, readPointerA + 2).contains("RECEIVED:" + new Info("B").keptTickets(2)));
        assertTrue(Pa.details.subList(readPointerB, readPointerB + 2).contains("RECEIVED:" + new Info("A").keptTickets(5)));
        assertTrue(Pa.details.subList(readPointerB, readPointerB + 2).contains("RECEIVED:" + new Info("B").keptTickets(2)));
        readPointerA += 2; readPointerB += 2;

        // nextTurn should be called
        PlayerId currentPlayerId = firstPlayer;

        int countA = 0; int countB = 0;

        int isFinished = 2;
        while (isFinished != 0) {
            String pName = currentPlayerId == PlayerId.PLAYER_1 ? "A" : "B";
            DummyPlayer currentP = pName.equals("A") ? Pa : Pb;
            DummyPlayer otherP = pName.equals("B") ? Pa : Pb;
            int readPointerCurrent = pName.equals("A") ? readPointerA : readPointerB;
            int readPointerOther = pName.equals("A") ? readPointerB : readPointerA;


            if (isFinished == 1) {
                isFinished--;
            }

            if (currentP.details.get(readPointerCurrent).contains("le dernier tour commence")) {
                isFinished = 1;
                readPointerCurrent++;
                readPointerOther++;
            }
            int updateId = hasDoneUpdate(currentP, otherP, readPointerCurrent++, readPointerOther++);
            assertEquals("RECEIVED:" + new Info(pName).canPlay(), currentP.details.get(readPointerCurrent++));
            assertEquals("RECEIVED:" + new Info(pName).canPlay(), otherP.details.get(readPointerOther++));
            int[] alpha;

            TestTurn kind = currentP.list.get((pName.equals("A") ? countA : countB) % currentP.list.size());

            while(!kind.playable(currentP.states.get(updateId))) {
                if (pName.equals("A"))
                    countA++;
                else
                    countB++;
                kind = currentP.list.get((pName.equals("A") ? countA : countB) % currentP.list.size());
            }

            if (kind.kind() == Player.TurnKind.DRAW_CARDS) {
                alpha = testDrawCard(currentP, otherP, readPointerCurrent, readPointerOther, updateId);
            } else if (kind.kind() == Player.TurnKind.DRAW_TICKETS) {
                alpha = testDrawTickets(currentP, otherP, readPointerCurrent, readPointerOther, updateId);
            } else if (kind.kind() == Player.TurnKind.CLAIM_ROUTE) {
                alpha = testClaimRoute(currentP, otherP, readPointerCurrent, readPointerOther, updateId);
            } else {
                throw new Error();
            }


            if (pName.equals("A")) { countA++; } else { countB++; };

            readPointerCurrent = alpha[0]; readPointerOther = alpha[1];
            readPointerA = pName.equals("A") ? readPointerCurrent : readPointerOther;
            readPointerB = pName.equals("A") ? readPointerOther : readPointerCurrent;

            if (alpha[2] == 1) {
                currentPlayerId = currentPlayerId.next();
            }
        }

        hasDoneUpdate(Pa, Pb, readPointerA++, readPointerB++);
        // Normally, A has a the biggest trail
        assertEquals("RECEIVED:" + new Info("A").getsLongestTrailBonus(Trail.longest(Arrays.asList(routeA, routeB))), Pa.details.get(readPointerA++));
        assertEquals("RECEIVED:" + new Info("A").getsLongestTrailBonus(Trail.longest(Arrays.asList(routeA, routeB))), Pb.details.get(readPointerB++));

        Map<PlayerId, Integer> ranking = new HashMap<>();
        ranking.put(PlayerId.PLAYER_1, Pa.expectedPoints() + Constants.LONGEST_TRAIL_BONUS_POINTS);
        ranking.put(PlayerId.PLAYER_2, Pb.expectedPoints());

        // normally A
        assertEquals(ranking.get(PlayerId.PLAYER_1).compareTo(ranking.get(PlayerId.PLAYER_2)), 1);
        assertEquals("RECEIVED:" + new Info("A").won(ranking.get(PlayerId.PLAYER_1), ranking.get(PlayerId.PLAYER_2)), Pa.details.get(readPointerA++));
        assertEquals("RECEIVED:" + new Info("A").won(ranking.get(PlayerId.PLAYER_1), ranking.get(PlayerId.PLAYER_2)), Pb.details.get(readPointerB++));

    }

    private static int[] testClaimRoute(DummyPlayer playerWhoTakesRoute, DummyPlayer spec, int readPointerA, int readPointerB, int state) {
        DummyPlayer pA = playerWhoTakesRoute; DummyPlayer pB = spec;

        assertEquals("TURN:" + Player.TurnKind.CLAIM_ROUTE, pA.details.get(readPointerA++));
        assertTrue(pA.details.get(readPointerA).startsWith("CLAIMROUTE:"));

        int index = Integer.parseInt(pA.details.get(readPointerA++).substring("CLAIMROUTE:".length()));
        assertEquals(index, Integer.parseInt(pA.details.get(readPointerA++).substring("ROUTECARDS:".length())));
        if (pA.routes.get(index).level() == Route.Level.OVERGROUND) {
            assertEquals("RECEIVED:" + new Info(pA.name).claimedRoute(pA.routes.get(index), pA.routeCards.get(index)), pA.details.get(readPointerA++));
            assertEquals("RECEIVED:" + new Info(pA.name).claimedRoute(pA.routes.get(index), pA.routeCards.get(index)), pB.details.get(readPointerB++));
        } else {
            assertEquals("RECEIVED:" + new Info(pA.name).attemptsTunnelClaim(pA.routes.get(index), pA.routeCards.get(index)), pA.details.get(readPointerA++));
            assertEquals("RECEIVED:" + new Info(pA.name).attemptsTunnelClaim(pA.routes.get(index), pA.routeCards.get(index)), pB.details.get(readPointerB++));

            System.out.println("Please check:");
            System.out.println(pA.details.get(readPointerA++));
            readPointerB++; //


            // two ways
            if (pA.details.get(readPointerA).startsWith("ADDCARDS:")) {
                // needed additional cards
                readPointerA++;
                SortedBag<Card> additionalCards = pA.routePlayAdditionalCardsOptions.get(index);

                if (additionalCards.isEmpty()) {
                    assertEquals("RECEIVED:" + new Info(pA.name).didNotClaimRoute(pA.routes.get(index)), pA.details.get(readPointerA++));
                    assertEquals("RECEIVED:" + new Info(pA.name).didNotClaimRoute(pA.routes.get(index)), pB.details.get(readPointerB++));
                } else {
                    assertEquals("RECEIVED:" + new Info(pA.name).claimedRoute(pA.routes.get(index), pA.routeCards.get(index).union(additionalCards)), pA.details.get(readPointerA++));
                    assertEquals("RECEIVED:" + new Info(pA.name).claimedRoute(pA.routes.get(index), pA.routeCards.get(index).union(additionalCards)), pB.details.get(readPointerB++));
                }
            } else {
                assertEquals("RECEIVED:" + new Info(pA.name).claimedRoute(pA.routes.get(index), pA.routeCards.get(index)), pA.details.get(readPointerA++));
                assertEquals("RECEIVED:" + new Info(pA.name).claimedRoute(pA.routes.get(index), pA.routeCards.get(index)), pB.details.get(readPointerB++));
            }
        }
        return new int[]{ readPointerA, readPointerB, 1 };
    }

    private static int[] testDrawTickets(DummyPlayer playerWhoTookCards, DummyPlayer spec, int readPointerA, int readPointerB, int stateId) {
        DummyPlayer pA = playerWhoTookCards; DummyPlayer pB = spec;

        assertEquals("TURN:" + Player.TurnKind.DRAW_TICKETS, pA.details.get(readPointerA++));
        int success = 0;
        if (pA.details.get(readPointerA).startsWith("CHOSE-TICKETS-FROM:")) {
            success = 1;
            int index = Integer.parseInt(pA.details.get(readPointerA++).substring("CHOSE-TICKETS-FROM:".length()));
            SortedBag<Ticket> availableTickets = pA.ticketsOption.get(index);
            SortedBag<Ticket> chosenTickets = pA.ticketsChosen.get(index);

            assertEquals("RECEIVED:" + new Info(pA.name).drewTickets(availableTickets.size()), pA.details.get(readPointerA++));
            assertEquals("RECEIVED:" + new Info(pA.name).keptTickets(chosenTickets.size()), pA.details.get(readPointerA++));
            assertEquals("RECEIVED:" + new Info(pA.name).drewTickets(availableTickets.size()), pB.details.get(readPointerB++));
            assertEquals("RECEIVED:" + new Info(pA.name).keptTickets(chosenTickets.size()), pB.details.get(readPointerB++));
        }
        return new int[]{ readPointerA, readPointerB, success };
    }

    private static int[] testDrawCard(DummyPlayer playerWhoTookCards, DummyPlayer spec, int readPointerA, int readPointerB, int stateId) {
        DummyPlayer pA = playerWhoTookCards; DummyPlayer pB = spec;

        assertEquals("TURN:" + Player.TurnKind.DRAW_CARDS, pA.details.get(readPointerA++));

        int success = 0;
        if(pA.details.get(readPointerA).startsWith("DRAW:")) {
            success = 1;
            List<Integer> takenSlots = Arrays.stream(pA.details.get(readPointerA++).substring("DRAW:".length()).split("/")).map(Integer::parseInt).collect(Collectors.toList());

            assertEquals(expectedMessageCards(pA, stateId, takenSlots.get(0)), pA.details.get(readPointerA++));
            assertEquals(expectedMessageCards(pA, stateId, takenSlots.get(0)), pB.details.get(readPointerB++));

            int numberOfCards = pA.ownstates.get(stateId).cards().size();
            stateId = hasDoneUpdate(pA, pB, readPointerA++, readPointerB++);
            assertEquals(numberOfCards + 1, pA.ownstates.get(stateId).cards().size());
            assertEquals("DRAW:" + takenSlots.stream().map(Object::toString).collect(Collectors.joining("/")), pA.details.get(readPointerA++));
            assertEquals(expectedMessageCards(pA, stateId, takenSlots.get(1)), pA.details.get(readPointerA++));
            assertEquals(expectedMessageCards(pA, stateId, takenSlots.get(1)), pB.details.get(readPointerB++));
            assertEquals(numberOfCards + 2, pA.ownstates.get(stateId + 1).cards().size());
        }

        return new int[]{ readPointerA, readPointerB, success };
    }

    private static String expectedMessageCards(DummyPlayer p, int stateId, int slot) {
        PublicGameState state = p.states.get(stateId);
        if (slot == -1)
            return "RECEIVED:" + new Info(p.name).drewBlindCard();
        else
            return "RECEIVED:" + new Info(p.name).drewVisibleCard(state.cardState().faceUpCard(slot));
    }

    private static int hasDoneUpdate(DummyPlayer a, DummyPlayer b, int readPointerA, int readPointerB) {
        assertTrue(a.details.get(readPointerA++).startsWith("STATEUPDT:"));

        assertTrue(b.details.get(readPointerB++).startsWith("STATEUPDT:"));
        return Integer.parseInt(a.details.get(readPointerA-1).substring("STATEUPDT:".length()));
    }
}

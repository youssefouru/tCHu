package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

class NoPossibleAction extends IllegalStateException
{}

class TestPlayer implements Player
{
    private static final int TURN_LIMIT = 1000;
    private final Queue<String> expectedInfoQueue;
    private int turnCount = 0;
    private PlayerId id;
    private String name, otherPlayerName;

    private PlayerState playerState;
    private PublicGameState gameState;

    private SortedBag<Ticket> initialTicketChoice;
    private Route claimedRoute;
    private SortedBag<Card> claimCards;

    private Info info;

    public TestPlayer()
    {
        expectedInfoQueue = new ArrayDeque<>();
    }
    public boolean testIsSuccessful()
    {
        return expectedInfoQueue.isEmpty();
    }

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames)
    {
        id = ownId;
        name = playerNames.get(id);
        otherPlayerName = playerNames.get(id.next());
        info = new Info(name);
    }
    @Override
    public void receiveInfo(String info)
    {
        if (id == PlayerId.PLAYER_1)
        {
            System.out.print(info);
        }
        if (info.equals(expectedInfoQueue.peek()))
            expectedInfoQueue.poll();
    }
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState)
    {
        gameState = newState;
        playerState = ownState;
    }
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets)
    {
        initialTicketChoice = tickets;
    }
    @Override
    public SortedBag<Ticket> chooseInitialTickets()
    {
        int chosenTicketsCount = new Random().nextInt(Constants.INITIAL_TICKETS_COUNT - 3) + 3;
        SortedBag.Builder<Ticket> sbb = new SortedBag.Builder<>();

        for (int i = 0; i < chosenTicketsCount; ++i)
        {
            int chosenTicket = new Random().nextInt(initialTicketChoice.size());
            sbb.add(initialTicketChoice.get(chosenTicket));
            initialTicketChoice = initialTicketChoice.difference(SortedBag.of(initialTicketChoice.get(chosenTicket)));
        }

        expectedInfoQueue.offer(info.keptTickets(chosenTicketsCount));

        return sbb.build();
    }
    @Override
    public TurnKind nextTurn()
    {
        ++turnCount;
        if (turnCount > TURN_LIMIT)
            throw new Error("Too many turns");

        List<Route> claimableRoutes =
                ChMap.routes().stream()
                        .filter(route -> playerState.canClaimRoute(route) && !gameState.claimedRoutes().contains(route))
                        .collect(Collectors.toList());

        if (!claimableRoutes.isEmpty())
        {
            claimedRoute = claimableRoutes.get(new Random().nextInt(claimableRoutes.size()));

            List<SortedBag<Card>> possibleClaimCards = playerState.possibleClaimCards(claimedRoute);
            claimCards = possibleClaimCards.get(new Random().nextInt(possibleClaimCards.size()));

            if (claimedRoute.level() == Route.Level.OVERGROUND)
                expectedInfoQueue.offer(info.claimedRoute(claimedRoute, claimCards));
            else
                expectedInfoQueue.offer(info.attemptsTunnelClaim(claimedRoute, claimCards));

            return TurnKind.CLAIM_ROUTE;
        }


        int action = new Random().nextInt(10);

        if (gameState.canDrawTickets() && action == 0)
            return TurnKind.DRAW_TICKETS;

        if (gameState.cardState().discardsSize() + gameState.cardState().deckSize() > 5)
            return TurnKind.DRAW_CARDS;
        else
            throw new NoPossibleAction();
    }
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options)
    {
        int chosenTicketsCount = new Random().nextInt(options.size() - 1) + 1;
        SortedBag.Builder<Ticket> sbb = new SortedBag.Builder<>();

        for (int i = 0; i < chosenTicketsCount; ++i)
        {
            int chosenTicket = new Random().nextInt(options.size());
            sbb.add(options.get(chosenTicket));
            options = options.difference(SortedBag.of(options.get(chosenTicket)));
        }

        expectedInfoQueue.offer(info.drewTickets(chosenTicketsCount));

        return sbb.build();
    }
    @Override
    public int drawSlot()
    {
        int chosenSlot = new Random().nextInt(6) - 1;

        if (chosenSlot == -1)
            expectedInfoQueue.offer(info.drewBlindCard());
        else
            expectedInfoQueue.offer(info.drewVisibleCard(gameState.cardState().faceUpCard(chosenSlot)));

        return chosenSlot;
    }
    @Override
    public Route claimedRoute()
    {
        return claimedRoute;
    }
    @Override
    public SortedBag<Card> initialClaimCards()
    {
        return claimCards;
    }
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options)
    {
        List<SortedBag<Card>> playableOptions =
                options.stream()
                        .filter(sb -> playerState.cards().contains(sb))
                        .collect(Collectors.toList());

        if (playableOptions.isEmpty())
        {
            expectedInfoQueue.offer(info.didNotClaimRoute(claimedRoute));
            return SortedBag.of();
        }
        else
        {
            SortedBag<Card> chosenOption = playableOptions.get(new Random().nextInt(playableOptions.size()));
            expectedInfoQueue.offer(info.claimedRoute(claimedRoute, claimCards.union(chosenOption)));
            return chosenOption;
        }
    }
}

public class GameTest5 {
    @Test
    void testGame()
    {
        for (int i = 0; i < 100; ++i)
        {
            Map<PlayerId, String> names = Map.of
                    (
                            PlayerId.PLAYER_1, "Joueur 1",
                            PlayerId.PLAYER_2, "Joueur 2"
                    );
            Map<PlayerId, Player> players = Map.of
                    (
                            PlayerId.PLAYER_1, new TestPlayer(),
                            PlayerId.PLAYER_2, new TestPlayer()
                    );

            Game.play(players, names, SortedBag.of(ChMap.tickets()), new Random());

            assertTrue(((TestPlayer) players.get(PlayerId.PLAYER_1)).testIsSuccessful());
            assertTrue(((TestPlayer) players.get(PlayerId.PLAYER_2)).testIsSuccessful());
        }
    }
}
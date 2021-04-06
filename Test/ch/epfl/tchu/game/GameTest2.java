package ch.epfl.tchu.game;


import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest2 {

    // These tests are executed by running a predefined number of randomised games. If a single predetermined run
    // of a game is desired, set RANDOMISED_TESTS to false. Note that due to the random process, it is sometimes
    // possible that the number of turns is exceeded because the TestPlayers try to claim tunnels for which they
    // do not possess the required additionnal cards. This is due to the fact that TestPlayer only draws a card
    // if there are no routes available to be claimed with the current cards in hand. This sends it into a loop
    // where it tries many times to claim the tunnel, hoping for no additionnal claim cards, and this can exceed
    // the given turn limit and throw and exception.

    private static final boolean RANDOMISED_TESTS = true;
    private static final int RANDOM_TEST_COUNT = 1000;

    // Private class which generates a testing player. The testing works by testing internally if, according
    // from what the player knows, the game runs correctly.
    //
    // The things the player knows are:
    // -The amount of turns
    // -The amount of remaining tickets (not testable because no ticket discard exists)
    // -The current players' ID
    // -The routes claimed by both players
    // -The number of tickets both players have (not testable, see above)
    // -The number of cards each player has
    // -The routes claimed by each player
    // -The amount of remaining cars each player has
    // -The amount of points each player has
    // -The last player, if available
    // -The amount of cards that none of the players own
    // -The cards that are currently face-up (not testable, nothing to compare with)
    // -The number of remaining cards in the deck (not testable, same as above)
    // -The size of the discard pile (not testable, same as above)
    // -If the deck is empty or not (not testable, same as above)


    private static final class TestPlayer implements Player {
        private static final int TURN_LIMIT = 2000;

        private final Random rng;
        // Toutes les routes de la carte
        private final List<Route> allRoutes;
        private List<Route> routesclaimable;
        private int turnCounter;
        private PlayerState ownState;
        private PublicGameState gameState;
        private Map<PlayerId, String> playerNames;
        int v = 0;
        private boolean draw = false;

        // Lorsque nextTurn retourne CLAIM_ROUTE
        private Route routeToClaim;
        private SortedBag<Card> initialClaimCards;

        private SortedBag<Ticket> initialTickets;

        private String ownPlayerName;
        private String otherPlayersName;

        public TestPlayer(long seed, List<Route> allRoutes) {
            this.rng = new Random(seed);
            this.allRoutes = new ArrayList<>(allRoutes);
            this.turnCounter = 0;
        }

        @Override
        public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
            this.ownPlayerName = playerNames.get(ownId);
            this.otherPlayersName = playerNames.get(ownId.next());
            this.playerNames = playerNames;
            receiveInfo("Vous jouez en tant que " + ownPlayerName + ". Votre adversaire est " + otherPlayersName);
        }

        @Override
        public void receiveInfo(String info) {
            System.out.println("Recieve info (" + ownPlayerName + "): " + info);
        }

        @Override
        public void updateState(PublicGameState newState, PlayerState ownState) {
            this.gameState = newState;
            this.ownState = ownState;
        }

        @Override
        public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
            this.initialTickets = tickets;
        }

        @Override
        public SortedBag<Ticket> chooseInitialTickets() {
            Random random = new Random();
            SortedBag.Builder<Ticket> out = new SortedBag.Builder<>();
            while (out.size() < Math.min(3, initialTickets.size())) {
                int index = random.nextInt(initialTickets.size());
                out.add(initialTickets.get(index));
                initialTickets = initialTickets.difference(out.build());
            }

            return out.build();
        }

        @Override
        public TurnKind nextTurn() {

            // |----ALL TESTS GO HERE AS THIS METHOD IS ACCESSED ON EVERY PLAYERS' TURN----|

            // Turn limit (terminates the game)
            turnCounter += 1;
            if (turnCounter > TURN_LIMIT)
                throw new Error("Trop de tours joués !");


            // Current players' ID (assumes that players have different name strings)
            PlayerId ownPlayerId = null;
            PlayerId otherPlayerId = null;
            for (Map.Entry<PlayerId, String> playerName : playerNames.entrySet()) {
                if (playerName.getValue().equals(ownPlayerName)) {
                    ownPlayerId = playerName.getKey();
                    otherPlayerId = playerName.getKey().next();
                } else {
                    otherPlayerId = playerName.getKey();
                    ownPlayerId = otherPlayerId.next();
                }
            }
            assertEquals(gameState.currentPlayerId(), ownPlayerId);
            assertEquals(gameState.currentPlayerId().next(), otherPlayerId);


            // The routes claimed by both players
            assertTrue(gameState.claimedRoutes().containsAll(gameState.playerState(gameState.currentPlayerId()).routes()));
            assertTrue(gameState.claimedRoutes().containsAll(gameState.playerState(gameState.currentPlayerId().next()).routes()));

            // The number of cards both players have
            int ownCardsCount = ownState.cardCount();
            assertEquals(gameState.currentPlayerState().cardCount(), ownCardsCount);


            // The routes each player has claimed
            assertEquals(gameState.currentPlayerState().routes(), ownState.routes());


            // The amount of remaining cars each player has
            assertEquals(gameState.currentPlayerState().carCount(), ownState.carCount());


            // The amount of points each player has
            assertEquals(gameState.currentPlayerState().claimPoints(), ownState.claimPoints());


            // Last player check
            int ownCarCount = ownState.carCount();
            int otherCarCount = gameState.playerState(otherPlayerId).carCount();
            if (ownCarCount <= 2) {
                assertEquals(ownPlayerId, gameState.lastPlayer());
            }

            if (otherCarCount <= 2 && !(ownCarCount <= 2)) {
                assertEquals(otherPlayerId, gameState.lastPlayer());
            }

            // |----END OF ALL TESTS----|


            // Détermine les routes dont ce joueur peut s'emparer
            List<Route> claimableRoutes = new ArrayList<>();
            for (Route route : allRoutes) {
                if (ownState.canClaimRoute(route) && !gameState.claimedRoutes().contains(route)) {
                    claimableRoutes.add(route);
                }
            }

            routesclaimable = claimableRoutes;

            if (routesclaimable.size() == 0) {
                return TurnKind.DRAW_CARDS;

            } else if (!claimableRoutes.isEmpty()) {
                Random random = new Random();
                int routeIndex = random.nextInt(claimableRoutes.size());
                routeToClaim = claimableRoutes.get(routeIndex);
                List<SortedBag<Card>> cards = ownState.possibleClaimCards(routeToClaim);
                int index = random.nextInt(cards.size());
                initialClaimCards = cards.get(index);
                claimableRoutes.remove(routeToClaim);
                return TurnKind.CLAIM_ROUTE;
            }else return TurnKind.DRAW_TICKETS;
        }


        @Override
        public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
            int index1 = rng.nextInt(options.size() - 1) + 1;
            int index2 = rng.nextInt(index1);
            return SortedBag.of(options.toList().subList(index1, index2));
        }

        @Override
        public int drawSlot() {
            int index = rng.nextInt(Constants.FACE_UP_CARD_SLOTS.size());
            return Constants.FACE_UP_CARD_SLOTS.get(index);
        }

        @Override
        public Route claimedRoute() {

            return routeToClaim;
        }

        @Override
        public SortedBag<Card> initialClaimCards() {
            return initialClaimCards;
        }

        @Override
        public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
            if(!options.isEmpty())
            {
                int index = rng.nextInt(options.size());
                return options.get(index);
            }
            else
            {
                return SortedBag.of();
            }
        }
    }

    @Test
    public void GameTests()
    {
        // Note: these tests are randomised. The seed given to the TestPlayer instances is randomised, so they are too.
        // Every game is random. If this is not desired, set RANDOMISED_TESTS to false. This will run a single
        // predefined game with predefined TestPlayer instances (predefined seed).

        if(RANDOMISED_TESTS)
        {

            for(int i = 0; i < RANDOM_TEST_COUNT; i++)
            {
                Random rng = new Random(6);

                Map<PlayerId, Player> players = new HashMap<>();
                players.put(PlayerId.PLAYER_1, new TestPlayer(6, ChMap.routes()));
                players.put(PlayerId.PLAYER_2, new TestPlayer(6, ChMap.routes()));

                Map<PlayerId, String> playerNames = new HashMap<>();
                playerNames.put(PlayerId.PLAYER_1, "Lachowska");
                playerNames.put(PlayerId.PLAYER_2, "Urech");

                Game.play(players, playerNames, SortedBag.of(ChMap.tickets()), rng);
            }
        }
        else
        {
            Random rng = new Random(6);
            long seed = rng.nextLong();
            Map<PlayerId, Player> players = new HashMap<>();
            players.put(PlayerId.PLAYER_1, new TestPlayer(6,ChMap.routes()));
            players.put(PlayerId.PLAYER_2, new TestPlayer(6, ChMap.routes()));

            Map<PlayerId, String> playerNames = new HashMap<>();
            playerNames.put(PlayerId.PLAYER_1, "Lachowska");
            playerNames.put(PlayerId.PLAYER_2, "Urech");

            Game.play(players, playerNames, SortedBag.of(ChMap.tickets()), rng);
        }

    }

}

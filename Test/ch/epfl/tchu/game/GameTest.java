package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameTest {

    @Test
    void playWorks(){
    }

    public static class TestRandomPlayer implements Player {

        private static final int TURN_LIMIT = 1000;

        private final Random rng;
        // Toutes les routes de la carte
        private final List<Route> allRoutes;
        private SortedBag<Ticket> initialTickets;

        private int numberOfInfo;
        private int turnCounter;
        private PlayerState ownState;
        private PublicGameState gameState;

        // Lorsque nextTurn retourne CLAIM_ROUTE
        private Route routeToClaim;
        private SortedBag<Card> initialClaimCards;

        PlayerId ownId;
        Map<PlayerId, String> playerNames;


        public TestRandomPlayer(Random randomSeed, List<Route> allRoutes) {
            this.rng = randomSeed;
            this.allRoutes = List.copyOf(allRoutes);
            this.turnCounter = 0;
            this.numberOfInfo = 0;

        }

        @Override
        public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
            this.ownId = ownId;
            this.playerNames = playerNames;
        }

        @Override
        public void receiveInfo(String info) {
            numberOfInfo++;


        }

        @Override
        public void updateState(PublicGameState newState, PlayerState ownState) {
            this.gameState = newState;
            this.ownState = ownState;
        }


        @Override
        public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
            initialTickets = tickets;
        }

        public void initiationWorks() {
            if (turnCounter==0){
                boolean works = true;
                assert(this.ownState.carCount() == 40);
                assert(this.ownState.cards().size() == 4);
            }

        }
        @Override
        public SortedBag<Ticket> chooseInitialTickets() {
            Stream<Ticket> chosenTicketsFloat =  initialTickets.stream().filter(x -> {
                return rng.nextBoolean();});

            return SortedBag.of(chosenTicketsFloat.collect(Collectors.toList()));

        }

        @Override
        public TurnKind nextTurn() {
            turnCounter += 1;
            if (turnCounter > TURN_LIMIT)
                throw new Error("Trop de tours joués !");

            // Détermine les routes dont ce joueur peut s'emparer
            List<Route> claimableRoutes = claimableRoutes();

            if (rng.nextInt() % 3 == 0) { // draw tichets 1/3 of the time for test purpose
                return TurnKind.DRAW_TICKETS;
            }
            if (claimableRoutes.isEmpty()) {
                return TurnKind.DRAW_CARDS;
            } else {
                int routeIndex = rng.nextInt(claimableRoutes.size());
                Route route = claimableRoutes.get(routeIndex);
                List<SortedBag<Card>> cards = ownState.possibleClaimCards(route);

                routeToClaim = route;
                initialClaimCards = cards.get(0);
                return TurnKind.CLAIM_ROUTES;
            }


        }

        @Override
        public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
            Stream<Ticket> chosenTicketsFloat =  options.stream().filter(x -> {
                return rng.nextBoolean();});

            return SortedBag.of(chosenTicketsFloat.collect(Collectors.toList()));

        }

        @Override
        public int drawSlot() {
            if (rng.nextBoolean() == false) {
                return -1 // draw a card from the deck
                        ;
            } else {
                return rng.nextInt(4);
            }
        }

        private List<Route> claimableRoutes() {
            List<Route> claimableRoutes = new ArrayList<>();
            allRoutes.forEach(s ->{
                        if (ownState.canClaimRoute(s)) {
                            claimableRoutes.add(s);
                        }
                    }
            );
            return claimableRoutes;
        }

        /**
         * Randomly select a route from the claimable routes
         * @return
         */
        @Override
        public Route claimedRoute() {
            routeToClaim = claimableRoutes().get(rng.nextInt(claimableRoutes().size()));
            return claimableRoutes().get(rng.nextInt(claimableRoutes().size()));
        }

        @Override
        public SortedBag<Card> initialClaimCards() {
            List<SortedBag<Card>> possibleCardsClaim = ownState.possibleClaimCards(routeToClaim);
            return possibleCardsClaim.get(possibleCardsClaim.size());}

        @Override
        public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
            return options.get(rng.nextInt(options.size()));
        }
    }
}

package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameTest2 {


    void exceptionsAreThrown() {
        Random rng = new Random();
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());
        //SortedBag<Ticket> tickets = SortedBag.of();
        assertThrows(IllegalArgumentException.class, () -> {
            Map<PlayerId, Player> invPlayers = new HashMap<>();
            Map<PlayerId, String> invPlayerNames = new HashMap<>();
            Game.play(invPlayers, invPlayerNames, tickets, rng);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Map<PlayerId, Player> invPlayers = new HashMap<>();
            invPlayers.put(PlayerId.PLAYER_2, new TestPlayer(1034234234, ChMap.routes()));
            Map<PlayerId, String> invPlayerNames = new HashMap<>();
            invPlayerNames.put(PlayerId.PLAYER_2, "aaa");
            Game.play(invPlayers, invPlayerNames, tickets, rng);
        });
    }


    void gameWorks() {
        Random rng = new Random();
        long seed = 2000000000;
        Map<PlayerId, Player> invPlayers = new HashMap<>();
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());
        Map<PlayerId, String> invPlayerNames = new HashMap<>();
        invPlayerNames.put(PlayerId.PLAYER_1, "Alice");
        invPlayerNames.put(PlayerId.PLAYER_2, "Bob");
        invPlayers.put(PlayerId.PLAYER_1, new TestPlayer(seed, ChMap.routes()));
        invPlayers.put(PlayerId.PLAYER_2, new TestPlayer(seed, ChMap.routes()));
        Game.play(invPlayers, invPlayerNames, tickets, rng);
        assertTrue(((TestPlayer) invPlayers.get(PlayerId.PLAYER_1)).infos() >= ((TestPlayer) invPlayers.get(PlayerId.PLAYER_1)).turnCounter());

    }

    void moyennePoints() {
        Random rng = new Random();
        int BEAUCOUP = 100;
        int moyenne = 0;
        int maxtours = 0;
        long seed = 1832743;
        for (int i = 0; i < BEAUCOUP; i++) {
            seed += i * 777;
            Map<PlayerId, Player> invPlayers = new HashMap<>();
            SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());
            Map<PlayerId, String> invPlayerNames = new HashMap<>();
            invPlayerNames.put(PlayerId.PLAYER_1, "Alice");
            invPlayerNames.put(PlayerId.PLAYER_2, "Bob");
            invPlayers.put(PlayerId.PLAYER_1, new TestPlayer(seed, ChMap.routes()));
            invPlayers.put(PlayerId.PLAYER_2, new TestPlayer(seed, ChMap.routes()));
            Game.play(invPlayers, invPlayerNames, tickets, rng);
            moyenne += ((TestPlayer) invPlayers.get(PlayerId.PLAYER_1)).pointsFin;
            assertTrue(((TestPlayer) invPlayers.get(PlayerId.PLAYER_1)).infos() >= ((TestPlayer) invPlayers.get(PlayerId.PLAYER_1)).turnCounter());
            maxtours = Math.max(maxtours, ((TestPlayer) invPlayers.get(PlayerId.PLAYER_1)).turnCounter);
        }
        moyenne /= BEAUCOUP;
        System.out.println(moyenne);
        System.out.println("maxtours = " + maxtours);
    }

    public static final class TestPlayer implements AdvancedPlayer {
        private static final int TURN_LIMIT = 1000;

        private final Random rng;
        // Toutes les routes de la carte
        private final List<Route> allRoutes;
        private int pointsFin;
        private boolean winner;
        private boolean ex;

        public int pointsFin() {
            return pointsFin;
        }

        public boolean winner() {
            return winner;
        }

        public boolean ex() {
            return ex;
        }

        public int turnCounter() {
            return turnCounter;
        }

        public int infos() {
            return infos;
        }

        private int infos;
        private int turnCounter;
        private PlayerState ownState;
        private PublicGameState gameState;


        //Rajoutés par moi
        private PlayerId ownId;
        private Map<PlayerId, String> playerNames;
        private SortedBag<Ticket> inTickets;

        // Lorsque nextTurn retourne CLAIM_ROUTE
        private Route routeToClaim;
        private SortedBag<Card> initialClaimCards;

        public TestPlayer(long randomSeed, List<Route> allRoutes) {
            this.rng = new Random(randomSeed);
            this.allRoutes = new ArrayList<>(allRoutes);
            this.turnCounter = 0;
        }

        @Override
        public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
            this.ownId = ownId;
            this.playerNames = playerNames;
        }

        @Override
        public void receiveInfo(String info) {
            infos++;
            //            System.out.println("Message pour "+ playerNames.get(ownId) + " : " + info);
            if (info.contains("victoire")) {
                System.out.println(info);
                Pattern p = Pattern.compile("-?\\d+");
                Matcher m = p.matcher(info);
                boolean first = true;
                while (m.find() && first) {
                    pointsFin = Integer.parseInt(m.group());
                    first = false;
                }

            }
        }

        @Override
        public void updateState(PublicGameState newState, PlayerState ownState) {
            this.gameState = newState;
            this.ownState = ownState;
        }

        @Override
        public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
            inTickets = tickets;
        }

        @Override
        public SortedBag<Ticket> chooseInitialTickets() {
            List<Ticket> inT = new ArrayList<>();
            //            System.out.println(inTickets.size());
            int numb = 0;
            for (Ticket inTicket : inTickets) {
                if (rng.nextInt(2) == 0 || numb > 1) {
                    inT.add(inTicket);
                } else
                    numb++;
            }
            //            System.out.println(inT.size() + " aa");
            return SortedBag.of(inT);
        }

        @Override
        public TurnKind nextTurn() {
            turnCounter += 1;
            if (turnCounter > TURN_LIMIT)
                throw new Error("Trop de tours joués !");

            // Détermine les routes dont ce joueur peut s'emparer
            allRoutes.removeIf(next -> !ownState.canClaimRoute(next) || gameState.claimedRoutes().contains(next));
            //            if (turnCounter < 5)
            //                return TurnKind.DRAW_TICKETS;
            if (rng.nextInt(50) == 1 && gameState.canDrawTickets())
                return TurnKind.DRAW_TICKETS;
            if (allRoutes.isEmpty() && gameState.canDrawCards()) {
                return TurnKind.DRAW_CARDS;
            } else if (!allRoutes.isEmpty()){
                int routeIndex = rng.nextInt(allRoutes.size());
                Route route = allRoutes.get(routeIndex);
                List<SortedBag<Card>> cards = ownState.possibleClaimCards(route);

                routeToClaim = route;
                initialClaimCards = cards.get(0);
                return TurnKind.CLAIM_ROUTE;
            }else {
                return TurnKind.DRAW_TICKETS;
            }
        }

        @Override
        public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
            List<Ticket> inT = new ArrayList<>();
            int numb = 0;
            for (Ticket option : options) {
                if (rng.nextInt(2) == 1 || numb > 0)
                    inT.add(option);
                else
                    numb++;
            }
            return SortedBag.of(inT);
        }

        @Override
        public int drawSlot() {
            if(rng.nextInt(10) == 1)
                return -1;
            return rng.nextInt(5);
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
            if (options.isEmpty())
                return SortedBag.of();
            if (rng.nextInt(100) == 3)
                return SortedBag.of();
            return options.get(rng.nextInt(options.size()));
        }

        /**
         * This method is used to notify the client that the routes in parameter are in the longest Trail.
         *
         * @param routes (List< Route >) : the routes in the longest trail.
         */
        @Override
        public void notifyLongest(List<Route> routes) {

        }

        private void debug() {
            System.out.print("tickets : " + ownState.tickets().size());
            System.out.print(" ; cards : " + ownState.cards().size());
            System.out.print(" ; routes : " + ownState.routes().size());
            System.out.println(" ; cars : " + ownState.carCount());


        }

        /**
         * This method is used to send a message to the client bound to the player.
         *
         * @param serializedMessage (String) : the serialized message sent from the manager that we want to send tot the client
         */
        @Override
        public void sendToClient(String serializedMessage) {

        }

        /**
         * This method is used to verify if a message has been written in the socket of the client and write it in the socket of the manager
         */
        @Override
        public void sendToManager() {

        }

        /**
         * This method method is used by the client to send a message to the proxy that can be transmitted to the manager after
         *
         * @param message (String) : the message we want to send to the proxy
         */
        @Override
        public void sendToProxy(String message) {

        }

        /**
         * This method is used to receive a message from a the socket of messages.
         *
         * @param message jhjjh
         */
        @Override
        public void receiveMessage(String message) {

        }
    }
}

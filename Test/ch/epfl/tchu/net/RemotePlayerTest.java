package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.gui.MapViewCreator;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;
import ch.epfl.tchu.net.RemotePlayerClient;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

public class RemotePlayerTest {

    public static void main(String[] args)  {
        System.out.println("Starting client!");
        RemotePlayerClient playerClient =
                new RemotePlayerClient(new TestPlayer(510,new ArrayList<>()),
                        "localhost",
                        5108);
        playerClient.run();
        System.out.println("Client done!");
    }







    private static final class TestPlayer implements Player {
        private static final int TURN_LIMIT = 1000;

        private final Random rng;
        // Toutes les routeOwner de la carte
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
            this.allRoutes = List.copyOf(allRoutes);
            this.turnCounter = 0;
        }

        @Override
        public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
            this.ownId = ownId;
            this.playerNames = playerNames;
            System.out.printf("ownId: %s\n", ownId);
            System.out.printf("playerNames: %s\n", playerNames);
        }

        @Override
        public void receiveInfo(String info) {
            infos++;
            System.out.println("Message pour "+ playerNames.get(ownId) + " : " + info + ":::::info done");
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
            System.out.println(Serdes.PUBLIC_GAME_STATE_SERDE.serialize(newState) + " " + Serdes.PLAYER_STATE_SERDE.serialize(ownState));
        }

        @Override
        public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
            System.out.println(Serdes.TICKET_BAG_SERDE.serialize(tickets));
            inTickets = tickets;
        }

        @Override
        public SortedBag<Ticket> chooseInitialTickets() {
            int index = TestRandomizer.newRandom().nextInt(3 +1) + 1;
            SortedBag<Ticket> tickets =  SortedBag.of(inTickets.toList().subList(0,index));
            System.out.println(Serdes.TICKET_BAG_SERDE.serialize(tickets));
            return tickets;
        }

        @Override
        public TurnKind nextTurn() {
            TurnKind turnKind = Serdes.TURN_KIND_SERDE.deserialize(String.valueOf(TestRandomizer.newRandom().nextInt(3)));
            System.out.println(turnKind);
            return turnKind;
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
            int result = rng.nextInt(5);
            System.out.println(result);
            return result;
        }

        @Override
        public Route claimedRoute() {
            Route route = ChMap.routes().get((new Random()).nextInt(ChMap.routes().size()));
            System.out.println(String.join(" ", List.of(route.station1().toString(), "-",route.station2().toString(), "(" + route.length() + ")")));
            routeToClaim = route;
            return route;
        }

        @Override
        public SortedBag<Card> initialClaimCards() {
            List<SortedBag<Card>> cards = routeToClaim.possibleClaimCards();
            SortedBag<Card> bagOfCard = cards.get(TestRandomizer.newRandom().nextInt(cards.size()));
            System.out.println(bagOfCard);
            return bagOfCard;
        }

        @Override
        public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
            if (options.isEmpty())
                return SortedBag.of();
            if (rng.nextInt(100) == 3)
                return SortedBag.of();
            return options.get(rng.nextInt(options.size()));
        }


    }


}

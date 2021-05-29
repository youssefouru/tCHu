package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Game : this class have only one method that is used to run the game
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public final class Game {

    private final static int NUMBER_OF_CARDS_DREW = 2;

    private Game() {
    }


    /**
     * this method represents the game
     *
     * @param players     (Map<PlayerId, Player>) : map that associate each playerId to the player he refers to
     * @param playerNames (Map<PlayerId, String>) : map that associate each playerId to his name
     * @param tickets     (SortedBag<Ticket>) : the initial tickets of the game
     * @param rng         (Random) : the random object which is used several times in this method
     */
    public static void play(Map<PlayerId,? extends Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng) {
        Preconditions.checkArgument(players.size() == PlayerId.COUNT && playerNames.size() == PlayerId.COUNT);
        GameState gameState = GameState.initial(tickets, rng);
        players.forEach(((playerId, player) -> player.initPlayers(playerId, playerNames)));
        Map<PlayerId, Info> playersInfos = new EnumMap<>(PlayerId.class);
        PlayerId.ALL.forEach((playerId -> playersInfos.put(playerId, new Info(playerNames.get(playerId)))));
        transmitInfo(players, playersInfos.
                get(gameState.
                        currentPlayerId()).
                willPlayFirst());

        Map<PlayerId, String> infosOfTickets = new HashMap<>();
        for (PlayerId playerId : PlayerId.ALL) {
            Player player = players.get(playerId);
            player.setInitialTicketChoice(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));
            gameState = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
        }

        updateStates(players, gameState);
        for (PlayerId playerId : PlayerId.ALL) {
            Player player = players.get(playerId);
            SortedBag<Ticket> chosenTickets = player.chooseInitialTickets();
            gameState = gameState.withInitiallyChosenTickets(playerId, chosenTickets);
            infosOfTickets.put(playerId, playersInfos.get(playerId).keptTickets(chosenTickets.size()));
        }

        for (PlayerId playerId : PlayerId.ALL) {
            transmitInfo(players, infosOfTickets.get(playerId));
        }

        while (true) {
            Player currentPlayer = players.get(gameState.currentPlayerId());
            updateStates(players, gameState);
            Info currentPlayerInfo = playersInfos.get(gameState.currentPlayerId());
            transmitInfo(players, currentPlayerInfo.canPlay());
            switch (currentPlayer.nextTurn()) {
                case DRAW_TICKETS:
                    int ticketsDrawn = Math.min(Constants.IN_GAME_TICKETS_COUNT,gameState.ticketsCount());
                    SortedBag<Ticket> drawnTickets = gameState.topTickets(ticketsDrawn);
                    transmitInfo(players, currentPlayerInfo.drewTickets(ticketsDrawn));
                    SortedBag<Ticket> chosenTickets = currentPlayer.chooseTickets(drawnTickets);
                    gameState = gameState.withChosenAdditionalTickets(drawnTickets, chosenTickets);
                    transmitInfo(players, currentPlayerInfo.keptTickets(chosenTickets.size()));

                    break;
                case DRAW_CARDS:
                    for (int i = 0; i < NUMBER_OF_CARDS_DREW; ++i) {
                        if (i == 1) {
                            updateStates(players, gameState);
                        }
                        gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                        int slot = currentPlayer.drawSlot();

                        if (slot == Constants.DECK_SLOT) {
                            gameState = gameState.withBlindlyDrawnCard();
                            transmitInfo(players, currentPlayerInfo.drewBlindCard());
                        } else {
                            Card drewCard = gameState.cardState().faceUpCard(slot);
                            gameState = gameState.withDrawnFaceUpCard(slot);
                            transmitInfo(players, currentPlayerInfo.drewVisibleCard(drewCard));
                        }
                    }
                    break;
                case CLAIM_ROUTE:
                    Route claimedRoute = currentPlayer.claimedRoute();
                    SortedBag<Card> playedCard = currentPlayer.initialClaimCards();
                    if (claimedRoute.level() == Route.Level.UNDERGROUND) {
                        transmitInfo(players, currentPlayerInfo.attemptsTunnelClaim(claimedRoute, playedCard));
                        SortedBag.Builder<Card> drawnCardsBuilder = new SortedBag.Builder<>();
                        for (int i = 0; i < Constants.ADDITIONAL_TUNNEL_CARDS; ++i) {
                            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                            drawnCardsBuilder.add(gameState.topCard());
                            gameState = gameState.withoutTopCard();
                        }
                        SortedBag<Card> drawnCards = drawnCardsBuilder.build();
                        int additionalCards = claimedRoute.additionalClaimCardsCount(playedCard, drawnCards);
                        transmitInfo(players, currentPlayerInfo.drewAdditionalCards(drawnCards, additionalCards));
                        gameState = gameState.withMoreDiscardedCards(drawnCards);
                        if (additionalCards != 0) {
                            List<SortedBag<Card>> optionsOfSortedBag = gameState.currentPlayerState().possibleAdditionalCards(additionalCards, playedCard);
                            if (optionsOfSortedBag.isEmpty()) {
                                playedCard = SortedBag.of();
                            } else {
                                SortedBag<Card> additionalCardsPlayed = currentPlayer.chooseAdditionalCards(optionsOfSortedBag);
                                playedCard = additionalCardsPlayed.isEmpty() ? SortedBag.of() : playedCard.union(additionalCardsPlayed);
                            }
                        }
                    }

                    if (playedCard.isEmpty()) {
                        transmitInfo(players, currentPlayerInfo.didNotClaimRoute(claimedRoute));
                        break;
                    } else {
                        gameState = gameState.withClaimedRoute(claimedRoute, playedCard);
                        transmitInfo(players, currentPlayerInfo.claimedRoute(claimedRoute, playedCard));
                    }
                    break;
            }

            if (gameState.currentPlayerId() == gameState.lastPlayer()) {
                break;
            }

            if (gameState.lastTurnBegins()) {
                int carCount = gameState.currentPlayerState().carCount();
                transmitInfo(players, currentPlayerInfo.lastTurnBegins(carCount));

            }

            gameState = gameState.forNextTurn();

        }


        updateStates(players, gameState);


        Map<PlayerId, Trail> mapOfTrails = new HashMap<>();
        for (PlayerId playerId : PlayerId.ALL) {
            PlayerState playerState = gameState.playerState(playerId);
            mapOfTrails.put(playerId, Trail.longest(playerState.routes()));
        }

        Set<PlayerId> playerTheLongestTrails = getsBonus(mapOfTrails);
        Map<PlayerId, Integer> mapPoints = new EnumMap<>(PlayerId.class);
        for (PlayerId playerId : PlayerId.ALL) {
            //we verify if the player is among the player who has the longest trail and if he is among them he can have the bonus
            int bonus = playerTheLongestTrails.contains(playerId) ? Constants.LONGEST_TRAIL_BONUS_POINTS : 0;
            mapPoints.put(playerId, gameState.playerState(playerId).finalPoints() + bonus);
            if (bonus == Constants.LONGEST_TRAIL_BONUS_POINTS) {
                Trail maximalTrail =mapOfTrails.get(playerId);
                notifyLongest(players,maximalTrail);
                transmitInfo(players, playersInfos.get(playerId).getsLongestTrailBonus(maximalTrail));
            }
        }

        List<PlayerId> listOfPlayer = maxPoints(mapPoints);
        PlayerId winner = listOfPlayer.get(0);
        if (listOfPlayer.size() == 1) {
            int winnerPoint = mapPoints.get(winner);
            int looserPoint = mapPoints.get(winner.next());
            transmitInfo(players, playersInfos.get(listOfPlayer.get(0)).won(winnerPoint, looserPoint));
        } else {
            int points = mapPoints.get(winner);
            List<String> names = listOfPlayer.stream().
                    map(playerNames::get).
                    collect(Collectors.toList());
            transmitInfo(players, Info.draw(names, points));
        }


    }


    private static void transmitInfo(Map<PlayerId,? extends Player> map, String info) {
        map.forEach(((playerId, player) -> player.receiveInfo(info)));
    }

    private static void updateStates(Map<PlayerId,? extends Player> map, GameState gameState) {
        map.forEach(((playerId, player) -> player.updateState(gameState, gameState.playerState(playerId))));

    }

    private static void notifyLongest(Map<PlayerId, ? extends Player> map, Trail trail){
            map.forEach(((playerId, player) -> player.notifyLongest(trail.routes())));

    }

    private static Set<PlayerId> getsBonus(Map<PlayerId, Trail> longestTrailList) {
        int maxLength = longestTrailList.get(PlayerId.PLAYER_1).length();
        for (PlayerId playerId : PlayerId.ALL) {
            if (longestTrailList.get(playerId).length() > maxLength) {
                maxLength = longestTrailList.get(playerId).length();
            }
        }
        Set<PlayerId> playerIdList = new TreeSet<>(Enum::compareTo);
        for (PlayerId playerId : PlayerId.ALL) {
            if (longestTrailList.get(playerId).length() == maxLength) {
                playerIdList.add(playerId);
            }
        }
        return playerIdList;

    }

    private static List<PlayerId> maxPoints(Map<PlayerId, Integer> points) {
        List<PlayerId> winner = new ArrayList<>();

        int max = points.get(PlayerId.PLAYER_1);
        for (PlayerId playerId : PlayerId.ALL) {
            if (points.get(playerId) > max) {
                max = points.get(playerId);
            }
        }
        for (PlayerId playerId : PlayerId.ALL) {
            if (points.get(playerId) == max) {
                winner.add(playerId);
            }
        }
        return winner;
    }

}

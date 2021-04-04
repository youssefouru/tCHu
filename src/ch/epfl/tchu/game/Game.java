package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.*;

/**
 * Game
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public final class Game {

    private final static int NUMBER_OF_CARDS_DREW = 2;

    private Game() {
    }


    /**
     * this method is the methode that run the game
     *
     * @param players     (Map<PlayerId, Player>) : map that associate each playerId to the player he refers to
     * @param playerNames (Map<PlayerId, String>) : map that associate each playerId to his name
     * @param tickets     (SortedBag<Ticket>) : the initial tickets of the game
     * @param rng         (Random) : the random object which is used several times in this method
     */
    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng) {
        Preconditions.checkArgument(players.size() == PlayerId.COUNT && playerNames.size() == PlayerId.COUNT);
        GameState gameState = GameState.initial(tickets, rng);

        players.forEach(((playerId, player) -> player.initPlayers(playerId, playerNames)));
        Map<PlayerId, Info> playersInfos = new EnumMap<>(PlayerId.class);
        for(PlayerId playerId : PlayerId.ALL){
            playersInfos.put(playerId,new Info(playerNames.get(playerId)));
        }
        transmitInfo(players, playersInfos.
                            get(gameState.
                            currentPlayerId()).
                            willPlayFirst());
        
        for (PlayerId playerId : PlayerId.ALL) {
            Player player = players.get(playerId);
            player.setInitialTicketChoice(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));
            gameState = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
        }
        updateState(players,gameState);
        for(PlayerId playerId : PlayerId.ALL){
            Player player = players.get(playerId);
            gameState = gameState.withInitiallyChosenTickets(playerId, player.chooseInitialTickets());
            transmitInfo(players, playersInfos.
                    get(playerId).
                    keptTickets(player.chooseInitialTickets().size()));

        }
        while (true) {
            Player currentPlayer = players.get(gameState.currentPlayerId());
            Info currentPlayerInfo = playersInfos.get(gameState.currentPlayerId());
            transmitInfo(players, currentPlayerInfo.canPlay());
            updateState(players,gameState);
            switch (currentPlayer.nextTurn()) {
                case DRAW_TICKETS:
                    if(!gameState.canDrawTickets()){
                        break;
                    }
                    SortedBag<Ticket> drawnTickets = gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT);
                    transmitInfo(players, currentPlayerInfo.drewTickets(Constants.IN_GAME_TICKETS_COUNT));
                    SortedBag<Ticket> chosenTickets = currentPlayer.chooseTickets(drawnTickets);
                    gameState = gameState.withChosenAdditionalTickets(drawnTickets, chosenTickets);
                    transmitInfo(players, currentPlayerInfo.keptTickets(chosenTickets.size()));

                    break;
                case DRAW_CARDS:
                    if(gameState.cardState().deckSize() + gameState.cardState().discardsSize() <6){
                        break;
                    }
                    for (int i = 0; i < NUMBER_OF_CARDS_DREW; ++i) {
                        gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                        int slot = currentPlayer.drawSlot();
                        if(i == 1){
                            updateState(players,gameState);
                        }
                        if (slot == Constants.DECK_SLOT) {
                            gameState = gameState.withBlindlyDrawnCard();
                            transmitInfo(players, currentPlayerInfo.drewBlindCard());
                        } else {
                            gameState = gameState.withDrawnFaceUpCard(slot);
                            Card drewCard = gameState.cardState().faceUpCard(slot);
                            transmitInfo(players, currentPlayerInfo.drewVisibleCard(drewCard));
                        }
                    }
                    break;
                case CLAIM_ROUTES:
                    Route claimedRoute = currentPlayer.claimedRoute();
                    SortedBag<Card> playedCard = currentPlayer.initialClaimCards();
                    if (claimedRoute.level() == Route.Level.UNDERGROUND) {
                        transmitInfo(players, currentPlayerInfo.attemptsTunnelClaim(claimedRoute, playedCard));
                        SortedBag.Builder<Card> cards = new SortedBag.Builder<>();
                        for (int i = 0; i < Constants.ADDITIONAL_TUNNEL_CARDS; ++i) {
                            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                            cards.add(gameState.topCard());
                            gameState = gameState.withoutTopCard();
                        }
                        SortedBag<Card> drawnCards = cards.build();
                        int additionalCards = claimedRoute.additionalClaimCardsCount(playedCard, drawnCards);
                        transmitInfo(players, currentPlayerInfo.drewAdditionalCards(drawnCards, additionalCards));
                        if(additionalCards == 0) {
                            playedCard = SortedBag.of();

                        }else {
                            gameState = gameState.withMoreDiscardedCards(drawnCards);
                            List<SortedBag<Card>> optionsOfSortedBag = gameState.currentPlayerState().possibleAdditionalCards(additionalCards, playedCard, drawnCards);
                            if (optionsOfSortedBag.isEmpty()) {
                                playedCard = SortedBag.of();
                            }else {
                                SortedBag<Card> additionalCardsPlayed = currentPlayer.chooseAdditionalCards(optionsOfSortedBag);
                                playedCard = additionalCardsPlayed.isEmpty() ? SortedBag.of() : playedCard.union(additionalCardsPlayed);
                            }
                        }
                    }
                    if(playedCard.isEmpty()){
                        transmitInfo(players,currentPlayerInfo.didNotClaimRoute(claimedRoute));
                        break;
                    }else {
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
        updateState(players,gameState);

        Map<PlayerId, Integer> mapPoints = new EnumMap<>(PlayerId.class);

        for (PlayerId playerId : PlayerId.ALL) {
            mapPoints.put(playerId, gameState.playerState(playerId).finalPoints());
        }

        List<PlayerId> playerTheLongestTrails = getsBonus(gameState);

        for(PlayerId playerId : playerTheLongestTrails) {
            mapPoints.put(playerId, mapPoints.get(playerId) + Constants.LONGEST_TRAIL_BONUS_POINTS);
            PlayerState playerStateLongTrail = gameState.playerState(playerId);
            transmitInfo(players, playersInfos.get(playerId).getsLongestTrailBonus(Trail.longest(playerStateLongTrail.routes())));
        }

        List<PlayerId> listOfPlayer = maxPoints(mapPoints);
        if (listOfPlayer.size() == 1) {
            PlayerId winner = listOfPlayer.get(0);
            int winnerPoint = mapPoints.get(winner);
            int looserPoint = mapPoints.get(winner.next());
            transmitInfo(players, playersInfos.get(listOfPlayer.get(0)).won(winnerPoint, looserPoint));
        } else {
            int points = mapPoints.get(listOfPlayer.get(0));
            List<String> names = new ArrayList<>();
            for (PlayerId playerId : listOfPlayer) {
                names.add(playerNames.get(playerId));
            }
            transmitInfo(players, Info.draw(names, points));
        }


    }


    private static void transmitInfo(Map<PlayerId, Player> map, String info) {
        map.forEach(((playerId, player) -> player.receiveInfo(info)));
    }

    private static void updateState(Map<PlayerId, Player> map, GameState gameState) {
        for (Map.Entry<PlayerId, Player> entry : map.entrySet()) {
            entry.getValue().updateState(gameState, gameState.playerState(entry.getKey()));
        }
    }

    private static List<PlayerId> getsBonus(GameState gameState) {
        Map<PlayerId, Trail> longestTrailList = new HashMap<>();
        //first we creat a map
        for (PlayerId playerId : PlayerId.ALL) {
            PlayerState playerState = gameState.playerState(playerId);
            longestTrailList.put(playerId, Trail.longest(playerState.routes()));
        }

        int maxLength = longestTrailList.get(PlayerId.PLAYER_1).length();

        for (PlayerId playerId : PlayerId.ALL) {
            if (longestTrailList.get(playerId).length() > maxLength) {
                maxLength = longestTrailList.get(playerId).length();
            }
        }
        List<PlayerId> playerIdList = new LinkedList<>();
        for(PlayerId playerId : PlayerId.ALL){
            if(longestTrailList.get(playerId).length() == maxLength){
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
            if (points.get(playerId) == max && !winner.contains(playerId)) {
                winner.add(playerId);
            }
        }
        return winner;
    }

}

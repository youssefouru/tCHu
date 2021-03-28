package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;
import org.w3c.dom.ls.LSInput;

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
     * Method that update the Game
     *
     * @param players     (Map<PlayerId, Player>) : map that
     * @param playerNames (Map<PlayerId, String>) :
     * @param tickets     (SortedBag<Ticket>) :
     * @param rng         (Random) :
     */
    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng) {
        Preconditions.checkArgument(players.size() == PlayerId.COUNT && playerNames.size() == PlayerId.COUNT);
        GameState gameState = GameState.initial(tickets, rng);
        updateState(players,gameState);
        players.forEach(((playerId, player) -> player.initPlayers(playerId, playerNames)));
        Map<PlayerId, Info> playersInfos = new EnumMap<PlayerId, Info>(PlayerId.class);
        playersInfos.forEach((playerId, info) -> {
            info = new Info(playerNames.get(playerId));
        });
        transmitInfo(players, playersInfos.
                    get(gameState.
                    currentPlayerId()).
                    willPlayFirst());
        List<Integer> numberOfTicket = new ArrayList<>();
        for (Map.Entry<PlayerId, Player> playerEntry : players.entrySet()) {
            Player player = playerEntry.getValue();
            PlayerId playerId = playerEntry.getKey();
            player.setInitialTicketChoice(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));
            gameState = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
            updateState(players,gameState);
            gameState = gameState.withInitiallyChosenTickets(playerId, player.chooseInitialTickets());
            updateState(players,gameState);
            transmitInfo(players, playersInfos.
                    get(playerId).
                    keptTickets(player.
                    chooseInitialTickets().
                    size()));
        }

        while (gameState.currentPlayerId() != gameState.lastPlayer()) {
            Player currentPlayer = players.get(gameState.currentPlayerId());
            Info currentPlayerInfo = playersInfos.get(gameState.currentPlayerId());
            transmitInfo(players,currentPlayerInfo.canPlay());
            switch (currentPlayer.nextTurn()) {
                case DRAW_TICKETS:
                    if (gameState.canDrawTickets()) {
                        SortedBag<Ticket> drawnTickets = gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT);
                        transmitInfo(players,currentPlayerInfo.drewTickets(Constants.IN_GAME_TICKETS_COUNT));
                        gameState = gameState.withoutTopTickets(Constants.IN_GAME_TICKETS_COUNT);
                        updateState(players,gameState);
                        SortedBag<Ticket> chosenTickets =currentPlayer.chooseTickets(drawnTickets);
                        gameState = gameState.withChosenAdditionalTickets(drawnTickets, chosenTickets);
                        updateState(players,gameState);
                        transmitInfo(players,currentPlayerInfo.keptTickets(chosenTickets.size()));
                    }

                    break;
                case DRAW_CARDS:
                    for (int i = 0; i < NUMBER_OF_CARDS_DREW; ++i) {
                        GameState save = gameState;
                        gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                        if(gameState != save){
                            updateState(players,gameState);
                        }
                        int slot = currentPlayer.drawSlot();
                        if(slot == Constants.DECK_SLOT){
                            gameState = gameState.withBlindlyDrawnCard();
                            transmitInfo(players,currentPlayerInfo.drewBlindCard());
                        }else {
                            gameState = gameState.withDrawnFaceUpCard(slot);
                            Card drewCard = gameState.cardState().faceUpCard(slot);
                            transmitInfo(players,currentPlayerInfo.drewVisibleCard(drewCard));
                        }
                        updateState(players,gameState);
                    }
                    break;
                case CLAIM_ROUTES:
                    Route claimedRoute = currentPlayer.claimedRoute();
                    SortedBag<Card> initialClaimCards = currentPlayer.initialClaimCards();
                    if (claimedRoute.level() == Route.Level.UNDERGROUND) {
                        SortedBag.Builder<Card> cards = new SortedBag.Builder<>();
                        for (int i = 0; i < Constants.ADDITIONAL_TUNNEL_CARDS; ++i) {
                            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                            updateState(players,gameState);
                            cards.add(gameState.topCard());
                            gameState = gameState.withoutTopCard();
                            updateState(players,gameState);
                        }
                        SortedBag<Card> drawnCards = cards.build();
                        int additionalCards = claimedRoute.additionalClaimCardsCount(initialClaimCards, drawnCards);
                        List<SortedBag<Card>> optionsOfSortedBag = gameState.currentPlayerState().possibleAdditionalCards(additionalCards, initialClaimCards, drawnCards);
                        SortedBag<Card> additionalCardsPlayed = currentPlayer.chooseAdditionalCards(optionsOfSortedBag);

                    }
                    break;
            }
            if(gameState.currentPlayerId() == gameState.lastPlayer()){
                break;
            }
            if (gameState.lastTurnBegins()){
                int carCount = gameState.currentPlayerState().carCount();
                transmitInfo(players,currentPlayerInfo.lastTurnBegins(carCount));
            }
            gameState = gameState.forNextTurn();
            updateState(players,gameState);
        }

        Map<PlayerId,Integer> mapPoints = new EnumMap<>(PlayerId.class);
        for(PlayerId playerId : PlayerId.ALL) {
            mapPoints.put(playerId, gameState.playerState(playerId).finalPoints());
        }

        mapPoints.put(getsBonus(gameState),mapPoints.get(getsBonus(gameState)) + Constants.LONGEST_TRAIL_BONUS_POINTS);




    }


    private static void transmitInfo(Map<PlayerId, Player> map, String info) {
        map.forEach(((playerId, player) -> player.receiveInfo(info)));
    }

    private static void updateState(Map<PlayerId, Player> map,GameState gameState){
        for(Map.Entry<PlayerId,Player> entry : map.entrySet()){
            entry.getValue().updateState(gameState,gameState.playerState(entry.getKey()));
        }
    }

    private static PlayerId getsBonus(GameState gameState){
        Map<PlayerId,Trail> longestTrailList = new HashMap<>();
        for(PlayerId playerId : PlayerId.ALL){
            PlayerState playerState = gameState.playerState(playerId);
            longestTrailList.put(playerId,Trail.longest(playerState.routes()));
        }
        int saveLenght =longestTrailList.get(PlayerId.PLAYER_1).length();
        PlayerId savePlayer =PlayerId.PLAYER_1;
        for(PlayerId playerId : PlayerId.ALL){
            if(longestTrailList.get(playerId).length() > saveLenght){
                saveLenght = longestTrailList.get(playerId).length();
                savePlayer = playerId;
            }
        }

        return savePlayer;

    }

    private List<PlayerId> maxPoints(Map<PlayerId,Integer> points){
        List<PlayerId> winner = new ArrayList<>();
        int max = points.get(PlayerId.PLAYER_1);
        PlayerId saveId = PlayerId.PLAYER_1;
        for(PlayerId playerId : PlayerId.ALL){
            if(points.get(playerId)>max){
               saveId = playerId;
               max = points.get(playerId);
            }
        }
        winner.add(saveId);
        for(PlayerId playerId : PlayerId.ALL){
            if(points.get(playerId) == max && !winner.contains(playerId)){
                winner.add(playerId);
            }
        }
        return winner;
    }


}

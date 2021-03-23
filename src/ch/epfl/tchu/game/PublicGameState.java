package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.*;


public class PublicGameState {
    int ticketCount;
    final PublicCardState cardState;
    final PlayerId currentPlayerId;
    final Map<PlayerId, PublicPlayerState> playerState;
    final PlayerId lastPlayer;

    public PublicGameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId, Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer) {
        Preconditions.checkArgument(cardState.discardsSize() >= 0 || playerState.size() == 2);
        if (ticketsCount == 0 || cardState == null || currentPlayerId == null || playerState == null) {
            throw new NullPointerException();
        }

        this.ticketCount = ticketsCount;
        this.cardState = cardState;
        this.currentPlayerId = currentPlayerId;
        this.playerState = Map.copyOf(playerState);
        this.lastPlayer = lastPlayer;
    }

    public int ticketsCount() {
        return ticketCount;
    }

    public boolean canDrawTickets() {
        return cardState.deckSize() > 0;
    }

    public PublicCardState cardState() {
        return cardState;
    }

    public boolean canDrawCards() {
        return cardState.deckSize() >= 5 && cardState.discardsSize() >= 5;
    }

    public PlayerId currentPlayerId() {
        return currentPlayerId;
    }

    public PublicPlayerState playerState(PlayerId playerId) {
        return playerState.get(playerId);
    }

    public PublicPlayerState currentPlayerState() {
        return playerState.get(currentPlayerId);
    }

    public List<Route> claimedRoutes() {
        List<Route> list = new LinkedList<>();
        Collection<PublicPlayerState> playerStates =  playerState.values();
        for (PublicPlayerState playerState : playerStates) {
            list.addAll(playerState.routes());
        }

        return list;
    }

    public PlayerId lastPlayer() {
        return lastPlayer;
    }

}
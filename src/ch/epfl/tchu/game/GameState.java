package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


/**
 * GameState : this class represents the private part of a GameState
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public final class GameState extends PublicGameState {
    private final static int LAST_TURN_CARS = 2;
    private final Deck<Ticket> tickets;
    private final CardState cardState;
    private final Map<PlayerId, PlayerState> playerState;

    /**
     * Constructor of GameState
     *
     * @param tickets         (Deck<Ticket>) : Sorted bag of the tickets
     * @param cardState       (CardState) : the current CardState
     * @param currentPlayerId (PlayerId) : the Id of the current player Id
     * @param playerState     ( Map<PlayerId, PlayerState>): the playerState which will give us the player state of each player based on id of the player
     * @param lastPlayer      (PlayerId) : the Id of the last Player
     */
    private GameState(Deck<Ticket> tickets, CardState cardState, PlayerId currentPlayerId, Map<PlayerId, PlayerState> playerState, PlayerId lastPlayer) {
        super(tickets.size(), cardState, currentPlayerId, transform(playerState), lastPlayer);
        this.playerState = Map.copyOf(playerState);
        this.tickets = tickets;
        this.cardState = cardState;

    }


    private static Map<PlayerId, PublicPlayerState> transform(Map<PlayerId, PlayerState> playerState) {
        Map<PlayerId, PublicPlayerState> map = new HashMap<>();
        playerState.forEach(map::put);
        return map;
    }

    /**
     * returns the initial GameState
     *
     * @param tickets (SortedBag<Ticket>) : initial tickets of the game
     * @param rng     (Random) :  the randomizer that we’re going to use to shuffle cards
     * @return (GameState) : return the initial the GameState
     */
    public static GameState initial(SortedBag<Ticket> tickets, Random rng) {
        Deck<Card> cardDeck = Deck.of(Constants.ALL_CARDS, rng);
        Map<PlayerId, PlayerState> map = new EnumMap<>(PlayerId.class);
        for (PlayerId playerId : PlayerId.ALL) {
            SortedBag<Card> cards = cardDeck.topCards(Constants.INITIAL_CARDS_COUNT);
            PlayerState playerState = PlayerState.initial(cards);
            map.put(playerId, playerState);
            cardDeck = cardDeck.withoutTopCards(Constants.INITIAL_CARDS_COUNT);
        }

        PlayerId firstPlayer = PlayerId.ALL.get(rng.nextInt(PlayerId.COUNT));
        return new GameState(Deck.of(tickets, rng), CardState.of(cardDeck), firstPlayer, map, null);
    }


    /**
     * this method returns the playerState of the player with Id in parameter
     *
     * @param playerId (PlayerId) : the id of the player that we want to know the public state
     * @return (PlayerState) : the  player state of the player with the id in parameter
     */
    @Override
    public PlayerState playerState(PlayerId playerId) {
        return playerState.get(playerId);
    }

    /**
     * returns the public part of the playerState of the current player
     *
     * @return (PublicPlayerState) : the public player state of the current player
     */
    @Override
    public PlayerState currentPlayerState() {
        return playerState(currentPlayerId());
    }

    /**
     * this method returns the first count tickets
     *
     * @param count (int) : number of tickets
     * @return (SortedBag < Ticket >) : a bag of the first count tickets
     */
    public SortedBag<Ticket> topTickets(int count) {
        Preconditions.checkArgument(count >= 0 && count <= ticketsCount());
        return tickets.topCards(count);
    }

    /**
     * this method returns a GameState but without the count first tickets
     *
     * @param count (int) : number of tickets we want to remove
     * @return (GameState) :this GameState but without the count first tickets
     */
    public GameState withoutTopTickets(int count) {
        Preconditions.checkArgument(count >= 0 && count <= ticketsCount());
        return new GameState(tickets.withoutTopCards(count), cardState, currentPlayerId(), playerState, lastPlayer());
    }

    /**
     * this method returns the top card of the deck of the cardState
     *
     * @return (Card) : the TopDeckCard of the attribute cardState
     */
    public Card topCard() {
        Preconditions.checkArgument(!cardState.isDeckEmpty());
        return cardState.topDeckCard();
    }

    /**
     * this method returns a new Game State but with a cardState without the top Deck card
     *
     * @return (GameState) : this Game State but with this cardState without the top Deck card
     */
    public GameState withoutTopCard() {
        Preconditions.checkArgument(!cardState.isDeckEmpty());
        return new GameState(tickets, cardState.withoutTopDeckCard(), currentPlayerId(), playerState, lastPlayer());
    }

    /**
     * this method returns a new GameState but with more discardCards
     *
     * @param discardedCards (SortedBag<Card>) : the discard cards that we want to add to the new gameState
     * @return (GameState) : this GameState but with more discardCards
     */
    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards) {
        return new GameState(tickets, cardState.withMoreDiscardedCards(discardedCards), currentPlayerId(), playerState, lastPlayer());
    }

    /**
     * this method returns a new GameState but with a deck recreated with discards cards if the deck is empty or return the same game state
     *
     * @param rng (Random) : the randomizer that we’re going to use to shuffle cards
     * @return (GameState) :  this GameState  with a deck recreated from discards or this game state of the deck is non empty
     */
    public GameState withCardsDeckRecreatedIfNeeded(Random rng) {
        if (!cardState.isDeckEmpty()) {
            return this;
        }
        return new GameState(tickets, cardState.withDeckRecreatedFromDiscards(rng), currentPlayerId(), playerState, lastPlayer());
    }

    /**
     * this method returns a new GameState with the same attribute of this but the player with the Id in parameter to whom we added the tickets in parameter
     *
     * @param playerId      (PlayerId) : the id of the player we want to add the additional tickets
     * @param chosenTickets (SortedBag<Ticket>) : the tickets we want to add
     * @return (GameState) : new GameState with the same attribute of this but the player with the Id in parameter to whom we added the tickets in parameter
     */
    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(playerState(playerId).ticketCount() == 0);
        Map<PlayerId, PlayerState> map = (new HashMap<>(this.playerState));
        map.put(playerId,playerState(playerId).withAddedTickets(chosenTickets));
        return new GameState(tickets, cardState, currentPlayerId(), map, lastPlayer());
    }

    /**
     * returns a new GameState but the current player has taken the chosenTickets form the drawnTickets
     *
     * @param drawnTickets  (SortedBag<Ticket>) : the tickets drawn by the player
     * @param chosenTickets (SortedBag<Ticket>) : the tickets chosen by the player
     * @return (GameState) : new GameState but we add to the state of the current player the chosenTickets form the drawnTickets
     */
    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));
        Map<PlayerId, PlayerState> map = new HashMap<>(this.playerState);
        map.put(currentPlayerId(), currentPlayerState().withAddedTickets(chosenTickets));
        return new GameState(tickets.withoutTopCards(drawnTickets.size()), cardState, currentPlayerId(), map, lastPlayer());
    }

    /**
     * returns this GameState but without the slot face up card which has been taken by the current player
     *
     * @param slot (int) : the index of the card that we want
     * @return (GameState) : this GameState but without the slot faceUpCard that we add to the current player's state
     */
    public GameState withDrawnFaceUpCard(int slot) {
        Preconditions.checkArgument(canDrawCards());
        Map<PlayerId, PlayerState> map = new HashMap<>(this.playerState);
        map.put(currentPlayerId(), currentPlayerState().withAddedCard(cardState.faceUpCard(slot)));
        return new GameState(tickets, cardState.withDrawnFaceUpCard(slot), currentPlayerId(), map, lastPlayer());
    }


    /**
     * returns a new GameState but the current player has drew blindly a card from the deck
     *
     * @return (GameState) : this  GameState but without the top deck card which has been added to the player's card
     */
    public GameState withBlindlyDrawnCard() {
        Preconditions.checkArgument(canDrawCards());
        Map<PlayerId, PlayerState> map = new HashMap<>(this.playerState);
        map.put(currentPlayerId(), currentPlayerState().withAddedCard(cardState.topDeckCard()));
        return new GameState(tickets, cardState.withoutTopDeckCard(), currentPlayerId(), map, lastPlayer());
    }

    /**
     * this method returns a GameState but where the current player claimed the route in parameter with the cards in parameter
     *
     * @param route (Route) : route claimed by the player
     * @param cards (SortedBag<Card>) : the cards that the current player used to claim the route in parameter
     * @return (GameState) : a GameState where the current player's state claimed the route with the cards in parameter
     */
    public GameState withClaimedRoute(Route route, SortedBag<Card> cards) {
        Map<PlayerId, PlayerState> map = new HashMap<>(this.playerState);
        map.put(currentPlayerId(), currentPlayerState().withClaimedRoute(route, cards));
        return new GameState(tickets, cardState.withMoreDiscardedCards(cards), currentPlayerId(), map, lastPlayer());
    }

    /**
     * This method tells  if the last turn begins
     *
     * @return (boolean) : true if the last turn begin
     */
    public boolean lastTurnBegins() {
        return lastPlayer() == null && currentPlayerState().carCount() <= LAST_TURN_CARS;
    }

    /**
     * this method returns a GameState where the next player of this GameState is the current player
     *
     * @return (GameState) : new GameState where it's current player is this next player
     */
    public GameState forNextTurn() {
        return new GameState(tickets, cardState, currentPlayerId().next(), playerState, lastTurnBegins() ? currentPlayerId() : lastPlayer());
    }
}
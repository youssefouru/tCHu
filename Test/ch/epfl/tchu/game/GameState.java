package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

public class GameState extends PublicGameState {
    final SortedBag<Ticket> tickets;
    Map<PlayerId, PlayerState> playerStates;
    CardState cardState;

    private GameState(int ticketsCount, CardState cardState, PlayerId currentPlayerId, Map<PlayerId, PlayerState> playerState, PlayerId lastPlayer, SortedBag<Ticket> tickets) {
        super(ticketsCount, cardState, currentPlayerId, Map.copyOf(playerState), lastPlayer);
        this.cardState = cardState;
        this.tickets = tickets;
        this.playerStates = playerState;

    }
   public GameState initial(SortedBag<Ticket> tickets, Random rng) {
       Map<PlayerId, PlayerState> playerState = new EnumMap<PlayerId, PlayerState>(PlayerId.class);
       Deck<Card> deck = Deck.of(Constants.ALL_CARDS, rng); //deck is shuffled here
       PlayerId firstPlayer = firstPlayer(rng);
       PlayerId secondPlayer = firstPlayer.next();

       //We are giving the first 4 cards to the player, and we do not forget to remove them from the top of the list each time
       PlayerState playerStateOfFirstPlayer = PlayerState.initial(deck.topCards(4));
       deck = deck.withoutTopCards(4);
       PlayerState playerStateOfSecondPlayer = PlayerState.initial(deck.topCards(4));
       deck = deck.withoutTopCards(4);

       Deck.of(tickets, rng);

       playerState.put(firstPlayer, playerStateOfFirstPlayer);
       playerState.put(secondPlayer, playerStateOfSecondPlayer);

       return new GameState(tickets.size(), CardState.of(deck), firstPlayer, playerState, null, tickets);

   }

    private static PlayerId firstPlayer(Random rng) {
        if (rng.nextInt(1) == 0) {
            return PlayerId.PLAYER_1;
        } else return PlayerId.PLAYER_2;
    }

   public PlayerState playerState(PlayerId playerId) {
       return playerStates.get(playerId);
   }
   public PlayerState currentPlayerState() {
       return playerState(currentPlayerId);
   }

   public SortedBag<Ticket> topTickets(int count) {
       Preconditions.checkArgument(count >= 0 && count <= tickets.size());
       List<Ticket> myList = new ArrayList<>(tickets.toList().subList(0, count));
       return SortedBag.of(myList);
   }
   public GameState withoutTopTickets(int count) {
       Preconditions.checkArgument(count >= 0 && count <= tickets.size());
       List<Ticket> myList = new ArrayList<>(tickets.toList().subList(count, tickets.size()));
       return new GameState(this.ticketCount, this.cardState, this.currentPlayerId, this.playerStates, this.lastPlayer, SortedBag.of(myList));
   }
   public Card topCard() {
       return cardState.topDeckCard();
   }
   public GameState withoutTopCard() {
       return new GameState(this.ticketCount, cardState.withoutTopDeckCard(), this.currentPlayerId, this.playerStates, this.lastPlayer, this.tickets);

   }
   public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards) {
       return new GameState(this.ticketCount, cardState.withMoreDiscardedCards(discardedCards), this.currentPlayerId, this.playerStates, this.lastPlayer, this.tickets);

   }
   public GameState withCardsDeckRecreatedIfNeeded(Random rng) {
       if (cardState.isDeckEmpty()) {
           Deck<Card> cardDeck = Deck.of(Constants.ALL_CARDS, rng); //deck is shuffled here
           return new GameState(this.ticketCount, CardState.of(cardDeck), this.currentPlayerId, this.playerStates, this.lastPlayer, this.tickets);
       } else {
           return new GameState(this.ticketCount, this.cardState, this.currentPlayerId, this.playerStates, this.lastPlayer, this.tickets);
       }
   }

   public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets) {
       PlayerState playerState = playerStates.get(playerId);
       playerState.withAddedTickets(chosenTickets);
       Map<PlayerId, PlayerState> playerStates = new EnumMap<PlayerId, PlayerState>(this.playerStates); //copy of the immutable list

       playerStates.put(playerId, playerState);
       return new GameState(this.ticketCount, this.cardState, this.currentPlayerId, Map.copyOf(playerStates), this.lastPlayer, this.tickets);

   }
   /*
   public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets)
   public GameState withDrawnFaceUpCard(int slot)
   public GameState withBlindlyDrawnCard()
   public GameState withClaimedRoute(Route route, SortedBag<Card> cards)

   public boolean lastTurnBegins()
   public GameState forNextTurn()

    */
}
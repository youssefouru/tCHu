package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

public final class CardState extends PublicCardState{
    private final Deck<Card> deck;
    private final SortedBag<Card> discardCards;

    /**
     * Constructor of CardState
     *
     * @param deck this is the deck from which we will take cards
     */
    private CardState(Deck<Card> deck) {
        super(deck.topCards(5).toList(),deck.size() - 5,0);
        this.deck = deck;
        this.discardCards = SortedBag.of(List.of());
    }


    private CardState(Deck<Card> deck,SortedBag<Card> discardCards){
        super(deck.topCards(5).toList(),deck.size() -5 , discardCards.size());
        this.deck = deck;
        this.discardCards = discardCards;
    }

    /**
     * this method creates a cardState and returns it
     *
     * @param deck (Deck<Card>) : this is the deck from which we will take cards
     * @return a card state
     */
    public static  CardState of(Deck<Card> deck){
        return new CardState(deck);
    }

    /**
     *this method returns a Card State without the faceUpCard on the slot position
     *
     * @param slot (int) : it's the index of the card that we remove from the faceUpCards
     * @return a new CardState with
     */
    public CardState withDrawnFaceUpCard(int slot){
        Objects.checkIndex(slot,5);
        Preconditions.checkArgument(!deck.isEmpty());
        LinkedList<Card>  myList = new LinkedList<>(super.faceUpCards());
        myList.remove(slot);
        myList.add(slot,deck.topCard());
        return new CardState(Deck.of(SortedBag.of(myList)));

    }


    /**
     * this method returns the top card of the deck
     *
     * @return the deck's top card
     */
    public Card topDeckCard(){
        Preconditions.checkArgument(!deck.isEmpty());
        return deck.topCard();
    }


    /**
     * this method creates a new deck with the attribute deck without the top card
     *
     * @return a new CardState created using the deck of this CardState without the top Card
     */
    public CardState withoutTopDeckCard(){
        Preconditions.checkArgument(!deck.isEmpty());
        return new CardState(deck.withoutTopCard());
    }


    /**
     *
     *
     * @param rng
     * @return
     */
    public CardState withDeckRecreatedFromDiscards(Random rng){
        Preconditions.checkArgument(deck.isEmpty());
        return new CardState(Deck.of(discardCards,rng));
    }



}

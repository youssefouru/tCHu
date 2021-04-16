package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

/**
 * A CardState : this class represent the private part of a CardState
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public final class CardState extends PublicCardState {
    private final Deck<Card> deck;
    private final SortedBag<Card> discardCards;


    /**
     * Constructor of CardState
     *
     * @param deck         (Deck<Card>) : the deck of cards
     * @param faceUpCards  (List<Card>) : the List of faceUpCards of the CardState
     * @param discardCards (SortedBag<Card>): these are the List of cards that are in initially in the discard
     */
    private CardState(Deck<Card> deck, List<Card> faceUpCards, SortedBag<Card> discardCards) {
        super(faceUpCards, deck.size(), discardCards.size());
        this.deck = deck;
        this.discardCards = discardCards;

    }

    /**
     * this method creates a cardState and returns it
     *
     * @param deck (Deck<Card>) : this is the deck from which we will take cards
     * @return (CardState) : a card state with the deck in parameter as a deck
     */
    public static CardState of(Deck<Card> deck) {
        Preconditions.checkArgument(deck.size()>=Constants.FACE_UP_CARDS_COUNT);
        return new CardState(deck.withoutTopCards(Constants.FACE_UP_CARDS_COUNT), deck.topCards(Constants.FACE_UP_CARDS_COUNT).toList(), SortedBag.of());
    }

    /**
     * this method returns a Card State without the faceUpCard on the slot position
     *
     * @param slot (int) : it's the index of the card that we remove from the faceUpCards
     * @return (CardState) : a new CardState with the slot faceUpCard removed and replaced by the top deck Card
     */
    public CardState withDrawnFaceUpCard(int slot) {
        Objects.checkIndex(slot, Constants.FACE_UP_CARDS_COUNT);
        Preconditions.checkArgument(!deck.isEmpty());
        List<Card> myList = new ArrayList<>(faceUpCards());
        myList.set(slot, deck.topCard());
        return new CardState(deck.withoutTopCard(), myList, discardCards);

    }


    /**
     * this method returns the top card of the deck
     *
     * @return (Card) : the deck's top card
     */
    public Card topDeckCard() {
        Preconditions.checkArgument(!deck.isEmpty());
        return deck.topCard();
    }


    /**
     * this method creates a new deck with the attribute deck without the top card
     *
     * @return (CardState) : a new CardState created using the deck of this CardState without the top Card
     */
    public CardState withoutTopDeckCard() {
        Preconditions.checkArgument(!deck.isEmpty());
        return new CardState(deck.withoutTopCard(), faceUpCards(), discardCards);
    }


    /**
     * this method create a new CardState with the discard cards as a deck
     *
     * @param rng (Random) : the object that we will shuffle with
     * @return (CardState) : a deck recreated from the discard and shuffled
     */
    public CardState withDeckRecreatedFromDiscards(Random rng) {
        Preconditions.checkArgument(deck.isEmpty());
        return new CardState(Deck.of(discardCards, rng), faceUpCards(), SortedBag.of());
    }


    /**
     * this method creates a CardState with a the discard Cards of this CardStat to which we add the additional Discards in parameter
     *
     * @param additionalDiscards (SortedBag<Card>) : the cards that we want to add
     * @return (CardState) : a CardState with the discard Cards of this CardStat to which we add the additional Discards in parameter
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards) {
        return new CardState(this.deck, faceUpCards(), discardCards.union(additionalDiscards));

    }

}

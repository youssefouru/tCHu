package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

/**
 * A CardState
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
        super(List.copyOf(faceUpCards), deck.size(), discardCards.size());
        this.deck = deck;
        this.discardCards = discardCards;

    }

    /**
     * this method creates a cardState and returns it
     *
     * @param deck (Deck<Card>) : this is the deck from which we will take cards
     * @return a card state
     */
    public static CardState of(Deck<Card> deck) {
        Preconditions.checkArgument(deck.size()>=5);
        return new CardState(deck.withoutTopCards(5), deck.topCards(5).toList(), SortedBag.of());
    }

    /**
     * this method returns a Card State without the faceUpCard on the slot position
     *
     * @param slot (int) : it's the index of the card that we remove from the faceUpCards
     * @return a new CardState with
     */
    public CardState withDrawnFaceUpCard(int slot) {
        Objects.checkIndex(slot, 5);
        Preconditions.checkArgument(!deck.isEmpty());
        List<Card> myList = new ArrayList<>(super.faceUpCards());
        myList.set(slot, deck.topCard());
        return new CardState(deck.withoutTopCard(), myList, discardCards);

    }


    /**
     * this method returns the top card of the deck
     *
     * @return the deck's top card
     */
    public Card topDeckCard() {
        Preconditions.checkArgument(!deck.isEmpty());
        return deck.topCard();
    }


    /**
     * this method creates a new deck with the attribute deck without the top card
     *
     * @return a new CardState created using the deck of this CardState without the top Card
     */
    public CardState withoutTopDeckCard() {
        Preconditions.checkArgument(!deck.isEmpty());
        return new CardState(deck.withoutTopCard(), super.faceUpCards(), discardCards);
    }


    /**
     * this method create a new CardState with the discard cards as a deck
     *
     * @param rng (Random) : the object that we will shuffle with
     * @return a deck recreated from the discard and shuffled
     */
    public CardState withDeckRecreatedFromDiscards(Random rng) {
        Preconditions.checkArgument(deck.isEmpty());
        return new CardState(Deck.of(discardCards, rng), super.faceUpCards(), SortedBag.of());
    }


    /**
     * this method creates a CardState with a the discard Cards of this CardStat to which we add the additional Discards in parameter
     *
     * @param additionalDiscards (SortedBag<Card>) : the cards that we want to add
     * @return a CardState with the discard Cards of this CardStat to which we add the additional Discards in parameter
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards) {
        return new CardState(this.deck, super.faceUpCards(), discardCards.union(additionalDiscards));

    }

}

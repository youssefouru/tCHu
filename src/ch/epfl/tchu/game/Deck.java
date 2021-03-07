package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * A Deck
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public final class Deck<C extends Comparable<C>> {
    private final SortedBag<C> cards;

    /**
     * private constructor of Deck
     *
     * @param cards (SortedBag<C>) : the  Cs that are contained by the Deck
     */
    private Deck(SortedBag<C> cards) {
        this.cards = SortedBag.of(cards);
    }

    /**
     * this method creat a C's deck
     *
     * @param cards (SortedBag<C>) : the set of C that will be used as parameter to create a Deck
     * @param rng   (Random)       : the object that will be used to shuffle our Cs
     * @param <C>   : the type of the objects the type of object collected in the deck
     * @return deck (Deck<C>) : a deck wich has the SortedBag cards shuffled as parameter
     */
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng) {
        List<C> c = cards.toList();
        Collections.shuffle(c, rng);
        return new Deck<>(SortedBag.of(c));
    }

    /**
     * this method creat a C's deck
     *
     * @param cards (SortedBag<C>) : the set of C that will be used as parameter to create a Deck
     * @param <C>   : the type of the objects the type of object collected in the deck
     * @return deck (Deck<C>) : a deck which has the SortedBag cards shuffled as parameter
     */
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards) {
        return new Deck<>(cards);
    }

    /**
     * this method returns the number of cards which are in the deck
     *
     * @return size (int) : the size of the attributes card
     */
    public int size() {
        return this.cards.size();
    }

    /**
     * this method return true if there is no card in the deck
     *
     * @return aboolean (boolean) : it retruns true iff the size of the attribute cards is 0
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * this method returns the card in the top of the deck without deleting it from the deck
     *
     * @return card (C) : the last element of the attribute card
     */
    public C topCard() {
        Preconditions.checkArgument(!isEmpty());
        return cards.get(0);
    }


    /**
     * this methode returns a multiset of the count topCards
     *
     * @param count (int) : number of the top cards that we want from the top of the deck
     * @return sortedBag (SortedBag<C>) : sorted bag of count topCards from this deck
     */
    public SortedBag<C> topCards(int count) {
        Preconditions.checkArgument(count >= 0 && count <= size());
        List<C> myList = new LinkedList<>();
        Deck< C> myDeck = new Deck<>(this.cards);
        for (int i = 0; i < count; ++i) {
            myList.add(myDeck.topCard());
            myDeck = myDeck.withoutTopCard();
        }
        return SortedBag.of(myList);
    }


    /**
     * return a new deck without the top card
     *
     * @return deck (Deck<C>):  a new Deck composed with the same cards without the top Card
     */
    public Deck<C> withoutTopCard() {
        Preconditions.checkArgument(!isEmpty());
        List<C> myList = SortedBag.of(cards).toList();
        myList.remove(0);
        return new Deck<>(SortedBag.of(myList));
    }

    /**
     * this method return a new deck without the count topCards
     *
     * @param count (int) : number of top cards that we remove from deck
     * @return deck (Deck<C>) : a new deck composed of the cards of this deck without the count first cards
     */
    public Deck<C> withoutTopCards(int count) {
        Preconditions.checkArgument(count >= 0 && count <= size());
        Deck<C> myDeck = new Deck<>(cards);
        for(int i = 0 ; i<count;++i){
            myDeck = myDeck.withoutTopCard();
        }
        return myDeck;
    }

}

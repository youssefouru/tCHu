package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

/**
 * A Deck
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public final class Deck<C extends Comparable<C>> {
    private final List<C> cards;

    /**
     * private constructor of Deck
     *
     * @param cards (SortedBag<C>) : the  Cs that are contained by the Deck
     */
    private Deck(List<C> cards) {
        this.cards = List.copyOf(cards);
    }

    /**
     * this method creat a C's deck
     *
     * @param cards (SortedBag<C>) : the set of C that will be used as parameter to create a Deck
     * @param rng   (Random)       : the object that will be used to shuffle our Cs
     * @param <C>   : the type of the objects the type of object collected in the deck
     * @return deck (Deck<C>) : a deck which has the SortedBag cards shuffled as parameter
     */
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng) {
        List<C> c = cards.toList();
        Collections.shuffle(c, rng);
        return new Deck<>(c);
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
     * @return aboolean (boolean) : it returns true iff the size of the attribute cards is 0
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
        List<C> myList = new ArrayList<>(cards.subList(0, count));
        return SortedBag.of(myList);
    }


    /**
     * return a new deck without the top card
     *
     * @return deck (Deck<C>):  a new Deck composed with the same cards without the top Card
     */
    public Deck<C> withoutTopCard() {
        Preconditions.checkArgument(!isEmpty());
        List<C> myList = new ArrayList<>(cards);
        myList.remove(0);
        return new Deck<>(myList);
    }

    /**
     * this method return a new deck without the count topCards
     *
     * @param count (int) : number of top cards that we remove from deck
     * @return deck (Deck<C>) : a new deck composed of the cards of this deck without the count first cards
     */
    public Deck<C> withoutTopCards(int count) {
        Preconditions.checkArgument(count >= 0 && count <= size());
        List<C> c = new LinkedList<>(cards);
        Deck<C> deck = new Deck<>(cards);
        for(int i = 0 ; i <count ; ++i){
            deck = deck.withoutTopCard();
        }
        return deck;
    }

}
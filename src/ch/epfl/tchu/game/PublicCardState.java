package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.Objects;

/**
 * PublicCardState : this class represents the public part of a cardState
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */

public class PublicCardState {
    private final List<Card> faceUpCards;
    private final int deckSize;
    private final int discardsSize;


    /**
     * Constructor of PublicCardState
     *
     * @param faceUpCards  (List<Card>): a list of cards that are face up
     * @param deckSize     (int)       : the total number of card in the deck
     * @param discardsSize (int)       : the number of cards in the discard
     */
    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize) {
        Preconditions.checkArgument(faceUpCards.size() == Constants.FACE_UP_CARDS_COUNT && (discardsSize >= 0 && deckSize>=0) );
        this.faceUpCards = List.copyOf(faceUpCards);
        this.deckSize = deckSize;
        this.discardsSize = discardsSize;
    }

    /**
     * this method returns the total number of cards that are not in the hand of the player
     *
     * @return the total number of cards
     */
    public int totalSize() {
        return faceUpCards.size() + deckSize + discardsSize;
    }

    /**
     * return the faceUpCards
     *
     * @return the attribute faceUpCards
     */
    public List<Card> faceUpCards() {
        return faceUpCards;
    }

    /**
     * this method returns the slot faceUpCard
     *
     * @param slot (int) : this parameter is the index of card that we want
     * @return the card the slot face Up card
     */
    public Card faceUpCard(int slot) {
        Objects.checkIndex(slot, Constants.FACE_UP_CARDS_COUNT);
        return faceUpCards.get(slot);
    }

    /**
     * return the size of the deck
     *
     * @return the attribute deckSize
     */
    public int deckSize() {
        return deckSize;
    }

    /**
     * this method check if the Deck is Empty
     *
     * @return a boolean which is true if the DeckSize is equals to 0
     */
    public boolean isDeckEmpty() {
        return deckSize == 0;
    }

    /**
     * returns the discardsSize
     *
     * @return the attribute discardsSize
     */
    public int discardsSize() {
        return discardsSize;
    }

}

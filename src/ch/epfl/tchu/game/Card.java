package ch.epfl.tchu.game;

import java.util.List;

/**
 * Cards
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public enum Card {
    BLACK(Color.BLACK),
    VIOLET(Color.VIOLET),
    BLUE(Color.BLUE),
    GREEN(Color.GREEN),
    YELLOW(Color.YELLOW),
    ORANGE(Color.ORANGE),
    RED(Color.RED),
    WHITE(Color.WHITE),
    LOCOMOTIVE(null);


    /**
     * COUNT (int) :  represent the number of all the values of this enum type
     */
    public final static int COUNT = Card.values().length;
    /**
     * ALL (List<Card>) : contains all the element of this enum type
     */
    public final static List<Card> ALL = List.of(Card.values());
    /**
     * CARS (List<Card>) : contains the element of this enum type from BLACK to WHITE
     */
    public final static List<Card> CARS = List.of(BLACK, VIOLET, BLUE, GREEN, YELLOW, ORANGE, RED, WHITE);
    private final Color color;

    /**
     * contructor of the Card enum type
     *
     * @param color (Color) : color of the card
     */
    Card(Color color) {
        this.color = color;
    }

    /**
     * this method creat a card of a color that you put in paramter
     *
     * @param color (Color) : color of the card
     * @return card (Card) : return the card with the color that has been choosen in parameter
     */
    public static Card of(Color color) {
        for(Card card : ALL){
            if(card.color.equals(color))
                return card;
        }
        return null;
    }

    /**
     * this methode retrun the color of the card
     *
     * @return color (Color) : the color of the card
     */
    public Color color() {
        return this.color;
    }
}

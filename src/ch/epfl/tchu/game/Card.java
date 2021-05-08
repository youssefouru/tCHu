package ch.epfl.tchu.game;

import java.util.List;

/**
 * Cards : this enum type represent the cards playable in the game
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
    public final static int COUNT = values().length;
    /**
     * ALL (List<Card>) : contains all the element of this enum type
     */
    public final static List<Card> ALL = List.of(values());
    /**
     * CARS (List<Card>) : contains the element of this enum type from BLACK to WHITE
     */
    public final static List<Card> CARS = List.of(BLACK, VIOLET, BLUE, GREEN, YELLOW, ORANGE, RED, WHITE);
    private final Color color;

    /**
     * constructor of the Card enum type
     *
     * @param color (Color) : color of the card
     */
    Card(Color color) {
        this.color = color;
    }

    /**
     * this method creat a card of a color that we have in parameter
     *
     * @param color (Color) : color of the card
     * @return (Card) : return the card with the color that has been chosen in parameter
     */
    public static Card of(Color color) {
        switch (color){
            case BLACK:
                return Card.BLACK;
            case VIOLET:
                return Card.VIOLET;
            case BLUE:
                return Card.BLUE;
            case GREEN :
                return Card.GREEN;
            case YELLOW:
                return Card.YELLOW;
            case ORANGE:
                return Card.ORANGE;
            case RED:
                return Card.RED;
            case WHITE:
                return Card.WHITE;
            default:
                throw new Error();
        }
    }

    /**
     * this methode returns the color of the card
     *
     * @return (Color) : the color of the card
     */
    public Color color() {
        return this.color;
    }
}

package ch.epfl.tchu.game;

import java.util.List;

//Amine Youssef Louis Barinka
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


    // COUNT represent the number of all the values of this enum type
    public final static int COUNT = 9;
    // ALL contains all the element of this enum type
    public final static List<Card> ALL = List.of(Card.values());
    //CARS contains the element of this enum type from BLACK to WHITE
    public final static List<Card> CARS = List.of(BLACK,VIOLET,BLUE,GREEN,YELLOW, ORANGE, RED, WHITE);
    //This represent the color of the enum type
    private final Color color;

    /**
     * contructor of the Card enum type
     * @param color (Color) : color of the card
     */
    Card(Color color){
        this.color =color;
    }

    /**
     * this method creat a card of a color that you put in paramter
     * @param color (Color) : color of the card
     * @return card (Card) : return the card with the color that has been choosen in parameter
     */
    public static Card of(Color color){
        switch (color){
            case BLACK:
                return Card.BLACK;
            case VIOLET:
                return Card.VIOLET;
            case BLUE:
                return Card.BLUE;
            case GREEN:
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
                return null;
        }

    }

    /**
     * this methode retrun the color of the card
     * @return color (Color) : the color of the card
     */
    public Color color(){
        return this.color;
    }
}

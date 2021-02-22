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
    public final static int COUNT = 9;
    public final static List<Card> ALL = List.of(Card.values());
    public final static List<Card> CARS = List.of(BLACK,VIOLET,BLUE,GREEN,YELLOW, ORANGE, RED, WHITE);
    private final Color color;
    Card(Color color){
        this.color =color;
    }

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

    public Color color(){
        return this.color;
    }
}

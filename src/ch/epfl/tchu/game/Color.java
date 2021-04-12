package ch.epfl.tchu.game;

import java.util.List;
/**
 * Colors : this enum type represent the colors of the cards of the Game
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public enum Color {
    BLACK,
    VIOLET,
    BLUE,
    GREEN,
    YELLOW,
    ORANGE,
    RED,
    WHITE;


    /**
     * COUNT (int) : represent the number of all the values of this enum type
     */
    public final static int COUNT = Color.values().length;

    /**
     * ALL (List<Color>) : contains all the element of this enum type
     */
    public final static List<Color> ALL = List.of(Color.values());

}

package ch.epfl.tchu.game;

import java.util.List;

//Amine Youssef Louis Barinka
public enum Color {
    BLACK,
    VIOLET,
    BLUE,
    GREEN,
    YELLOW,
    ORANGE,
    RED,
    WHITE;
    // COUNT represent the number of all the values of this enum type
    public final static int COUNT = 8;
    // ALL contains all the element of this enum type
    public final static List<Color> ALL = List.of(Color.values());

}

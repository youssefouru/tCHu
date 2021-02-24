package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ColorTest {

    @Test
    void checkTheElements(){
        var expected = new Color[]{Color.BLACK,Color.VIOLET,Color.BLUE,Color.GREEN,Color.YELLOW,Color.ORANGE,Color.RED,Color.WHITE};
        assertArrayEquals(expected,Color.values());
    }

    @Test
    void checkTheALLlist(){
        var excpected = new Color[]{Color.BLACK,Color.VIOLET,Color.BLUE,Color.GREEN,Color.YELLOW,Color.ORANGE,Color.RED,Color.WHITE};
        var excpectedList = List.of(excpected);
        assertEquals(excpectedList,Color.ALL);
    }
    @Test
    void checkTheCOUNT(){
        assertEquals(Color.COUNT,8);
    }
}

package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerIdTest {



    @Test
    void testnext(){
        PlayerId playerId1 = PlayerId.PLAYER_1;
        PlayerId playerId2 = PlayerId.PLAYER_2;
        assertEquals(playerId1,playerId2.next());
        assertEquals(playerId2,playerId1.next());
    }
}

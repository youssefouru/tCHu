package ch.epfl.tCHu;

import ch.epfl.tchu.Precodition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PreconditonTest {
    @Test
    void checkIfCheckArgumentThrowsTheRightException(){
        assertThrows(IllegalArgumentException.class, () ->{
            Precodition.checkArgument(false);
        });
    }
}


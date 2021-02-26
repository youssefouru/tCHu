package ch.epfl.tchu;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class PreconditonsTest {
    @Test
    void checkIfCheckArgumentThrowsTheRightException() {
        assertThrows(IllegalArgumentException.class, () -> {
            Preconditions.checkArgument(false);
        });
    }
}


package ch.epfl.tchu.bonus;

import ch.epfl.tchu.game.PublicGameState;

public interface Spectator {
    void receiveInfo(String info);

    void updateState(PublicGameState gameState);
}

package ch.epfl.tchu.bonus;

import ch.epfl.tchu.game.PlayerId;

import java.util.Map;

import static javafx.application.Platform.isFxApplicationThread;

public class GraphicalSpectator {
    private final Map<PlayerId,String> playerNames;

    /**
     * Constructor of the GraphicalSpectator.
     * @param spectatorId (SpectatorId) : the id of this spectator
     * @param playerNames (Map< PlayerId,String >) : the names of the players
     */
    public GraphicalSpectator(SpectatorId spectatorId, Map<PlayerId,String> playerNames){
        assert isFxApplicationThread();
        this.playerNames = playerNames;
    }
}

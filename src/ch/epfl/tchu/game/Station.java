package ch.epfl.tchu.game;
import ch.epfl.tchu.Precodition;
import org.junit.jupiter.api.Test;

public final class Station {
    private int id;
    private String name;
    public Station(int id,String name){
        Precodition.checkArgument(id>=0);
        this.id=id;
        this.name=name;

    }

    /**
     * return the identification number of the station
     * @return id (int)
     */
    public int id(){
        return id;
    }

    public String name(){
        return this.name;
    }
}

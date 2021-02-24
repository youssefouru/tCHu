package ch.epfl.tchu;

public final class Precodition {

    private Precodition(){
    }

    public static void checkArgument(boolean argument){
        if(!argument){
            throw new IllegalArgumentException();
        }
    }
}

package ch.epfl.tchu;

public final class Precondition {

    private Precondition(){
    }

    public static void checkArgument(boolean argument){
        if(!argument){
            throw new IllegalArgumentException();
        }
    }
}

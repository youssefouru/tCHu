package ch.epfl.tchu;

/**
 * The Preconditions
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */

public final class Precondition {

    private Precondition(){
    }

    public static void checkArgument(boolean argument){
        if(!argument){
            throw new IllegalArgumentException();
        }
    }
}

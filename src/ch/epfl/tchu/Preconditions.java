package ch.epfl.tchu;

/**
 * The Preconditions
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */

public final class Preconditions {

    private Preconditions(){
    }

    /**
     * Verify if the argument is true else it throws IllegalArgumentException exception
     *
     * @param argument (boolean) : the argument wich will be checked
     */
    public static void checkArgument(boolean argument){
        if(!argument){
            throw new IllegalArgumentException();
        }
    }
}

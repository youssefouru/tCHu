package ch.epfl.tchu;

//Amine Youssef Louis Barinka
public final class Precodition {

    private Precodition(){
    }

    /**
     * this method check if the argument is true else it throws the IllegalArgumentException
     * @param argument (Boolean) : this is the boolean we want to check
     */
    public static void checkArgument(boolean argument){
        if(!argument){
            throw new IllegalArgumentException();
        }
    }
}

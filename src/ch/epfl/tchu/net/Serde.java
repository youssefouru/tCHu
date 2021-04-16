package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * Serde : this interface is used to serialize and deserialize objects of type T
 *
 * @param <T> : the type of the objects serialized and deserialized by this interface
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public interface Serde<T> {
    /**
     * this method returns a serde that use the serFunction to serialize the objects of C and the deFunction to deserialize the String into an object
     *
     * @param serFunction (Function<C, String>) : the function used to serialize the object
     * @param deFunction (Function<String, C>) : the function used to deserialize the string
     * @param <C> : the type parameter of the serde
     * @return (Serde<C>) : a serde that serialize an object
     */
    static <C> Serde<C> of(Function<C, String> serFunction, Function<String, C> deFunction) {
        return new Serde<>() {
            @Override
            public String serialize(C object) {
                return serFunction.apply(object);
            }

            @Override
            public C deserialize(String name) {
                return deFunction.apply(name);
            }
        };
    }

    /**
     * @param list (List<C>) : the list of type C values
     * @param <C>  : the type parameter of the serde
     * @return (Serde < C >) : returns a serde that can serialize and deserialize an element of the list in parameter
     */
    static <C> Serde<C> oneOf(List<C> list) {
        Function<C, String> serFunction =(c) -> c == null ?"null":Integer.toString(list.indexOf(c));
        Function<String, C> deFunction = (s) ->  s.equals("null")?null:list.get(Integer.parseInt(s));
        return of(serFunction, deFunction);
    }

    /**
     * This method returns a serde that can serialize and deserialize a list of Cs
     *
     * @param serde     (Serde<C>) : the serde used to serialize each object a list
     * @param character (char) : the separation character we will use to separate the elements of a list
     * @param <C>       : the type parameter of the serde
     * @return (Serde<List<C>>) : a serde that can serialize and deserialize a list of Cs with a Cs serde
     */
    static <C> Serde<List<C>> listOf(Serde<C> serde, char character) {
        return new Serde<>() {
            @Override
            public String serialize(List<C> list) {
                List<String> result = list.stream().
                                      map(serde::serialize).
                                      collect(Collectors.toList());

                return String.join(String.valueOf(character),result);
            }

            @Override
            public List<C> deserialize(String name) {
                if(name.isEmpty())
                    return new ArrayList<>();
                String[] strings = name.
                                        split(Pattern.quote(String.valueOf(character)),-1);
                return Arrays.stream(strings).
                       map(serde::deserialize).
                       collect(Collectors.toList());
            }
        };
    }

    /**
     * This method returns a serde that can serialize and deserialize a SortedBag of Cs
     *
     * @param serde     (Serde<C>) : the serde used to serialize each object a SortedBag
     * @param character (char) : the separation character we will use to separate the elements of a list
     * @param <C>       : the type parameter of the serde
     * @return (Serde<List<C>>) : a serde that can serialize and deserialize a SortedBag of Cs with a Cs serde
     */
    static <C extends Comparable<C>> Serde<SortedBag<C>> bagOf(Serde<C> serde,char character){
        Serde<List<C>> listSerde = Serde.listOf(serde,character);

        return new Serde<>() {
            @Override
            public String serialize(SortedBag<C> bag) {
                return listSerde.serialize(bag.toList());
            }

            @Override
            public SortedBag<C> deserialize(String name) {
                return SortedBag.of(listSerde.deserialize(name));
            }
        };
    }


    /**
     * this method serialize the object in parameter and give it's string representation
     *
     * @param t (T) : the object we want to serialize
     * @return (String) : the string representation  of the serialized object e
     */
    String serialize(T t);

    /**
     * this method give us the object T represented by the string in parameter
     *
     * @param name (String) : the string representation of a serialized object
     * @return (T) : the object represented by the string in parameter
     */
    T deserialize(String name);

}




